package com.pse.peer2learn.ui.viewmodels

import android.content.Context
import android.widget.Toast
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.firebase.firestore.FirebaseFirestore
import com.pse.peer2learn.models.Student
import com.pse.peer2learn.repositories.UniversityRepository
import com.pse.peer2learn.repositories.UserRepository
import com.pse.peer2learn.ui.activities.interfaces.ISubmitListener
import com.pse.peer2learn.utils.RepositoryProgress
import io.mockk.*
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import org.mockito.ArgumentMatchers.anyInt
import org.mockito.ArgumentMatchers.anyString

class RegistrationViewModelTest {

    @get:Rule
    var rule: TestRule = InstantTaskExecutorRule()

    private lateinit var registrationViewModel: RegistrationViewModel
    private lateinit var userRepository: UserRepository
    private lateinit var universityRepository: UniversityRepository
    private val submitListener: ISubmitListener = mockk(relaxed = true)
    private val context: Context = mockk(relaxed = true)
    private val toast: Toast = mockk(relaxed = true)

    @Before
    fun setUp() {
        mockkStatic(FirebaseFirestore::class)
        mockkStatic(Toast::class)
        every { Toast.makeText(context, anyString(), anyInt()) } returns toast
        every { FirebaseFirestore.getInstance() } returns mockk(relaxed = true)
        userRepository = mockk(relaxed = true)
        universityRepository = mockk(relaxed = true)
        registrationViewModel = RegistrationViewModel(userRepository, universityRepository)
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun retrieveAllUnisTest() {
        registrationViewModel.retrieveAllUnis()

        verify { universityRepository.retrieve() }
    }

    @Test
    fun submitUserInfosTest() {
        val student: Student = mockk(relaxed = true)

        registrationViewModel.submitUserInfos(student)

        verify { userRepository.update(student) }
    }

    @Test
    fun `assert that verifyValidInfos will dislay a toast if username, university or studycourse are empty`() {
        registrationViewModel.verifyValidInfos("", "University", "StudyCourse", context)
        registrationViewModel.verifyValidInfos("Username", "", "StudyCourse", context)
        registrationViewModel.verifyValidInfos("Username", "University", "", context)

        verify(exactly = 3) { toast.show() }
    }

    @Test
    fun `assert that verifyValidInfos will verify the university if all fields are not empty`() {
        registrationViewModel.verifyValidInfos("Username", "University", "StudyCourse", context)

        verify(exactly = 0) { toast.show() }
        verify { universityRepository.verify("University") }
    }

   @Test
   fun `assert that submit will be called when the infos are updated successfully`() {
       registrationViewModel.updateInfos(RepositoryProgress.SUCCESS, context, submitListener)

       verify { toast.show() }
       verify { submitListener.submit() }
   }

    @Test
    fun `assert that submit will not be called when the infos aren't updated`() {
        registrationViewModel.updateInfos(RepositoryProgress.FAILED, context, submitListener)

        verify { toast.show() }
        verify(exactly = 0) { submitListener.submit() }
    }

    @Test
    fun checkUsernameSuccessTest() {
        val username = "Username"

        registrationViewModel.checkUsername(RepositoryProgress.SUCCESS, username, context)

        verify { userRepository.getUsername(username) }
    }

    @Test
    fun checkUsernameFailedTest() {
        val username = "Username"

        registrationViewModel.checkUsername(RepositoryProgress.FAILED, username, context)

        verify(exactly = 0) { userRepository.getUsername(username) }
        verify { toast.show() }
    }

    @Test
    fun submitChangesSuccessTest() {
        registrationViewModel.submitChanges(RepositoryProgress.SUCCESS, submitListener, context)

        verify { submitListener.updateCurrentUser() }
    }

    @Test
    fun submitChangesFailedTest() {
        registrationViewModel.submitChanges(RepositoryProgress.FAILED, submitListener, context)

        verify { toast.show() }
    }


}