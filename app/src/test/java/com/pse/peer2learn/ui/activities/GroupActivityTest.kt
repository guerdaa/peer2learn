package com.pse.peer2learn.ui.activities

import android.content.Intent
import android.os.Build
import androidx.test.core.app.ApplicationProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.pse.peer2learn.models.Student
import com.pse.peer2learn.models.StudyGroup
import com.pse.peer2learn.utils.Constants
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import kotlinx.android.synthetic.main.activity_group.*
import kotlinx.android.synthetic.main.toolbar.*
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
class GroupActivityTest {

    private lateinit var groupActivity: GroupActivity
    private lateinit var activityController: ActivityController<GroupActivity>
    private val currentStudent = Student("ID", "NICKNAME", "INFORMATIK", mockk(relaxed = true), arrayListOf())
    private val group: StudyGroup = StudyGroup("ID2", "Second group", "desc", 0, 0,
        "", mockk(relaxed = true), mockk(relaxed = true), 0)

    @Before
    fun setUp() {
        mockkStatic(FirebaseFirestore::class)
        mockkStatic(FirebaseAuth::class)

        every { FirebaseFirestore.getInstance() } returns mockk(relaxed = true)
        every { FirebaseAuth.getInstance() } returns mockk(relaxed = true)

        val intent =
            Intent(ApplicationProvider.getApplicationContext(), GroupActivity::class.java)
        intent.putExtra(Constants.USER_EXTRA, currentStudent)
        intent.putExtra(Constants.GROUP_EXTRA, group)
        activityController =
            Robolectric.buildActivity(GroupActivity::class.java, intent).create()
        groupActivity = activityController.get()
        groupActivity.groupViewModel= mockk(relaxed = true)
    }

    @Test
    fun `Assert that onBackPressed will start HomeActivity`() {
        groupActivity.onBackPressed()

        val intent = Shadows.shadowOf(groupActivity).nextStartedActivity
        val shadowIntent = Shadows.shadowOf(intent)

        assertEquals(HomeActivity::class.java, shadowIntent.intentClass)
    }

    @Test
    fun `Assert that clicking on chat header will set the correct ui`() {
        groupActivity.chat_text_view.performClick()

        assertEquals(groupActivity.view_pager.currentItem, 0)
        assertEquals(groupActivity.chat_text_view.alpha, 1.0f)
        assertEquals(groupActivity.appointments_text_view.alpha, 0.3f)
        assertEquals(groupActivity.overview_text_view.alpha,0.3f)
    }

    @Test
    fun `Assert that clicking on appointments header will set the correct ui`() {
        groupActivity.appointments_text_view.performClick()

        assertEquals(groupActivity.view_pager.currentItem, 1)
        assertEquals(groupActivity.chat_text_view.alpha, 0.3f)
        assertEquals(groupActivity.appointments_text_view.alpha, 1.0f)
        assertEquals(groupActivity.overview_text_view.alpha,0.3f)
    }

    @Test
    fun `Assert that clicking on overview header will set the correct ui`() {
        groupActivity.overview_text_view.performClick()

        assertEquals(groupActivity.view_pager.currentItem, 2)
        assertEquals(groupActivity.chat_text_view.alpha, 0.3f)
        assertEquals(groupActivity.appointments_text_view.alpha, 0.3f)
        assertEquals(groupActivity.overview_text_view.alpha,1.0f)
    }

    @After
    fun tearDown() = unmockkAll()

}