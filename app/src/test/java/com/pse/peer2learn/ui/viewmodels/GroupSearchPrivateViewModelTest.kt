package com.pse.peer2learn.ui.viewmodels

import android.content.Context
import android.widget.Toast
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.firebase.firestore.FirebaseFirestore
import com.pse.peer2learn.models.Student
import com.pse.peer2learn.models.StudyGroup
import com.pse.peer2learn.repositories.MemberRepository
import com.pse.peer2learn.repositories.PrivateGroupRepository
import com.pse.peer2learn.repositories.UserRepository
import com.pse.peer2learn.ui.activities.interfaces.IGroupSearchPrivateListener
import com.pse.peer2learn.utils.RepositoryProgress
import io.mockk.*
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import org.mockito.ArgumentMatchers

class GroupSearchPrivateViewModelTest {

    @get:Rule
    var rule: TestRule = InstantTaskExecutorRule()

    private lateinit var groupSearchPrivateViewModel: GroupSearchPrivateViewModel
    private lateinit var memberRepository: MemberRepository
    private lateinit var userRepository: UserRepository
    private lateinit var privateGroupRepository: PrivateGroupRepository
    private val groupSearchPrivateListener: IGroupSearchPrivateListener = mockk(relaxed = true)
    private val context: Context = mockk(relaxed = true)
    private val toast: Toast = mockk(relaxed = true)

    @Before
    fun setUp() {
        mockkStatic(FirebaseFirestore::class)
        mockkStatic(Toast::class)
        every { Toast.makeText(context, ArgumentMatchers.anyString(), ArgumentMatchers.anyInt()) } returns toast
        every { FirebaseFirestore.getInstance() } returns mockk(relaxed = true)
        memberRepository = mockk(relaxed = true)
        userRepository = mockk(relaxed = true)
        privateGroupRepository = mockk(relaxed = true)
        groupSearchPrivateViewModel = GroupSearchPrivateViewModel(userRepository, memberRepository, privateGroupRepository)
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun findPrivateGroupTest() {
        val accessCode = "Access code"

        groupSearchPrivateViewModel.findPrivateGroup(accessCode)

        verify { privateGroupRepository.retrieve(accessCode) }
    }

    @Test
    fun `assert that joinGroup is updating memberRepository, userRepository and privateGroupRepository when the user join a new group`() {
        val student: Student = spyk()
        val groupId = "Group ID"
        student.studyGroupList = arrayListOf()

        groupSearchPrivateViewModel.joinGroup(student, groupId)

        verify { memberRepository.update(any()) }
        verify { userRepository.update(any()) }
        assertTrue(student.studyGroupList.contains(groupId))
        verify { privateGroupRepository.incrementNumberOfMember(groupId) }
        assertFalse(groupSearchPrivateViewModel.groupAlreadyJoined.value!!)
    }

    @Test
    fun `assert that joinGroup is updating memberRepository, userRepository and privateGroupRepository when the usesr is already member of the group`() {
        val student: Student = spyk()
        val groupId = "Group ID"
        student.studyGroupList = arrayListOf(groupId)

        groupSearchPrivateViewModel.joinGroup(student, groupId)

        verify(exactly = 0) { memberRepository.update(any()) }
        verify(exactly = 0) { userRepository.update(any()) }
        verify(exactly = 0) { privateGroupRepository.incrementNumberOfMember(groupId) }
        assertTrue(groupSearchPrivateViewModel.groupAlreadyJoined.value!!)
    }

    @Test
    fun `assert that observeRetrieveGroup is calling joinGroup when the retrieved group isn't null`() {
        val studyGroup: StudyGroup = mockk(relaxed = true)

        groupSearchPrivateViewModel.observeRetrieveGroup(studyGroup, groupSearchPrivateListener, context)

        verify { groupSearchPrivateListener.joinGroup() }
    }

    @Test
    fun `assert that observeRetrieveGroup is printing an error message when the retrieved group is null`() {
        val studyGroup: StudyGroup? = null

        groupSearchPrivateViewModel.observeRetrieveGroup(studyGroup, groupSearchPrivateListener, context)

        verify(exactly = 0) { groupSearchPrivateListener.joinGroup() }
        verify { toast.show() }
    }

    @Test
    fun `assert observeUpdateUser is printing a toast message when the group is joined successfully`() {
        groupSearchPrivateViewModel.observeUpdateUser(RepositoryProgress.SUCCESS, context)

        verify { toast.show() }
    }

    @Test
    fun `assert observeGroupAlreadyJoined is printing a toast when the group is already joined`() {
        groupSearchPrivateViewModel.observeGroupAlreadyJoined(true, context)

        verify { toast.show() }
    }
}