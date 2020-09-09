package com.pse.peer2learn.ui.activities

import android.content.Intent
import android.os.Build
import android.provider.MediaStore
import androidx.test.core.app.ApplicationProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.pse.peer2learn.models.Student
import com.pse.peer2learn.models.University
import com.pse.peer2learn.utils.Constants
import io.mockk.*
import kotlinx.android.synthetic.main.activity_profile.study_course_edit_text
import kotlinx.android.synthetic.main.activity_profile.submit_button
import kotlinx.android.synthetic.main.activity_profile.university_edit_text
import kotlinx.android.synthetic.main.activity_profile.username_edit_text
import kotlinx.android.synthetic.main.activity_registration.*
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows
import org.robolectric.android.controller.ActivityController
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.P])
class RegistrationActivityTest {

    private lateinit var registrationActivity: RegistrationActivity
    private lateinit var activityController: ActivityController<RegistrationActivity>
    private val university = University("Karlsruhe Institut für Technologie", "KIT", "Germany")
    private val currentStudent = Student("ID", "NICKNAME", "INFORMATIK", university, arrayListOf())

    @Before
    fun setUp() {
        mockkStatic(FirebaseFirestore::class)
        mockkStatic(FirebaseAuth::class)
        mockkStatic(FirebaseStorage::class)
        every { FirebaseFirestore.getInstance() } returns mockk(relaxed = true)
        every { FirebaseAuth.getInstance() } returns mockk(relaxed = true)
        every { FirebaseStorage.getInstance() } returns mockk(relaxed = true)
        val intent = Intent(ApplicationProvider.getApplicationContext(), ProfileActivity::class.java)
        intent.putExtra(Constants.USER_EXTRA, currentStudent)
        activityController = Robolectric.buildActivity(RegistrationActivity::class.java, intent).create().start().visible()
        registrationActivity = activityController.get()
        registrationActivity.registrationViewModel = mockk(relaxed = true)
    }

    @After
    fun tearDown() = unmockkAll()

    @Test
    fun `Assert that clicking on submit will validate the infos first`() {
        registrationActivity.username_edit_text.setText("NEW_NICKNAME")
        registrationActivity.study_course_edit_text.setText("WIWI")

        registrationActivity.submit_button.performClick()

        verify { registrationActivity.registrationViewModel.verifyValidInfos("NEW_NICKNAME", any(), "WIWI", registrationActivity) }
    }

    @Test
    fun `Assert when the infos are submited, HomeActivity will start`() {
        registrationActivity.username_edit_text.setText("NEW_NICKNAME")
        registrationActivity.study_course_edit_text.setText("WIWI")
        registrationActivity.university_edit_text.setText("KIT")
        registrationActivity.uniList.add(university)
        registrationActivity.updateCurrentUser()

        registrationActivity.submit()
        val intent = Shadows.shadowOf(registrationActivity).nextStartedActivity
        val shadowIntent = Shadows.shadowOf(intent)

        verify { registrationActivity.registrationViewModel.submitUserInfos(any()) }
        assertEquals(HomeActivity::class.java, shadowIntent.intentClass)
    }

    @Test
    fun `Assert that clicking on profile Image will open the Gallery`() {
        registrationActivity.registration_profile_image.performClick()
        val intent = Shadows.shadowOf(registrationActivity).nextStartedActivity

        assertEquals(intent.action, Intent.ACTION_PICK)
        assertEquals(intent.data, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
    }

}