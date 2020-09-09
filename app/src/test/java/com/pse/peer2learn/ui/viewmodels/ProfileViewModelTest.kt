package com.pse.peer2learn.ui.viewmodels

import android.content.Context
import android.widget.Toast
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.pse.peer2learn.models.Student
import com.pse.peer2learn.repositories.GroupRepository
import com.pse.peer2learn.repositories.MemberRepository
import com.pse.peer2learn.repositories.UniversityRepository
import com.pse.peer2learn.repositories.UserRepository
import com.pse.peer2learn.ui.activities.interfaces.IProfileListener
import com.pse.peer2learn.ui.activities.interfaces.ISubmitListener
import com.pse.peer2learn.utils.RepositoryProgress
import io.mockk.*
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import org.mockito.ArgumentMatchers

class ProfileViewModelTest {

    @get:Rule
    var rule: TestRule = InstantTaskExecutorRule()

    private lateinit var profileViewModel: ProfileViewModel
    private lateinit var userRepository: UserRepository
    private lateinit var universityRepository: UniversityRepository
    private lateinit var memberRepository: MemberRepository
    private lateinit var groupRepository: GroupRepository
    private val context: Context = mockk(relaxed = true)
    private val toast: Toast = mockk(relaxed = true)
    private val firebaseAuth: FirebaseAuth = mockk(relaxed = true)
    private val firebaseStorage: FirebaseStorage = mockk(relaxed = true)
    private val submitListener: ISubmitListener = mockk(relaxed = true)
    private val profileListener: IProfileListener = mockk(relaxed = true)

