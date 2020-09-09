package com.pse.peer2learn.ui.activities

import android.os.Build
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.pse.peer2learn.models.StudyGroup
import com.pse.peer2learn.ui.viewmodels.GroupSearchPrivateViewModel
import io.mockk.*
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.P])
class GroupSearchPrivateFragmentTest {

    private val groupSearchPrivateFragment = GroupSearchPrivateFragment.newInstance(mockk(relaxed = true))
    private val spyGroupSearchPrivateFragment = spyk(groupSearchPrivateFragment)
    private val groupSearchPrivateViewModel: GroupSearchPrivateViewModel = mockk(relaxed = true)
    private val group = StudyGroup("ID2", "Second group", "desc", 0, 0,
        "", mockk(relaxed = true), mockk(relaxed = true), 0)
    @Before
    fun setUp() {
        mockkStatic(FirebaseFirestore::class)
        mockkStatic(FirebaseAuth::class)

        every { FirebaseFirestore.getInstance() } returns mockk(relaxed = true)
        every { FirebaseAuth.getInstance() } returns mockk(relaxed = true)

        every { groupSearchPrivateViewModel.retrievedGroup.value } returns group
        spyGroupSearchPrivateFragment.groupSearchPrivateViewModel = groupSearchPrivateViewModel
    }

    @After
    fun tearDown() = unmockkAll()

    @Test
    fun `Test joinGroup`() {
        spyGroupSearchPrivateFragment.joinGroup()

        verify { groupSearchPrivateViewModel.joinGroup(any(), any()) }
    }

/*    @Test
    fun `Test searchGroup`() {
        val scenario = launchFragmentInContainer<GroupSearchPrivateFragment>()
        scenario.onFragment { fragment ->
            fragment.groupSearchPrivateViewModel = groupSearchPrivateViewModel
            fragment.search_private_edit_text.setText("GROUP_ID")

            fragment.submit_button.performClick()

            verify { groupSearchPrivateViewModel.findPrivateGroup("GROUP_ID") }
        }
    }*/
}