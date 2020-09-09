package com.pse.peer2learn.ui.viewmodels

import android.content.Context
import android.widget.Toast
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.firebase.firestore.FirebaseFirestore
import com.pse.peer2learn.models.Student
import com.pse.peer2learn.models.StudyGroup
import com.pse.peer2learn.repositories.GroupRepository
import com.pse.peer2learn.repositories.MemberRepository
import com.pse.peer2learn.repositories.PrivateGroupRepository
import com.pse.peer2learn.repositories.UserRepository
import com.pse.peer2learn.ui.activities.interfaces.ICreateGroupListener
import com.pse.peer2learn.utils.RepositoryProgress
import io.mockk.*
import org.junit.*
import org.junit.Assert.*
import org.junit.rules.TestRule
import org.mockito.ArgumentMatchers.anyInt
import org.mockito.ArgumentMatchers.anyString
import java.util.*
import kotlin.collections.ArrayList


class CreateGroupViewModelTest {

    @get:Rule
    var rule: TestRule = InstantTaskExecutorRule()

    private lateinit var createGroupViewModel: CreateGroupViewModel
    private lateinit var userRepository: UserRepository
    private lateinit var groupRepository: GroupRepository
    private lateinit var memberRepository: MemberRepository
    private lateinit var privateGroupRepository: PrivateGroupRepository
    private val toast: Toast = mockk(relaxed = true)
    private val context: Context = mockk(relaxed = true)