    @Before
    fun setUp() {
        mockkStatic(FirebaseFirestore::class)
        mockkStatic(FirebaseAuth::class)
        mockkStatic(FirebaseStorage::class)
        mockkStatic(Toast::class)
        every { Toast.makeText(context, ArgumentMatchers.anyString(), ArgumentMatchers.anyInt()) } returns toast
        every { FirebaseFirestore.getInstance() } returns mockk(relaxed = true)
        every { FirebaseAuth.getInstance() } returns firebaseAuth
        every { FirebaseStorage.getInstance() } returns firebaseStorage
        userRepository = mockk(relaxed = true)
        universityRepository = mockk(relaxed = true)
        groupRepository = mockk(relaxed = true)
        memberRepository = mockk(relaxed = true)

        profileViewModel = ProfileViewModel(userRepository, memberRepository, groupRepository, universityRepository)
        profileViewModel.profileViewModelListener = mockk(relaxed = true)
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun retrieveAllUnisTest() {
        profileViewModel.retrieveAllUnis()

        verify { universityRepository.retrieve() }
    }

    @Test
    fun submitUserInfosTest() {
        val student: Student = mockk(relaxed = true)

        profileViewModel.submitUserInfos(student)

        verify { userRepository.update(student) }
    }

    @Test
    fun `assert checkUsername is not calling verifyUniqueUsername if username didn't change`() {
        val username = "Username"
        val student = Student()
        student.nickname = username

        profileViewModel.checkUsername(RepositoryProgress.SUCCESS, username, student, submitListener, context)

        verify(exactly = 0) { userRepository.getUsername(username) }
        verify { submitListener.updateCurrentUser() }
    }

    @Test
    fun `assert checkUsername is calling verifyUniqueUsername if username changed`() {
        val username = "Username"
        val student = Student()
        student.nickname = "another Name"

        profileViewModel.checkUsername(RepositoryProgress.SUCCESS, username, student, submitListener, context)

        verify(exactly = 1) { userRepository.getUsername(username) }
        verify(exactly = 0) { submitListener.updateCurrentUser() }
    }

    @Test
    fun `assert checkUsername is not calling anything on Failed und show a toast message instead`() {
        val username = "Username"
        val student = Student()
        student.nickname = "another Name"

        profileViewModel.checkUsername(RepositoryProgress.FAILED, username, student, submitListener, context)

        verify(exactly = 0) { userRepository.getUsername(username) }
        verify(exactly = 0) { submitListener.updateCurrentUser() }
        verify { toast.show() }
    }

    @Test
    fun submitChangesSuccessTest() {
        profileViewModel.submitChanges(RepositoryProgress.SUCCESS, submitListener, context)

        verify { submitListener.updateCurrentUser() }
        verify(exactly = 0) { toast.show() }
    }

    @Test
    fun submitChangesFailedTest() {
        profileViewModel.submitChanges(RepositoryProgress.FAILED, submitListener, context)

        verify(exactly = 0) { submitListener.updateCurrentUser() }
    }

    @Test
    fun `assert verifyValidInfos is printing a toast message if fields are empty`() {
        profileViewModel.verifyValidInfos("", "University", "StudyCourse", context)
        profileViewModel.verifyValidInfos("Username", "", "StudyCourse", context)
        profileViewModel.verifyValidInfos("Username", "University", "", context)

        verify(exactly = 3) { toast.show() }
    }

    @Test
    fun `assert verifyValidInfos is verifying the university when fields aren't empty`() {
        profileViewModel.verifyValidInfos("Username", "University", "StudyCourse", context)

        verify { universityRepository.verify("University")  }

    }

    @Test
    fun `test quitAllGroups when the listOfGroups isn't empty`() {
        val student: Student = spyk()
        student.studyGroupList = arrayListOf("group Id1", "group Id2")

        profileViewModel.quitAllGroups(context, student)

        verify { memberRepository.delete(any()) }
        verify { groupRepository.decrementNumberOfMember("group Id1") }
        verify { groupRepository.decrementNumberOfMember("group Id2") }
        verify(exactly = 0) { profileViewModel.profileViewModelListener.delete(context, student) }
    }

    @Test
    fun `assert that quitAllGroups is deleting the account when the listOfGroups is empty`() {
        val student: Student = spyk()
        student.studyGroupList = arrayListOf()

        profileViewModel.quitAllGroups(context, student)

        verify(exactly = 0) { memberRepository.delete(any()) }
        verify(exactly = 0) { groupRepository.decrementNumberOfMember(any()) }
        verify(exactly = 1) { profileViewModel.profileViewModelListener.delete(context, student) }
    }

    @Test
    fun `test observeDeleteMember on Success and when counter is greater than 0`() {
        val student: Student = spyk()
        student.studyGroupList = arrayListOf("group_id")
        every { profileListener.getDeleteCounter() } returns 1

        profileViewModel.observeDeleteMember(RepositoryProgress.SUCCESS, student, 2, profileListener)

        verify { profileListener.setDeleteCounter(1) }
        verify(exactly = 0) { memberRepository.updateAdminsOfGroup(any()) }
    }

    @Test
    fun `test observeDeleteMember on Success and when counter is equal 0`() {
        val student: Student = spyk()
        student.studyGroupList = arrayListOf("group_id")
        every { profileListener.getDeleteCounter() } returns 0

        profileViewModel.observeDeleteMember(RepositoryProgress.SUCCESS, student, 1, profileListener)

        verify { profileListener.setDeleteCounter(0) }
        verify(exactly = 1) { memberRepository.updateAdminsOfGroup(any()) }
        verify { profileListener.setDeleteCounter(1) }
    }

    @Test
    fun `test observeUpdateAdminsOfGroup on Success and when counter is greater than 0`() {
        val student: Student = spyk()
        student.studyGroupList = arrayListOf("group_id")
        every { profileListener.getDeleteCounter() } returns 1

        profileViewModel.observeUpdateAdminsOfGroup(RepositoryProgress.SUCCESS, student, 2, profileListener)

        verify { profileListener.setDeleteCounter(1) }
        verify(exactly = 0) { groupRepository.deleteEmptyGroup(any()) }
    }

    @Test
    fun `test observeUpdateAdminsOfGroup on Success and when counter is equal 0`() {
        val student: Student = spyk()
        student.studyGroupList = arrayListOf("group_id")
        every { profileListener.getDeleteCounter() } returns 0

        profileViewModel.observeUpdateAdminsOfGroup(RepositoryProgress.SUCCESS, student, 1, profileListener)

        verify { profileListener.setDeleteCounter(0) }
        verify(exactly = 1) { groupRepository.deleteEmptyGroup(any()) }
        verify { profileListener.setDeleteCounter(1) }
    }

    @Test
    fun `test observeDeleteGroup on Success and when counter is greater than 0`() {
        val student: Student = spyk()
        student.studyGroupList = arrayListOf("group_id")
        every { profileListener.getDeleteCounter() } returns 1

        profileViewModel.observeDeleteGroup(RepositoryProgress.SUCCESS, student, 2, profileListener, context)

        verify { profileListener.setDeleteCounter(1) }
        verify(exactly = 0) { profileViewModel.profileViewModelListener.delete(any(), any()) }
    }

    @Test
    fun `test observeDeleteGroup on Success and when counter is equal 0`() {
        val student: Student = spyk()
        student.studyGroupList = arrayListOf("group_id")
        every { profileListener.getDeleteCounter() } returns 0

        profileViewModel.observeDeleteGroup(RepositoryProgress.SUCCESS, student, 1, profileListener, context)

        verify { profileListener.setDeleteCounter(0) }
        verify(exactly = 1) { profileViewModel.profileViewModelListener.delete(context, student) }
    }
}