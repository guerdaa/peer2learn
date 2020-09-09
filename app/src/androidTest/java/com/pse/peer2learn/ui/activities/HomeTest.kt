package com.pse.peer2learn.ui.activities

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.pse.peer2learn.R
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class HomeTest {
    @Before
    fun setUp() {
        ActivityScenario.launch(SignInActivity::class.java)
        Thread.sleep(200)
        onView(withId(R.id.sign_in_button)).perform(ViewActions.click())
        Thread.sleep(200)
    }

    @Test
    fun isFragmentDisplayed() {
        onView(withId(R.id.home_activity))
            .check(matches(isDisplayed()))
    }

    @Test
    fun isTitleBarDisplayed() {
        onView(withId(R.id.title_bar))
            .check(matches(isDisplayed()))
    }

    @Test
    fun isPeer2LearnImageViewDisplayed() {
        onView(withId(R.id.Peer2Learn_image_view))
            .check(matches(isDisplayed()))
    }

    @Test
    fun isProfileImageViewDisplayed() {
        onView(withId(R.id.profile_image_view))
            .check(matches(isDisplayed()))
    }

    @Test
    fun isGroupTextViewDisplayed() {
        onView(withId(R.id.groups_text_view))
            .check(matches(isDisplayed()))
    }

    @Test
    fun isSortByTextViewDisplayed() {
        onView(withId(R.id.sort_by_text_view))
            .check(matches(isDisplayed()))
    }

    @Test
    fun isGroupsRecyclerViewDisplayed() {
        onView(withId(R.id.groups_recycler_view))
            .check(matches(isDisplayed()))
    }

    @Test
    fun isAddGroupButtonDisplayed() {
        onView(withId(R.id.add_group_button))
            .check(matches(isDisplayed()))
    }

}