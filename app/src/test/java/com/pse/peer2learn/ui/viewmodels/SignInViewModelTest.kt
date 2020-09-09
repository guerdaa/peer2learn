package com.pse.peer2learn.ui.viewmodels

import android.content.Context
import android.widget.Toast
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.pse.peer2learn.models.Student
import com.pse.peer2learn.models.University
import com.pse.peer2learn.repositories.UserRepository
import com.pse.peer2learn.ui.activities.interfaces.ISignInListener
import com.pse.peer2learn.utils.RepositoryProgress
import io.mockk.*
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import org.mockito.ArgumentMatchers

class SignInViewModelTest {

    @get:Rule
    var rule: TestRule = InstantTaskExecutorRule()

    private lateinit var signInViewModel: SignInViewModel
    private lateinit var userRepository: UserRepository

    private val user = "User"
    private val context: Context = mockk(relaxed = true)
    private val toast: Toast = mockk(relaxed = true)
    private val firebaseAuth: FirebaseAuth = mockk(relaxed = true)

    @Before
    fun setUp() {
        mockkStatic(FirebaseFirestore::class)
        mockkStatic(FirebaseAuth::class)
        mockkStatic(Toast::class)
        every { Toast.makeText(context, ArgumentMatchers.anyString(), ArgumentMatchers.anyInt()) } returns toast
        every { FirebaseFirestore.getInstance() } returns mockk(relaxed = true)
        every { FirebaseAuth.getInstance() } returns firebaseAuth
        every { firebaseAuth.uid } returns user
        userRepository = mockk(relaxed = true)
        signInViewModel = SignInViewModel(userRepository)

    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun initUserTest() {
        signInViewModel.initUser()

        assertEquals(signInViewModel.currentUidLiveData.value, user)
    }

    @Test
    fun observeSignInTest() {
        signInViewModel.observeSignIn(RepositoryProgress.SUCCESS, context)
        signInViewModel.observeSignIn(RepositoryProgress.FAILED, context)
        signInViewModel.observeSignIn(RepositoryProgress.IN_PROGRESS, context)

        verify(exactly = 3) { toast.show() }
    }

    @Test
    fun `assert that observeCurrentUid is check if user available`() {
        signInViewModel.initUser()

        signInViewModel.observeCurrentUid(user)

        verify { userRepository.retrieve(user)  }
    }


    @Test
    fun `assert that observeCurrentUid will not check if uid is empty`() {
        signInViewModel.observeCurrentUid("")

        verify(exactly = 0) { userRepository.retrieve(user)  }
    }

    @Test
    fun `assert that observeUserRetrieved will call HomeActivity if user has already an account`() {
        val student = Student("id", "nickname", "studycourse", University(), arrayListOf())
        val signInListener: ISignInListener = mockk(relaxed = true)

        signInViewModel.observeUserRetrieved(student, signInListener)

        verify { signInListener.startHomeActivity(student) }
        verify(exactly = 0) { userRepository.create(any()) }
    }

    @Test
    fun `assert that observeUserRetrieved will create an new account if user has no account`() {
        signInViewModel.initUser()
        val signInListener: ISignInListener = mockk(relaxed = true)

        signInViewModel.observeUserRetrieved(null, signInListener)

        verify (exactly = 0){ signInListener.startHomeActivity(any()) }
        verify(exactly = 1) { userRepository.create(any()) }
    }

    @Test
    fun observeUserCreatedTrueTest() {
        val signInListener: ISignInListener = mockk(relaxed = true)

        signInViewModel.observeUserCreated(true, signInListener, context)

        verify { signInListener.startRegistrationActivity() }
    }

    @Test
    fun observeUserCreatedFalseTest() {
        val signInListener: ISignInListener = mockk(relaxed = true)

        signInViewModel.observeUserCreated(false, signInListener, context)

        verify(exactly = 0) { signInListener.startRegistrationActivity() }
        verify { toast.show() }
    }
}