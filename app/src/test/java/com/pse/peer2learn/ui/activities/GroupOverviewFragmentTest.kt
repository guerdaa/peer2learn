package com.pse.peer2learn.ui.activities

import android.content.Intent
import android.os.Build
import androidx.core.view.isVisible
import androidx.test.core.app.ApplicationProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.pse.peer2learn.models.Language
import com.pse.peer2learn.models.Student
import com.pse.peer2learn.models.StudyGroup
import com.pse.peer2learn.utils.Constants
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import kotlinx.android.synthetic.main.fragment_group_overview.*
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.android.controller.ActivityController
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.P])
class GroupOverviewFragmentTest {

    private lateinit var groupActivity: GroupActivity
    private lateinit var activityController: ActivityController<GroupActivity>
    private lateinit var groupOverviewFragment: GroupOverviewFragment
    private val currentStudent = Student("ID", "NICKNAME", "INFORMATIK", mockk(relaxed = true), arrayListOf())
    private val group: StudyGroup = StudyGroup("ID2", "Second group", "desc", 0, 0,
        "ACCESS CODE", mockk(relaxed = true), Language("Englisch"), 0)

    @Before
    fun setUp() {
        mockkStatic(FirebaseFirestore::class)
        mockkStatic(FirebaseAuth::class)
        every { FirebaseFirestore.getInstance() } returns mockk(relaxed = true)
        every { FirebaseAuth.getInstance() } returns mockk(relaxed = true)
    }

    @After
    fun tearDown() = unmockkAll()

    @Test
    fun `Assert thst views are disabled when the user isn't admin`() {
        startFragment(false)

        assertFalse(groupOverviewFragment.progress_seek_bar.isEnabled)
        assertFalse(groupOverviewFragment.description_text_view.isEnabled)
        assertFalse(groupOverviewFragment.language_text_view.isEnabled)
        assertFalse(groupOverviewFragment.update_admin_rights.isVisible)
    }

    @Test
    fun `Assert thst views are enabled when the user is admin`() {
        startFragment(true)

        assertTrue(groupOverviewFragment.progress_seek_bar.isEnabled)
        assertTrue(groupOverviewFragment.description_text_view.isEnabled)
        assertTrue(groupOverviewFragment.language_text_view.isEnabled)
        assertTrue(groupOverviewFragment.update_admin_rights.isVisible)
    }

    @Test
    fun `Assert that the views are correctly initialized`() {
        startFragment(true)

        assertEquals(groupOverviewFragment.progress_seek_bar.progress, group.progress)
        assertEquals(groupOverviewFragment.description_text_view.text.toString(), group.description)
        assertEquals(groupOverviewFragment.language_text_view.text.toString(), group.language.name)
        assertEquals(groupOverviewFragment.access_code_text_view.text.toString(), group.accessCode)
    }

    @Test
    fun `Assert that appointment are correctly displayed`() {
        startFragment(true)
    }

    private fun startFragment(isAdmin: Boolean) {
        val intent =
            Intent(ApplicationProvider.getApplicationContext(), ProfileActivity::class.java)
        intent.putExtra(Constants.USER_EXTRA, currentStudent)
        intent.putExtra(Constants.GROUP_EXTRA, group)
        activityController =
            Robolectric.buildActivity(GroupActivity::class.java, intent).create().start().resume()
        groupActivity = activityController.get()
        groupActivity.isUserAdmin = isAdmin
        groupOverviewFragment = GroupOverviewFragment.newInstance(currentStudent, group)
        groupOverviewFragment.groupOverviewViewModel = mockk(relaxed = true)
        groupActivity.supportFragmentManager.beginTransaction().add(groupOverviewFragment, null).commitNowAllowingStateLoss()

    }
}