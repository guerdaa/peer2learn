package com.pse.peer2learn.ui.activities

import android.content.Intent
import android.os.Build
import androidx.test.core.app.ApplicationProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.pse.peer2learn.models.Student
import com.pse.peer2learn.utils.Constants
import io.mockk.*
import kotlinx.android.synthetic.main.activity_create_group.*
import org.junit.After
import org.junit.Assert.*
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
class CreateGroupActivityTest {

    private lateinit var createGroupActivity: CreateGroupActivity
    private lateinit var activityController: ActivityController<CreateGroupActivity>
    private val currentStudent = Student("ID", "NICKNAME", "INFORMATIK", mockk(relaxed = true), arrayListOf())

    @Before
    fun setUp() {
        mockkStatic(FirebaseFirestore::class)
        mockkStatic(FirebaseAuth::class)

        every { FirebaseFirestore.getInstance() } returns mockk(relaxed = true)
        every { FirebaseAuth.getInstance() } returns mockk(relaxed = true)

        val intent =
            Intent(ApplicationProvider.getApplicationContext(), ProfileActivity::class.java)
        intent.putExtra(Constants.USER_EXTRA, currentStudent)
        activityController =
            Robolectric.buildActivity(CreateGroupActivity::class.java, intent).create().start()
                .visible()
        createGroupActivity = activityController.get()
        createGroupActivity.createGroupViewModel = mockk(relaxed = true)
    }

    @Test
    fun `Assert that clicking on submit will create a public group if the infos are valid`() {
        createGroupActivity.private_group_switch_button.isChecked = false
        every {
            createGroupActivity.createGroupViewModel.validateFormValues(
                any(),
                any(),
                any(),
                any(),
                any()
            )
        } returns true

        createGroupActivity.submit_button.performClick()

        verify { createGroupActivity.createGroupViewModel.createPublicGroup(any()) }
        assertFalse(createGroupActivity.submit_button.isEnabled)
    }

    @Test
    fun `Assert that clicking on submit will create a private group if the infos are valid`() {
        createGroupActivity.private_group_switch_button.isChecked = true
        every {
            createGroupActivity.createGroupViewModel.validateFormValues(
                any(),
                any(),
                any(),
                any(),
                any()
            )
        } returns true

        createGroupActivity.submit_button.performClick()

        verify { createGroupActivity.createGroupViewModel.createPrivateGroup(any()) }
        assertFalse(createGroupActivity.private_group_switch_button.isChecked)
    }

    @Test
    fun `Assert that clicking on submit will create any group if the infos are not valid`() {
        every {
            createGroupActivity.createGroupViewModel.validateFormValues(
                any(),
                any(),
                any(),
                any(),
                any()
            )
        } returns false

        createGroupActivity.submit_button.performClick()

        verify(exactly = 0) { createGroupActivity.createGroupViewModel.createPrivateGroup(any()) }
        assertTrue(createGroupActivity.submit_button.isEnabled)
    }

    @Test
    fun `Assert that access_code_text_view will set the generated private code of the viewmodel`() {
        every { createGroupActivity.createGroupViewModel.privatAccessCode } returns "PRIVATE_CODE"

        createGroupActivity.setAccessCode()

        assertEquals(createGroupActivity.access_code_text_view.text.toString(), "PRIVATE_CODE")
        assertTrue(createGroupActivity.submit_button.isEnabled)
    }

    @Test
    fun testShowPrivateGroupCreatedFragment() {
        createGroupActivity.showPrivateGroupCreatedFragment()

        assertNotNull(createGroupActivity.supportFragmentManager.findFragmentByTag(PRIVATE_GROUP))
    }

    @Test
    fun `Assert that onBackPressed will start HomeActivity`() {
        createGroupActivity.onBackPressed()

        val intent = Shadows.shadowOf(createGroupActivity).nextStartedActivity
        val shadowIntent = Shadows.shadowOf(intent)

        assertEquals(HomeActivity::class.java, shadowIntent.intentClass)
        assertEquals(intent.getSerializableExtra(Constants.USER_EXTRA), currentStudent)
    }

    @Test
    fun testDecrementMinParticipant() {
        createGroupActivity.number_of_users_text_view.text = "2"

        createGroupActivity.decrementMinParticipant(mockk(relaxed = true))

        assertEquals(createGroupActivity.number_of_users_text_view.text.toString(), "1")
    }

    @Test
    fun testIncrementMinParticipant() {
        createGroupActivity.number_of_users_text_view.text = "2"

        createGroupActivity.incrementMaxParticipant(mockk(relaxed = true))

        assertEquals(createGroupActivity.number_of_users_text_view.text.toString(), "3")
    }

    @Test
    fun `Assert that switching off the switch button will clear the access code text view`() {
        createGroupActivity.private_group_switch_button.isChecked = true
        createGroupActivity.access_code_text_view.text = "NOT_EMPTY"

        createGroupActivity.private_group_switch_button.performClick()

        assertTrue(createGroupActivity.access_code_text_view.text.toString().isEmpty())
        assertTrue(createGroupActivity.submit_button.isEnabled)
    }

    @Test
    fun `Assert that switching on the switch button will generate an access code`() {
        createGroupActivity.private_group_switch_button.isChecked = false

        createGroupActivity.private_group_switch_button.performClick()

        verify { createGroupActivity.createGroupViewModel.generateAccessCode() }
        assertFalse(createGroupActivity.submit_button.isEnabled)
    }

    @Test
    fun `Assert that clicking on profile icon will start ProfileActivity`() {
        createGroupActivity.profile_image_view.performClick()

        val intent = Shadows.shadowOf(createGroupActivity).nextStartedActivity
        val shadowIntent = Shadows.shadowOf(intent)

        assertEquals(ProfileActivity::class.java, shadowIntent.intentClass)
    }

    @After
    fun tearDown() = unmockkAll()

    companion object {
        private const val PRIVATE_GROUP = "PRIVATE_GROUP"
    }
}