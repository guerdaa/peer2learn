package com.pse.peer2learn.ui.activities

import android.content.Intent
import android.os.Build
import androidx.core.view.isVisible
import androidx.test.core.app.ApplicationProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.pse.peer2learn.models.Student
import com.pse.peer2learn.models.StudyGroup
import com.pse.peer2learn.ui.viewmodels.ChatsViewModel
import com.pse.peer2learn.utils.Constants
import io.mockk.*
import kotlinx.android.synthetic.main.fragment_chats.*
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
class ChatsFragmentTest {

    private lateinit var groupActivity: GroupActivity
    private lateinit var activityController: ActivityController<GroupActivity>
    private val chatsViewModel: ChatsViewModel = mockk(relaxed = true)
    private val currentStudent = Student("ID", "NICKNAME", "INFORMATIK", mockk(relaxed = true), arrayListOf())
    private val group: StudyGroup = StudyGroup("ID2", "Second group", "desc", 0, 0,
        "", mockk(relaxed = true), mockk(relaxed = true), 0)
    private var chatsFragment = ChatsFragment.newInstance(currentStudent, group.id)


    @After
    fun tearDown() = unmockkAll()

    @Before
    fun setUp() {
        mockkStatic(FirebaseFirestore::class)
        mockkStatic(FirebaseAuth::class)
        every { FirebaseFirestore.getInstance() } returns mockk(relaxed = true)
        every { FirebaseAuth.getInstance() } returns mockk(relaxed = true)
    }

    @Test
    fun `Assert if send edit text is empty, galery button will be displayed`() {
        startFragment()

        assertFalse(chatsFragment.send_button.isVisible)
        assertTrue(chatsFragment.gallery_button.isVisible)
    }


    @Test
    fun `Assert that typing in send edit text will display send button`() {
        startFragment()

        chatsFragment.send_edit_text.setText("aa")

        assertTrue(chatsFragment.send_button.isVisible)
        assertFalse(chatsFragment.gallery_button.isVisible)
    }

    @Test
    fun `Assert that clicking on send will send a message and clear the edit text`() {
        startFragment()

        chatsFragment.send("Message")

        assertTrue(chatsFragment.send_edit_text.text.isEmpty())
        assertTrue(chatsFragment.send_edit_text.isEnabled)
    }

    @Test
    fun `Test clicking on gallery icon`() {
        startFragment()

        chatsFragment.gallery_button.performClick()

        val intent = Shadows.shadowOf(groupActivity).nextStartedActivity

        assertEquals(intent.action, Intent.ACTION_CHOOSER)
    }

    private fun startFragment() {
        val intent =
            Intent(ApplicationProvider.getApplicationContext(), ProfileActivity::class.java)
        intent.putExtra(Constants.USER_EXTRA, currentStudent)
        intent.putExtra(Constants.GROUP_EXTRA, group)
        activityController =
            Robolectric.buildActivity(GroupActivity::class.java, intent).create().start().resume()
        groupActivity = activityController.get()
        chatsFragment = ChatsFragment.newInstance(currentStudent, group.id)
        chatsFragment.chatsViewModel = spyk(chatsViewModel)
        groupActivity.supportFragmentManager.beginTransaction().add(chatsFragment, null).commitNowAllowingStateLoss()
    }


}