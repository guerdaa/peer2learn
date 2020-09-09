package com.pse.peer2learn.ui.viewmodels

import android.content.Context
import android.widget.Toast
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.firebase.firestore.FirebaseFirestore
import com.pse.peer2learn.models.Language
import com.pse.peer2learn.models.Student
import com.pse.peer2learn.models.StudyGroup
import com.pse.peer2learn.models.University
import com.pse.peer2learn.repositories.GroupRepository
import com.pse.peer2learn.repositories.MemberRepository
import com.pse.peer2learn.repositories.UserRepository
import com.pse.peer2learn.ui.activities.interfaces.IGroupSearchPublicListener
import com.pse.peer2learn.utils.Constants
import com.pse.peer2learn.utils.RepositoryProgress
import io.mockk.*
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import org.mockito.ArgumentMatchers

class GroupSearchPublicViewModelTest {

    @get:Rule
    var rule: TestRule = InstantTaskExecutorRule()

    private lateinit var groupSearchPublicViewModel: GroupSearchPublicViewModel
    private lateinit var memberRepository: MemberRepository
    private lateinit var userRepository: UserRepository
    private lateinit var groupRepository: GroupRepository
    private val groupSearchPublicListener: IGroupSearchPublicListener = mockk(relaxed = true)
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
        groupRepository = mockk(relaxed = true)
        groupSearchPublicViewModel = GroupSearchPublicViewModel(userRepository, memberRepository, groupRepository)
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun filterByLanguageTest() {
        val filters = arrayListOf("-", "English", "-")
        val studyGroup1 = StudyGroup()
        studyGroup1.language = Language("English")
        val studyGroup2 = StudyGroup()
        studyGroup2.language = Language("German")
        val arrayToFilter = arrayListOf(studyGroup1, studyGroup2)
        val result = arrayListOf(studyGroup1)

        val r = groupSearchPublicViewModel.listAllGroups(filters, arrayToFilter)

        assertEquals(r, result)
    }

    @Test
    fun filterByMinParticipantTest() {
        val filters = arrayListOf("7", "-", "-")
        val studyGroup1 = StudyGroup()
        studyGroup1.numberOfMembers = 9
        val studyGroup2 = StudyGroup()
        studyGroup2.numberOfMembers = 1
        val arrayToFilter = arrayListOf(studyGroup1, studyGroup2)
        val result = arrayListOf(studyGroup1)

        val r = groupSearchPublicViewModel.listAllGroups(filters, arrayToFilter)

        assertEquals(r, result)
    }

    @Test
    fun filterByProgressTest() {
        val filters = arrayListOf("-", "-", "20%")
        val studyGroup1 = StudyGroup()
        studyGroup1.progress = 40
        val studyGroup2 = StudyGroup()
        studyGroup2.progress = 5
        val arrayToFilter = arrayListOf(studyGroup1, studyGroup2)
        val result = arrayListOf(studyGroup1)

        val r = groupSearchPublicViewModel.listAllGroups(filters, arrayToFilter)

        assertEquals(r, result)
    }

    @Test
    fun listAllGroupsTest() {
        val filters = arrayListOf("7", "english", "20%")
        val studyGroup1 = StudyGroup()
        studyGroup1.progress = 40
        studyGroup1.language = Language("English")
        studyGroup1.numberOfMembers = 9
        val studyGroup2 = StudyGroup()
        studyGroup2.progress = 5
        studyGroup1.language = Language("English")
        studyGroup1.numberOfMembers = 9
        val arrayToFilter = arrayListOf(studyGroup1, studyGroup2)
        val result = arrayListOf(studyGroup1)

        val r = groupSearchPublicViewModel.listAllGroups(filters, arrayToFilter)

        assertEquals(r, result)
    }

    @Test
    fun `assert that joinGroup is updating memberRepository, userRepository and groupRepository when the user join a new group`() {
        val student: Student = spyk()
        val studyGroup: StudyGroup = spyk()
        val groupId = "Group ID"
        studyGroup.id = groupId
        student.studyGroupList = arrayListOf()

        groupSearchPublicViewModel.joinGroup(student, studyGroup)

        verify { memberRepository.update(any()) }
        verify { userRepository.update(any()) }
        assertTrue(student.studyGroupList.contains(groupId))
        verify { groupRepository.incrementNumberOfMember(groupId) }
        assertFalse(groupSearchPublicViewModel.groupAlreadyJoined.value!!)
    }

