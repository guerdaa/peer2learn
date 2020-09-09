package com.pse.peer2learn.ui.activities

import android.content.Intent
import android.os.Build
import androidx.core.view.get
import androidx.test.core.app.ApplicationProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.pse.peer2learn.R
import com.pse.peer2learn.models.Message
import com.pse.peer2learn.models.Student
import com.pse.peer2learn.models.StudyGroup
import com.pse.peer2learn.models.University
import com.pse.peer2learn.utils.Constants
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.group_item_layout.view.*
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
import org.robolectric.shadows.ShadowPopupMenu

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.P])
class HomeActivityTest {

    private lateinit var homeActivity: HomeActivity
    private lateinit var activityController: ActivityController<HomeActivity>
    private val university = University("Karlsruhe Institut für Technologie", "KIT", "Germany")
    private val currentStudent = Student("ID", "NICKNAME", "INFORMATIK", university, arrayListOf())

    @Before
    fun setUp() {
        mockkStatic(FirebaseFirestore::class)
        mockkStatic(FirebaseAuth::class)

        every { FirebaseFirestore.getInstance() } returns mockk(relaxed = true)
        every { FirebaseAuth.getInstance() } returns mockk(relaxed = true)

        val intent = Intent(ApplicationProvider.getApplicationContext(), ProfileActivity::class.java)
        intent.putExtra(Constants.USER_EXTRA, currentStudent)
        activityController = Robolectric.buildActivity(HomeActivity::class.java, intent).create().start().visible()
        homeActivity = activityController.get()
        homeActivity.homeViewModel = mockk(relaxed = true)
    }

    @After
    fun tearDown() = unmockkAll()

    @Test
    fun `Assert that the groups joined by the user are correctly displayed`() {
        val message = Message()
        message.id = "ID2"
        message.message = "new message"
        every { homeActivity.homeViewModel.retrievedUserGroupsLiveData.value } returns arrayListOf(
            StudyGroup("ID", "First group", "desc", 0, 0,
                "", mockk(relaxed = true), mockk(relaxed = true), 0),
            StudyGroup("ID2", "Second group", "desc", 0, 0,
                "", mockk(relaxed = true), mockk(relaxed = true), 0)
        )
        every { homeActivity.homeViewModel.retrievedLastMessages.value } returns hashMapOf(Pair("ID2", message))

        homeActivity.initRecyclerViewAdapter()
        val firstGroup = homeActivity.groups_recycler_view[0]
        val secondGroup = homeActivity.groups_recycler_view[1]

        assertEquals(firstGroup.group_title_text_view.text.toString(), "First group")
        assertEquals(secondGroup.group_title_text_view.text.toString(), "Second group")
        assertEquals(firstGroup.last_message_text_view.text.toString(), homeActivity.getString(R.string.group_initially_created))
        assertEquals(secondGroup.last_message_text_view.text.toString(), ": new message")
    }

    @Test
    fun `Assert that clicking on the menu of the group item will start HomeMenuFragment`() {
        every { homeActivity.homeViewModel.retrievedUserGroupsLiveData.value } returns arrayListOf(
            StudyGroup("ID", "First group", "desc", 0, 0,
                "", mockk(relaxed = true), mockk(relaxed = true), 0))
        every { homeActivity.homeViewModel.retrievedLastMessages.value } returns hashMapOf()

        homeActivity.initRecyclerViewAdapter()
        val firstGroup = homeActivity.groups_recycler_view[0]
        firstGroup.menu_image_view.performClick()

        assertNotEquals(homeActivity.supportFragmentManager.findFragmentByTag(TAG), null)
    }

    @Test
    fun `Assert that clicking on the group item will start GroupActivity`() {
        every { homeActivity.homeViewModel.retrievedUserGroupsLiveData.value } returns arrayListOf(
            StudyGroup("ID", "First group", "desc", 0, 0,
                "", mockk(relaxed = true), mockk(relaxed = true), 0))
        every { homeActivity.homeViewModel.retrievedLastMessages.value } returns hashMapOf()

        homeActivity.initRecyclerViewAdapter()
        val firstGroup = homeActivity.groups_recycler_view[0]
        firstGroup.performClick()
        val intent = Shadows.shadowOf(homeActivity).nextStartedActivity
        val shadowIntent = Shadows.shadowOf(intent)

        assertEquals(GroupActivity::class.java, shadowIntent.intentClass)
    }

    @Test
    fun `Assert that onBackPressed will put the app to the background`() {
        homeActivity.onBackPressed()
        val intent = Shadows.shadowOf(homeActivity).nextStartedActivity

        assertEquals(intent.action, Intent.ACTION_MAIN)
        assertEquals(intent.flags, Intent.FLAG_ACTIVITY_NEW_TASK)
        assertTrue(intent.categories.contains(Intent.CATEGORY_HOME))
    }

    @Test
    fun `Assert that clicking on add button will start GroupSearchPublicActivity`() {
        homeActivity.add_group_button.performClick()

        val intent = Shadows.shadowOf(homeActivity).nextStartedActivity
        val shadowIntent = Shadows.shadowOf(intent)

        assertEquals(GroupSearchPublicActivity::class.java, shadowIntent.intentClass)
    }

    @Test
    fun `Assert that clicking on profile icon will start ProfileActivity`() {
        homeActivity.profile_image_view.performClick()

        val intent = Shadows.shadowOf(homeActivity).nextStartedActivity
        val shadowIntent = Shadows.shadowOf(intent)

        assertEquals(ProfileActivity::class.java, shadowIntent.intentClass)
    }

    @Test
    fun `Assert that clicking on sort by will the sorting meni`() {
        homeActivity.adapter = mockk(relaxed = true)
        currentStudent.studyGroupList.add("Group ID")

        homeActivity.sort_by_text_view.performClick()
        val popupMenu = ShadowPopupMenu.getLatestPopupMenu()
        val descendingItem = popupMenu.menu.findItem(R.id.sort_desc)
        val ascendingItem = popupMenu.menu.findItem(R.id.sort_asc)

        assertTrue(descendingItem.isVisible)
        assertTrue(ascendingItem.isVisible)
    }

    companion object {
        const val TAG = "HomeMenuFragment"
    }
}