package com.pse.peer2learn.ui.activities

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.pse.peer2learn.R
import org.hamcrest.CoreMatchers
import org.hamcrest.core.AllOf
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class GroupSearchPrivateTest {
    @Before
    fun setUp() {
        ActivityScenario.launch(SignInActivity::class.java)
        Thread.sleep(200)
        onView(withId(R.id.sign_in_button)).perform(ViewActions.click())
        Thread.sleep(200)
        onView(withId(R.id.add_group_button)).perform(ViewActions.click())
        Thread.sleep(200)
        onView(withId(R.id.group_search_public_activity))
            .check(matches(isDisplayed()))
        Thread.sleep(200)
        onView(
            AllOf.allOf(
                ViewMatchers.withParent(withId(R.id.menu)),
                ViewMatchers.withClassName(CoreMatchers.endsWith("ImageView")),
                isDisplayed()
            )
        )
            .perform(ViewActions.click())
        Thread.sleep(200)
        onView(withId(R.id.private_search_button))
            .perform(ViewActions.click())
        Thread.sleep(200)
    }

    @Test
    fun backNavigationTest() {
        Espresso.pressBack()
        onView(withId(R.id.group_search_public_activity)).check(matches(isDisplayed()))
    }

    @Test
    fun isFragmentDisplayed() {
        onView(withId(R.id.group_search_private_fragment))
            .check(matches(isDisplayed()))
    }

    @Test
    fun isBackgroundDisplayed() {
        onView(withId(R.id.search_private_background_image_view))
            .check(matches(isDisplayed()))
    }

    @Test
    fun isSearchTitleDisplayed() {
        onView(withId(R.id.search_a_private_group_title_text_view))
            .check(matches(isDisplayed()))
    }

    @Test
    fun isSearchEditTextDisplayed() {
        onView(withId(R.id.search_private_edit_text))
            .check(matches(isDisplayed()))
    }

    @Test
    fun isSubmitButtonDisplayed() {
        onView(withId(R.id.submit_button))
            .check(matches(isDisplayed()))
    }
}