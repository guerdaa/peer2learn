package com.pse.peer2learn.ui.viewmodels

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.firebase.firestore.FirebaseFirestore
import com.pse.peer2learn.models.Student
import com.pse.peer2learn.models.StudyGroup
import com.pse.peer2learn.repositories.GroupRepository
import com.pse.peer2learn.repositories.MemberRepository
import com.pse.peer2learn.repositories.UserRepository
import com.pse.peer2learn.ui.activities.interfaces.IHomeMenuListener
import com.pse.peer2learn.utils.RepositoryProgress
import io.mockk.*
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule

class HomeMenuViewModelTest {

    @get:Rule
    var rule: TestRule = InstantTaskExecutorRule()

    private lateinit var homeMenuViewModel: HomeMenuViewModel
    private lateinit var memberRepository: MemberRepository
    private lateinit var userRepository: UserRepository
    private lateinit var groupRepository: GroupRepository
    private val homeMenuListener: IHomeMenuListener = mockk(relaxed = true)
    private val context: Context = mockk(relaxed = true)

    @Before
    fun setUp() {
        mockkStatic(FirebaseFirestore::class)
        every { FirebaseFirestore.getInstance() } returns mockk(relaxed = true)
        memberRepository = mockk(relaxed = true)
        userRepository = mockk(relaxed = true)
        groupRepository = mockk(relaxed = true)
        homeMenuViewModel = HomeMenuViewModel(userRepository, groupRepository, memberRepository)
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun updateListOfMembersTest() {
        val studentId = "User ID"
        val groupId = "Group ID"

        homeMenuViewModel.updateListOfMembers(studentId, groupId)

        verify { memberRepository.delete(any()) }
        verify { groupRepository.decrementNumberOfMember(groupId) }
    }

    @Test
    fun updateListOfGroupTest() {
        val student: Student = spyk()
        val groupId = "Group ID"
        student.studyGroupList = arrayListOf(groupId)

        homeMenuViewModel.updateListOfGroup(student, groupId)

        verify { userRepository.update(student) }
        assertFalse(student.studyGroupList.contains(groupId))
    }

    @Test
    fun `assert that observeDeleteMember will update listOfGroup when deleting the member is successfull`() {
        homeMenuViewModel.observeDeleteMember(RepositoryProgress.SUCCESS, homeMenuListener, context)

        verify { homeMenuListener.updateListOfGroup() }
    }

    @Test
    fun `assert that observeDeleteMember will dismiss the dialog when deleting the member fails`() {
        homeMenuViewModel.observeDeleteMember(RepositoryProgress.FAILED, homeMenuListener, context)

        verify(exactly = 0) { homeMenuListener.updateListOfGroup() }
        verify { homeMenuListener.dismissDialog(any()) }
    }

    @Test
    fun `assert that observeUpdateUser will call getNumberOfParticipants when updating the user is successfull`() {
        val groupId = "Group ID"

        homeMenuViewModel.observeUpdateUser(RepositoryProgress.SUCCESS, groupId, homeMenuListener, context)

        verify { memberRepository.getNumberOfParticipant(groupId) }
    }

    @Test
    fun `assert that observeUpdateUser will dismiss the dialog when updating the user fails`() {
        val groupId = "Group ID"

        homeMenuViewModel.observeUpdateUser(RepositoryProgress.FAILED, groupId, homeMenuListener, context)

        verify(exactly = 0) { memberRepository.getNumberOfParticipant(groupId) }
        verify { homeMenuListener.dismissDialog(any()) }
    }

    @Test
    fun `assert that observeParticipantsCount will update the admin of the group if numberOfParticipants is greater than 0`() {
        val studyGroup: StudyGroup = spyk()

        homeMenuViewModel.observeParticipantsCount(1, studyGroup)

        verify { memberRepository.updateAdminsOfGroup(studyGroup.id) }
    }

    @Test
    fun `assert that observeParticipantsCount will delete the group if numberOfParticipants is equal to 0`() {
        val studyGroup: StudyGroup = mockk(relaxed = true)

        homeMenuViewModel.observeParticipantsCount(0, studyGroup)

        verify { groupRepository.delete(studyGroup) }
    }

    @Test
    fun observeDeleteGroupTest() {
        homeMenuViewModel.observeDeleteGroup(RepositoryProgress.SUCCESS, homeMenuListener, context)
        homeMenuViewModel.observeDeleteGroup(RepositoryProgress.FAILED, homeMenuListener, context)

        verify(exactly = 2) { homeMenuListener.dismissDialog(any()) }
    }

    @Test
    fun observeUpdateAdminOfGroupTest() {
        homeMenuViewModel.observeUpdateAdminOfGroup(RepositoryProgress.SUCCESS, homeMenuListener, context)
        homeMenuViewModel.observeUpdateAdminOfGroup(RepositoryProgress.FAILED, homeMenuListener, context)

        verify(exactly = 2) { homeMenuListener.dismissDialog(any()) }
    }

}