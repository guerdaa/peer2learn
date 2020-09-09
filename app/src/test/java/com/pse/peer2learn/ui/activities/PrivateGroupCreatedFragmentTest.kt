package com.pse.peer2learn.ui.activities

import android.content.Intent
import android.os.Build
import androidx.test.core.app.ApplicationProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.pse.peer2learn.models.Student
import com.pse.peer2learn.utils.Constants
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import kotlinx.android.synthetic.main.fragment_private_group_created.*
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
class PrivateGroupCreatedFragmentTest {

    private val currentStudent = Student("ID", "NICKNAME", "INFORMATIK", mockk(relaxed = true), arrayListOf())
    private lateinit var createGroupActivity: CreateGroupActivity
    private lateinit var activityController: ActivityController<CreateGroupActivity>
    private lateinit var privateGroupCreatedFragment: PrivateGroupCreatedFragment

    private fun startFragment() {
        val intent =
            Intent(ApplicationProvider.getApplicationContext(), CreateGroupActivity::class.java)
        intent.putExtra(Constants.USER_EXTRA, currentStudent)
        activityController =
            Robolectric.buildActivity(CreateGroupActivity::class.java, intent).create().start().resume()
        createGroupActivity = activityController.get()
        privateGroupCreatedFragment = PrivateGroupCreatedFragment.newInstance("1234567", currentStudent)

        createGroupActivity.supportFragmentManager.beginTransaction().add(privateGroupCreatedFragment, null).commitNowAllowingStateLoss()

    }

    @Before
    fun setUp() {
        mockkStatic(FirebaseFirestore::class)
        mockkStatic(FirebaseAuth::class)

        every { FirebaseFirestore.getInstance() } returns mockk(relaxed = true)
        every { FirebaseAuth.getInstance() } returns mockk(relaxed = true)
    }

    @Test
    fun `Assert that access code is set correctly`() {
        startFragment()

        assertEquals(privateGroupCreatedFragment.access_code_text_view.text.toString(), "Access code:  1234567")
    }

    @Test
    fun `Test dismiss button`() {
        startFragment()

        privateGroupCreatedFragment.hide_button.performClick()

        val intent = Shadows.shadowOf(createGroupActivity).nextStartedActivity
        val shadowIntent = Shadows.shadowOf(intent)

        assertEquals(HomeActivity::class.java, shadowIntent.intentClass)
    }

    @After
    fun tearDown() = unmockkAll()
}