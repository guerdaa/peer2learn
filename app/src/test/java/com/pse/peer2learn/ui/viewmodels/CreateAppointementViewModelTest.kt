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

class CreateAppointementViewModelTest {

    @get:Rule
    var rule: TestRule = InstantTaskExecutorRule()

    private lateinit var createAppointementViewModel: CreateAppointementViewModel
    private lateinit var appointmentRepository: AppointmentRepository

    @Before
    fun setUp() {
        mockkStatic(FirebaseFirestore::class)
        every { FirebaseFirestore.getInstance() } returns mockk(relaxed = true)
        appointmentRepository = mockk(relaxed = true)
        createAppointementViewModel = CreateAppointementViewModel(appointmentRepository)
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun updateCategoryTest() {
        val category: Category = mockk(relaxed = true)

        createAppointementViewModel.updateCategory(category)

        verify { appointmentRepository.update(category) }
    }

    @Test
    fun createCategoryTest() {
        val category: Category = mockk(relaxed = true)

        createAppointementViewModel.createCategory(category)

        verify { appointmentRepository.create(category) }
    }
}