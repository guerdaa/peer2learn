package com.pse.peer2learn.ui.viewmodels

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.firebase.firestore.FirebaseFirestore
import com.pse.peer2learn.models.Member
import com.pse.peer2learn.models.StudyGroup
import com.pse.peer2learn.repositories.AppointmentRepository
import com.pse.peer2learn.repositories.GroupRepository
import com.pse.peer2learn.repositories.MemberRepository
import io.mockk.*
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule

class GroupOverviewViewModelTest {

    @get:Rule
    var rule: TestRule = InstantTaskExecutorRule()

    private lateinit var groupOverviewViewModel: GroupOverviewViewModel
    private lateinit var memberRepository: MemberRepository
    private lateinit var groupRepository: GroupRepository

    @Before
    fun setUp() {
        mockkStatic(FirebaseFirestore::class)
        every { FirebaseFirestore.getInstance() } returns mockk(relaxed = true)
        memberRepository = mockk(relaxed = true)
        groupRepository = mockk(relaxed = true)
        groupOverviewViewModel = GroupOverviewViewModel(groupRepository, memberRepository)
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun updateStudyGroupTest() {
        val studyGroup: StudyGroup = mockk(relaxed = true)

        groupOverviewViewModel.updateStudyGroup(studyGroup)

        verify { groupRepository.update(studyGroup) }
    }

    @Test
    fun giveAdminRightTest() {
        val member: Member = spyk()

        groupOverviewViewModel.giveAdminRight(member)

        assertEquals(member.isAdmin, true)
        verify { memberRepository.update(member) }
    }
}