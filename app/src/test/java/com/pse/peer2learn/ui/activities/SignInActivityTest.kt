package com.pse.peer2learn.ui.activities

import android.os.Build
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.pse.peer2learn.models.Student
import com.pse.peer2learn.utils.Constants
import io.mockk.*
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows
import org.robolectric.Shadows.shadowOf
import org.robolectric.android.controller.ActivityController
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.P])
class SignInActivityTest {


    private lateinit var signInActivity: SignInActivity
    private lateinit var activityController: ActivityController<SignInActivity>

    @Before
    fun setUp() {
        mockkStatic(FirebaseFirestore::class)
        mockkStatic(FirebaseAuth::class)

        every { FirebaseFirestore.getInstance() } returns mockk(relaxed = true)
        every { FirebaseAuth.getInstance() } returns mockk(relaxed = true)

        activityController =
            Robolectric.buildActivity(SignInActivity::class.java).create().start()
                .visible()
        signInActivity = activityController.get()
        signInActivity.signInViewModel = mockk(relaxed = true)
    }

    @After
    fun tearDown() = unmockkAll()

    @Test
    fun `Assert that onStart the user is initialized`() {
        activityController.start()

        verify { signInActivity.signInViewModel.initUser() }
    }

    @Test
    fun `Test startRegistrationActivity`() {
        signInActivity.startRegistrationActivity()
        val intent = Shadows.shadowOf(signInActivity).nextStartedActivity
        val shadowIntent = shadowOf(intent)

        assertEquals(shadowIntent.intentClass, RegistrationActivity::class.java)
    }

    @Test
    fun `Test startHomeActivity`() {
        val student: Student = mockk(relaxed = true)
        signInActivity.startHomeActivity(student)
        val intent = Shadows.shadowOf(signInActivity).nextStartedActivity
        val shadowIntent = shadowOf(intent)

        assertEquals(intent.getSerializableExtra(Constants.USER_EXTRA), student)
        assertEquals(shadowIntent.intentClass, HomeActivity::class.java)
    }
}