package com.pse.peer2learn.ui.activities

import android.content.Intent
import android.os.Build
import androidx.core.view.isVisible
import androidx.test.core.app.ApplicationProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.pse.peer2learn.models.Student
import com.pse.peer2learn.models.StudyGroup
import com.pse.peer2learn.utils.Constants
import io.mockk.*
import kotlinx.android.synthetic.main.fragment_appointements_overview.*
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
class AppointmentsOverviewFragmentTest {

    private lateinit var groupActivity: GroupActivity
    private lateinit var activityController: ActivityController<GroupActivity>
    private lateinit var appointmentsOverviewFragment: AppointmentsOverviewFragment
    private val currentStudent = Student("ID", "NICKNAME", "INFORMATIK", mockk(relaxed = true), arrayListOf())
    private val group: StudyGroup = StudyGroup("ID2", "Second group", "desc", 0, 0,
        "", mockk(relaxed = true), mockk(relaxed = true), 0)

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
    fun `Assert that the Add button are hidden when user isn't admin`() {
        startFragment(false)

        assertFalse(appointmentsOverviewFragment.add_category_button.isVisible)
    }

    @Test
    fun `Assert that the Add button are visible when user is admin`() {
        startFragment(true)

        assertTrue(appointmentsOverviewFragment.add_category_button.isVisible)
    }

    @Test
    fun `Assert that that clicking on Add button will open CreateAppointmentFragment`() {
        startFragment(true)
        appointmentsOverviewFragment.add_category_button.performClick()

        assertNotNull(groupActivity.supportFragmentManager.findFragmentByTag(TAG))
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
        appointmentsOverviewFragment = spyk(AppointmentsOverviewFragment.newInstance(group.id))
        appointmentsOverviewFragment.appointementsOverviewViewModel = mockk(relaxed = true)
        groupActivity.supportFragmentManager.beginTransaction().add(appointmentsOverviewFragment, null).commitNowAllowingStateLoss()

    }

    companion object {
        const val TAG = "CreateAppointementFragment"
    }

}