    @Before
    fun setUp() {
        mockkStatic(FirebaseFirestore::class)
        mockkStatic(Toast::class)
        mockkStatic(UUID::class)
        every { Toast.makeText(context, anyString(), anyInt()) } returns toast
        every { FirebaseFirestore.getInstance() } returns mockk(relaxed = true)
        every { UUID.randomUUID().toString() } returns "12345678"
        groupRepository = mockk(relaxed = true)
        memberRepository= mockk(relaxed = true)
        userRepository= mockk(relaxed = true)
        privateGroupRepository = mockk(relaxed = true)
        createGroupViewModel = CreateGroupViewModel(groupRepository, userRepository, privateGroupRepository, memberRepository)
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `check when createPublicGroup is called, groupRepository will trigger create method`() {
        val group: StudyGroup = mockk(relaxed = true)
        createGroupViewModel.createPublicGroup(group)
        verify (exactly = 1) { groupRepository.create(group) }
    }


    @Test
    fun `assert that observeCreateGroup is updating member of group when RepositoryProgress is successfull`() {
        val student: Student = mockk(relaxed = true)
        val groupId = "NOT_NULL"
        createGroupViewModel.observeCreateGroup(RepositoryProgress.SUCCESS, student, groupId, context)
        verify (exactly = 1) { memberRepository.update(any()) }
    }

    @Test
    fun `assert that observeCreateGroup isn't updating member of group when RepositoryProgress is successfull and group Id is null`() {
        val student: Student = mockk(relaxed = true)
        val groupId = null
        createGroupViewModel.observeCreateGroup(RepositoryProgress.SUCCESS, student, groupId, context)
        verify (exactly = 0) { memberRepository.update(any()) }
    }

    @Test
    fun `assert that observeCreateGroup is printing a toast message if RepositoryProgress has failed`() {
        createGroupViewModel.observeCreateGroup(RepositoryProgress.FAILED, mockk(relaxed = true), anyString(), context)
        verify (exactly = 0) { memberRepository.update(any()) }
        verify (exactly = 1) { toast.show() }
    }

    @Test
    fun `test create private group`() {
        val group: StudyGroup = mockk(relaxed = true)

        createGroupViewModel.createPrivateGroup(group)
        verify { privateGroupRepository.create(group) }
        assertEquals(createGroupViewModel.groupIdLiveData.value, "")
    }

    @Test
    fun `assert that observeUpdateMember is adding group to user repository when RepositoryProgress is successfull`() {
        val student: Student = mockk(relaxed = true)
        val listOfGroups: ArrayList<String> = mockk(relaxed = true)
        student.studyGroupList = listOfGroups
        val groupId = "Group_ID"
        createGroupViewModel.observeUpdateMember(RepositoryProgress.SUCCESS, student, groupId, context)

        verify { student.studyGroupList.add(groupId) }
        verify { userRepository.update(student) }
    }

    @Test
    fun `assert that observeUpdateMember is printing a toast message if RepositoryProgress has failed`() {
        val student: Student = mockk(relaxed = true)
        val listOfGroups: ArrayList<String> = mockk(relaxed = true)
        student.studyGroupList = listOfGroups
        val groupId = "Group_ID"

        createGroupViewModel.observeUpdateMember(RepositoryProgress.FAILED, student, groupId, context)

        verify (exactly = 0) { student.studyGroupList.add(groupId) }
        verify (exactly = 0) { userRepository.update(student) }
        verify (exactly = 1) { toast.show() }
    }

    @Test
    fun `assert that observeUpdateUserGroups is showing PrivateGroupCreatedFragment when a private group is created`() {
        val createGroupListener: ICreateGroupListener = mockk(relaxed = true)
        createGroupViewModel.privatAccessCode = "NOT_EMPTY"

        createGroupViewModel.observeUpdateUserGroups(RepositoryProgress.SUCCESS, createGroupListener, context)

        verify (exactly = 1) { createGroupListener.showPrivateGroupCreatedFragment() }
        assertEquals(createGroupViewModel.privatAccessCode, "")

    }

    @Test
    fun `assert that observeUpdateUserGroups is not showing PrivateGroupCreatedFragment when a public group is created and user is redirected to HomeActivity`() {
        val createGroupListener: ICreateGroupListener = mockk(relaxed = true)
        createGroupViewModel.privatAccessCode = ""

        createGroupViewModel.observeUpdateUserGroups(RepositoryProgress.SUCCESS, createGroupListener, context)

        verify (exactly = 0) { createGroupListener.showPrivateGroupCreatedFragment() }
        verify (exactly = 1) { createGroupListener.navigateHome() }
        verify (exactly = 1) { toast.show() }
    }

    @Test
    fun `assert that observeUpdateUserGroups is showing Toast message when creation of group fails`() {
        val createGroupListener: ICreateGroupListener = mockk(relaxed = true)
        createGroupViewModel.observeUpdateUserGroups(RepositoryProgress.FAILED, createGroupListener, context)

        verify (exactly = 0) { createGroupListener.showPrivateGroupCreatedFragment() }
        verify (exactly = 0) { createGroupListener.navigateHome() }
        verify (exactly = 1) { toast.show() }
    }

    @Test
    fun `assert that observeAccessCode is setting access code when code is unique`() {
        val createGroupListener: ICreateGroupListener = mockk(relaxed = true)
        createGroupViewModel.observeAccessCode(RepositoryProgress.SUCCESS, createGroupListener)

        verify (exactly = 1) { createGroupListener.setAccessCode() }
    }

    @Test
    fun `assert that observeAccessCode is generating another access code when old generated code isn't unique`() {
        val createGroupListener: ICreateGroupListener = mockk(relaxed = true)

        createGroupViewModel.observeAccessCode(RepositoryProgress.FAILED, createGroupListener)

        verify (exactly = 0) { createGroupListener.setAccessCode() }
        assertEquals(createGroupViewModel.privatAccessCode, "12345678")
    }

    @Test
    fun `assert that validateFormValues is returning false if title is empty`() {
        val returnValue = createGroupViewModel.validateFormValues("", "DESC", 1, "LANGUAGE", context)
        verify { toast.show() }
        assertFalse(returnValue)
    }

    @Test
    fun `assert that validateFormValues is returning false if description is empty`() {
        val returnValue = createGroupViewModel.validateFormValues("TITLE", "", 1, "LANGUAGE", context)
        verify { toast.show() }
        assertFalse(returnValue)
    }

    @Test
    fun `assert that validateFormValues is returning false if language is empty`() {
        val returnValue = createGroupViewModel.validateFormValues("TITLE", "DESC", 1, "", context)
        verify { toast.show() }
        assertFalse(returnValue)
    }

    @Test
    fun `assert that validateFormValues is returning false if numberOfPerson is 0`() {
        val returnValue = createGroupViewModel.validateFormValues("TITLE", "DESC", 0, "LANGUAGE", context)
        verify { toast.show() }
        assertFalse(returnValue)
    }

    @Test
    fun `assert that validateFormValues is returning true if everything is valid`() {
        val returnValue = createGroupViewModel.validateFormValues("TITLE", "DESC", 2, "LANGUAGE", context)
        verify (exactly = 0){ toast.show() }
        assertTrue(returnValue)
    }
}