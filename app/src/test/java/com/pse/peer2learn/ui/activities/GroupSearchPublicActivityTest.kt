package com.pse.peer2learn.ui.activities

import android.content.Intent
import android.os.Build
import androidx.core.view.get
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
import kotlinx.android.synthetic.main.activity_group_search_public.*
import kotlinx.android.synthetic.main.group_result_layout.view.*
import org.junit.After
import org.junit.Assert.*
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
class GroupSearchPublicActivityTest {

    private lateinit var groupSearchPublicActivity: GroupSearchPublicActivity
    private lateinit var activityController: ActivityController<GroupSearchPublicActivity>
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
            Robolectric.buildActivity(GroupSearchPublicActivity::class.java, intent).create().start()
                .visible()
        groupSearchPublicActivity = activityController.get()
        groupSearchPublicActivity.groupSearchPublicViewModel = mockk(relaxed = true)
    }

    @After
    fun tearDown() = unmockkAll()

    @Test
    fun `Assert that the filters are shown at the beginning`() {
        assertTrue(groupSearchPublicActivity.add_filter_layout.isVisible)
        assertFalse(groupSearchPublicActivity.added_filter_layout.isVisible)
        assertFalse(groupSearchPublicActivity.results_group_recycler_view.isVisible)
    }

    @Test
    fun `Assert that clicking on submit will close the filter layout if it is already visible`() {
        groupSearchPublicActivity.submit_button.performClick()

        assertFalse(groupSearchPublicActivity.add_filter_layout.isVisible)
        assertTrue(groupSearchPublicActivity.added_filter_layout.isVisible)
        assertTrue(groupSearchPublicActivity.results_group_recycler_view.isVisible)
    }

    @Test
    fun `Assert that clicking on submit will set the filters`() {
        groupSearchPublicActivity.setProgressText("20%")
        groupSearchPublicActivity.setLanguageText("EN")
        groupSearchPublicActivity.setMinimumUsersText("2")

        groupSearchPublicActivity.submit_button.performClick()

        assertEquals(groupSearchPublicActivity.users_filter.text.toString(), "2")
        assertEquals(groupSearchPublicActivity.language_filter.text.toString(), "EN")
        assertEquals(groupSearchPublicActivity.progress_filter.text.toString(), "20%")
    }

    @Test
    fun `Test clicking on submit and the filters field are empty`() {
        groupSearchPublicActivity.submit_button.performClick()

        assertEquals(groupSearchPublicActivity.users_filter.text.toString(), Constants.EMPTY_FILTER)
        assertEquals(groupSearchPublicActivity.language_filter.text.toString(), Constants.EMPTY_FILTER)
        assertEquals(groupSearchPublicActivity.progress_filter.text.toString(), Constants.EMPTY_FILTER)
    }

    @Test
    fun `Assert that onBackPressed will start HomeActivity`() {
        groupSearchPublicActivity.onBackPressed()
        val intent = shadowOf(groupSearchPublicActivity).nextStartedActivity
        val shadowIntent = shadowOf(intent)

        assertEquals(HomeActivity::class.java, shadowIntent.intentClass)
        assertEquals(intent.getSerializableExtra(Constants.USER_EXTRA), currentStudent)
    }

    @Test
    fun `Assert that clicking on Profile Icon will start ProfileActivity`() {
        groupSearchPublicActivity.profile_image_view.performClick()
        val intent = shadowOf(groupSearchPublicActivity).nextStartedActivity
        val shadowIntent = shadowOf(intent)

        assertEquals(ProfileActivity::class.java, shadowIntent.intentClass)
        assertEquals(intent.getSerializableExtra(Constants.USER_EXTRA), currentStudent)
    }

    @Test
    fun `Assert that clicking on plus button will start CreateGroupActivity`() {
        groupSearchPublicActivity.createPublicGroup(mockk(relaxed = true))
        val intent = shadowOf(groupSearchPublicActivity).nextStartedActivity
        val shadowIntent = shadowOf(intent)

        assertEquals(CreateGroupActivity::class.java, shadowIntent.intentClass)
        assertEquals(intent.getSerializableExtra(Constants.USER_EXTRA), currentStudent)
    }

    @Test
    fun `Assert that clicking on lock button will start GroupSearchPrivateFragment`() {
        groupSearchPublicActivity.searchPrivateGroup(mockk(relaxed = true))

        assertNotEquals(groupSearchPublicActivity.supportFragmentManager.findFragmentByTag(TAG), null)
    }

    @Test
    fun `Assert that initRecyclerViewAdapter will set the results`() {
        groupSearchPublicActivity.submit_button.performClick()
        val firstGroup = StudyGroup("ID", "First group", "desc", 0, 0,
            "", mockk(relaxed = true), Language("Englisch"), 0)
        val results = arrayListOf(firstGroup)
        every { groupSearchPublicActivity.groupSearchPublicViewModel.retrievedResultLiveData.value } returns results
        every { groupSearchPublicActivity.groupSearchPublicViewModel.listAllGroups(any(), any()) } returns results

        groupSearchPublicActivity.initRecyclerViewAdapter()
        val firstItem = groupSearchPublicActivity.results_group_recycler_view[0]

        assertEquals(firstItem.group_title_text_view.text.toString(), "First group")
        assertEquals(firstItem.group_description_text_view.text.toString(), "desc")
        assertEquals(firstItem.number_of_users_text_view.text.toString(), "0")
        assertEquals(firstItem.language_text_view.text.toString(), "EN")
        assertEquals(firstItem.progress_text_view.text.toString(), "0%")
    }

    companion object {
        private const val TAG = "GroupSearchPrivateFragment"
    }

}