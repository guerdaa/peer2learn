package com.pse.peer2learn.ui.viewmodels

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.firebase.firestore.FirebaseFirestore
import com.pse.peer2learn.models.Category
import com.pse.peer2learn.repositories.AppointmentRepository
import io.mockk.*
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule

class AppointementsOverviewViewModelTest() {
    private lateinit var appointementsOverviewViewModel: AppointementsOverviewViewModel
    private lateinit var appointmentRepository: AppointmentRepository

    @get:Rule
    var rule: TestRule = InstantTaskExecutorRule()

    @Before
    fun setUp() {
        mockkStatic(FirebaseFirestore::class)
        every { FirebaseFirestore.getInstance() } returns mockk(relaxed = true)
        appointmentRepository = mockk(relaxed = true)
        appointementsOverviewViewModel = AppointementsOverviewViewModel(appointmentRepository)
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun retrieveCategoriesTest() {
        val groupId = "Group ID"
        appointementsOverviewViewModel.retrieveCategories(groupId)
        verify { appointmentRepository.retrieve(groupId) }
    }

    @Test
    fun deleteCategoryTest() {
        val category: Category = mockk(relaxed = true)
        appointementsOverviewViewModel.deleteCategory(category)
        verify { appointmentRepository.delete(category) }
    }
}