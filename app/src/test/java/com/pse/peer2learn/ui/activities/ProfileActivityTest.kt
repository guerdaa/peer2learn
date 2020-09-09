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
import kotlinx.android.synthetic.main.activity_profile.*
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows.shadowOf
import org.robolectric.android.controller.ActivityController
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.P])
class ProfileActivityTest {

    private lateinit var profileActivity: ProfileActivity
    private lateinit var activityController: ActivityController<ProfileActivity>
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
        activityController = Robolectric.buildActivity(ProfileActivity::class.java, intent).create().start().visible()
        profileActivity = activityController.get()
        profileActivity.profileViewModel = mockk(relaxed = true)
    }

    @After
    fun tearDown() = unmockkAll()

    @Test
    fun `Assert that the text field are correctly initialized when ProfileActivity is started`() {
        assertEquals(profileActivity.username_edit_text.text.toString(), "NICKNAME")
        assertEquals(profileActivity.university_edit_text.text.toString(), "KIT")
        assertEquals(profileActivity.study_course_edit_text.text.toString(), "INFORMATIK")
    }


    @Test
    fun `Assert that clicking on submit will validate the infos first`() {
        profileActivity.username_edit_text.setText("NEW_NICKNAME")
        profileActivity.study_course_edit_text.setText("WIWI")

        profileActivity.submit_button.performClick()

        verify { profileActivity.profileViewModel.verifyValidInfos("NEW_NICKNAME", any(), "WIWI", profileActivity) }
    }

    @Test
    fun `Assert when the infos are updated, HomeActivity will start`() {
        profileActivity.submit()
        val intent = shadowOf(profileActivity).nextStartedActivity
        val shadowIntent = shadowOf(intent)

        assertEquals(HomeActivity::class.java, shadowIntent.intentClass)
    }

    @Test
    fun `Assert that when onBackPressed, HomeActivity will start`() {
        profileActivity.onBackPressed()
        val intent = shadowOf(profileActivity).nextStartedActivity
        val shadowIntent = shadowOf(intent)

        assertEquals(HomeActivity::class.java, shadowIntent.intentClass)
    }

    @Test
    fun `Assert that updateCurrentUser will submit the infos`() {
        profileActivity.username_edit_text.setText("NEW_NICKNAME")
        profileActivity.study_course_edit_text.setText("WIWI")
        profileActivity.uniList.add(university)

        profileActivity.updateCurrentUser()

        verify { profileActivity.profileViewModel.submitUserInfos(any()) }
        assertEquals(profileActivity.currentStudent.nickname, "NEW_NICKNAME")
        assertEquals(profileActivity.currentStudent.studyCourse, "WIWI")
        assertEquals(profileActivity.currentStudent.university, university)
    }

    @Test
    fun `Assert that clicking on profile Image will open the Gallery`() {
        profileActivity.profile_profile_image.performClick()
        val intent = shadowOf(profileActivity).nextStartedActivity

        assertEquals(intent.action, Intent.ACTION_PICK)
        assertEquals(intent.data, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
    }
}