    @Test
    fun `assert that joinGroup is updating memberRepository, userRepository and groupRepository when the usesr is already member of the group`() {
        val student: Student = spyk()
        val studyGroup: StudyGroup = spyk()
        val groupId = "Group ID"
        studyGroup.id = groupId
        student.studyGroupList = arrayListOf(groupId)

        groupSearchPublicViewModel.joinGroup(student, studyGroup)

        verify(exactly = 0) { memberRepository.update(any()) }
        verify(exactly = 0) { userRepository.update(any()) }
        verify(exactly = 0) { groupRepository.incrementNumberOfMember(groupId) }
        assertTrue(groupSearchPublicViewModel.groupAlreadyJoined.value!!)
    }

    @Test
    fun `assert that observeRetrievedList is calling initRecyclerViewAdapter when groups are retrieved`() {
        val studyGroup: StudyGroup = mockk(relaxed = true)

        groupSearchPublicViewModel.observeRetrievedList(arrayListOf(studyGroup), groupSearchPublicListener)

        verify { groupSearchPublicListener.initRecyclerViewAdapter() }
    }

    @Test
    fun `assert that observeRetrievedList is not calling initRecyclerViewAdapter when retrieved groups are empty`() {
        groupSearchPublicViewModel.observeRetrievedList(arrayListOf(), groupSearchPublicListener)

        verify(exactly = 0) { groupSearchPublicListener.initRecyclerViewAdapter() }
    }

    @Test
    fun `assert that a toast message is shown when the group is already joined`() {
        groupSearchPublicViewModel.observeGroupAlreadyJoined(true, context)

        verify { toast.show() }
    }

    @Test
    fun `assert that a toast message isn't shown when the group is not joined`() {
        groupSearchPublicViewModel.observeGroupAlreadyJoined(false, context)

        verify(exactly = 0) { toast.show() }
    }

    @Test
    fun `assert that a toast message is displayed when the member is successfully updated`() {
        groupSearchPublicViewModel.observeMemberUpdated(RepositoryProgress.SUCCESS, context)

        verify { toast.show() }
    }

    @Test
    fun `assert that a toast message isn't displayed when updating member fails`() {
        groupSearchPublicViewModel.observeMemberUpdated(RepositoryProgress.FAILED, context)

        verify(exactly = 0) { toast.show() }
    }

    @Test
    fun `test search group by progress, language and minimum users`() {
        val university: University = spyk()
        val groupSearchPublicListener: IGroupSearchPublicListener = mockk(relaxed = true)

        groupSearchPublicViewModel.searchGroups("20", "english", "2", "PP", university, groupSearchPublicListener)

        verify { groupSearchPublicListener.setLanguageText("english") }
        verify { groupSearchPublicListener.setMinimumUsersText("2") }
        verify { groupSearchPublicListener.setProgressText("20%") }
        assertEquals(groupSearchPublicViewModel.retrievedResultLiveData.value?.size, 0)
        verify { groupRepository.retrieve("PP", university) }
    }

    @Test
    fun `test search group when progress, language and minimum are empty`() {
        val university: University = spyk()
        val groupSearchPublicListener: IGroupSearchPublicListener = mockk(relaxed = true)

        groupSearchPublicViewModel.searchGroups("", "", "", "PP", university, groupSearchPublicListener)

        verify { groupSearchPublicListener.setLanguageText(Constants.EMPTY_FILTER) }
        verify { groupSearchPublicListener.setMinimumUsersText(Constants.EMPTY_FILTER) }
        verify { groupSearchPublicListener.setProgressText(Constants.EMPTY_FILTER) }
        assertEquals(groupSearchPublicViewModel.retrievedResultLiveData.value?.size, 0)
        verify { groupRepository.retrieve("PP", university) }
    }


}