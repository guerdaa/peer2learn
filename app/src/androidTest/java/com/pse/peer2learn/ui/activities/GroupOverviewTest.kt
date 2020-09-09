package com.pse.peer2learn.ui.activities

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.pse.peer2learn.R
import com.pse.peer2learn.utils.adapters.GroupsRecyclerViewAdapter
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class GroupOverviewTest {
    @Before
    fun setUp() {
        ActivityScenario.launch(SignInActivity::class.java)
        Thread.sleep(200)
        Espresso.onView(ViewMatchers.withId(R.id.sign_in_button)).perform(ViewActions.click())
        Thread.sleep(200)
        Espresso.onView(ViewMatchers.withId(R.id.home_activity))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Thread.sleep(1000)
        Espresso.onView(ViewMatchers.withId(R.id.groups_recycler_view)).perform(
            RecyclerViewActions.actionOnItemAtPosition<GroupsRecyclerViewAdapter.GroupsViewHolder>(
                0,
                ViewActions.click()
            )
        )
        Thread.sleep(200)
        Espresso.onView(ViewMatchers.withText("Overview")).perform(ViewActions.click())
    }

    @Test
    fun backNavigationTest() {
        Espresso.pressBack()
        Espresso.onView(ViewMatchers.withId(R.id.home_activity))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    @Test
    fun isFragmentDisplayedTest() {
        Espresso.onView(ViewMatchers.withId(R.id.group_overview_fragment))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    @Test
    fun isDescriptionTitleDisplayedTest() {
        Espresso.onView(ViewMatchers.withId(R.id.description_title))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Espresso.onView(ViewMatchers.withId(R.id.description_title))
            .check(ViewAssertions.matches(ViewMatchers.withText(R.string.description)))
    }

    @Test
    fun isDescriptionEditTextDisplayedTest() {
        Espresso.onView(ViewMatchers.withId(R.id.description_text_view))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    @Test
    fun isLanguageTitleDisplayedTest() {
        Espresso.onView(ViewMatchers.withId(R.id.language_title))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Espresso.onView(ViewMatchers.withId(R.id.language_title))
            .check(ViewAssertions.matches(ViewMatchers.withText(R.string.language)))
    }

    @Test
    fun isLanguageEditTextDisplayedTest() {
        Espresso.onView(ViewMatchers.withId(R.id.language_text_view))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    @Test
    fun isAccessCodeTitleDisplayedTest() {
        Espresso.onView(ViewMatchers.withId(R.id.access_code_title))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    @Test
    fun isAccessCodeTextViewDisplayedTest() {
        Espresso.onView(ViewMatchers.withId(R.id.access_code_text_view))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    @Test
    fun isProgressTitleDisplayedTest() {
        Espresso.onView(ViewMatchers.withId(R.id.progress_text_view))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    @Test
    fun isProgressSeekBarDisplayedTest() {
        Espresso.onView(ViewMatchers.withId(R.id.progress_seek_bar))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    @Test
    fun isMembersTitleDisplayedTest() {
        Espresso.onView(ViewMatchers.withId(R.id.members_title))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    @Test
    fun isAdmingHintTextDisplayedTest() {
        Espresso.onView(ViewMatchers.withId(R.id.update_admin_rights))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    @Test
    fun isMembersRecyclerViewDisplayedTest() {
        Espresso.onView(ViewMatchers.withId(R.id.members_recycler_view))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    @Test
    fun isSubmitButtonDisplayedTest() {
        Espresso.onView(ViewMatchers.withId(R.id.submit_button))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }
}