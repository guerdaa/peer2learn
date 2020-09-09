package com.pse.peer2learn.ui.viewmodels

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.firebase.firestore.FirebaseFirestore
import com.pse.peer2learn.models.Student
import com.pse.peer2learn.repositories.GroupRepository
import com.pse.peer2learn.repositories.MessageRepository
import com.pse.peer2learn.ui.activities.interfaces.IHomeListener
import com.pse.peer2learn.utils.RepositoryProgress
import io.mockk.*
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule

class HomeViewModelTest {

    @get:Rule
    var rule: TestRule = InstantTaskExecutorRule()

    private lateinit var homeViewModel: HomeViewModel
    private lateinit var groupRepository: GroupRepository
    private lateinit var messageRepository: MessageRepository
    private val homeListener: IHomeListener = mockk(relaxed = true)

    @Before
    fun setUp() {
        mockkStatic(FirebaseFirestore::class)
        every { FirebaseFirestore.getInstance() } returns mockk(relaxed = true)
        messageRepository = mockk(relaxed = true)
        groupRepository = mockk(relaxed = true)
        homeViewModel = HomeViewModel(groupRepository, messageRepository)
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun retrieveUserGroupsTest() {
        val groups = arrayListOf<String>()

        homeViewModel.retrieveUserGroups(groups)

        verify { groupRepository.retrieveGroupsOfUser(groups) }
    }

    @Test
    fun `assert that observeRetrieve that will retrieve list of last messages when retrieveGroupsOfUser is successfull and the user is member of at least one group`() {
        val student = Student()
        student.studyGroupList = arrayListOf("Group 1")

        homeViewModel.observeRetrieve(RepositoryProgress.SUCCESS, student)

        verify { messageRepository.retrieveLastMessages(student.studyGroupList) }
    }

    @Test
    fun `assert that observeRetrieve that will not retrieve list of last messages when retrieveGroupsOfUser is successfull and the user is not member of any group`() {
        val student = Student()
        student.studyGroupList = arrayListOf()

        homeViewModel.observeRetrieve(RepositoryProgress.SUCCESS, student)

        verify(exactly = 0) { messageRepository.retrieveLastMessages(student.studyGroupList) }
    }

    @Test
    fun `assert that observeRetrieve that will not retrieve list of last messages when retrieveGroupsOfUser failed and the user is member of at least one group`() {
        val student = Student()
        student.studyGroupList = arrayListOf("Group 1")

        homeViewModel.observeRetrieve(RepositoryProgress.FAILED, student)

        verify(exactly = 0) { messageRepository.retrieveLastMessages(student.studyGroupList) }
    }

    @Test
    fun `assert that observeRetrieveMessage that will call initRecyclerViewAdapter when retrieving message is successfull and the user is member of at least one group`() {
        val student = Student()
        student.studyGroupList = arrayListOf("Group 1")

        homeViewModel.observeRetrieveMessage(RepositoryProgress.SUCCESS, student, homeListener)

        verify(exactly = 1) { homeListener.initRecyclerViewAdapter() }
    }

    @Test
    fun `assert that observeRetrieveMessage that will not call initRecyclerViewAdapter when retrieving message fails`() {
        val student = Student()
        student.studyGroupList = arrayListOf("Group 1")

        homeViewModel.observeRetrieveMessage(RepositoryProgress.FAILED, student, homeListener)

        verify(exactly = 0) { homeListener.initRecyclerViewAdapter() }
    }

    @Test
    fun `assert that observeRetrieveMessage that will not call initRecyclerViewAdapter when the user is not member of any group`() {
        val student = Student()
        student.studyGroupList = arrayListOf()

        homeViewModel.observeRetrieveMessage(RepositoryProgress.SUCCESS, student, homeListener)

        verify(exactly = 0) { homeListener.initRecyclerViewAdapter() }
    }

}