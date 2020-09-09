package com.pse.peer2learn.ui.viewmodels

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.firebase.firestore.FirebaseFirestore
import com.pse.peer2learn.repositories.MemberRepository
import io.mockk.*
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule

class GroupsViewModelTest {

    @get:Rule
    var rule: TestRule = InstantTaskExecutorRule()

    private lateinit var groupsViewModel: GroupsViewModel
    private lateinit var memberRepository: MemberRepository

    @Before
    fun setUp() {
        mockkStatic(FirebaseFirestore::class)
        every { FirebaseFirestore.getInstance() } returns mockk(relaxed = true)
        memberRepository = mockk(relaxed = true)
        groupsViewModel = GroupsViewModel(memberRepository)
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun checkIfUserIsAdminTest() {
        val groupId = "Group ID"
        val studentId = "Student ID"

        groupsViewModel.checkIfUserIsAdmin(groupId, studentId)

        verify { memberRepository.checkIfUserIsAdmin(groupId, studentId) }
    }
}