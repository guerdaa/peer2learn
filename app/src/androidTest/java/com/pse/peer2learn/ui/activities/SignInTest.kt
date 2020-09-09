package com.pse.peer2learn.ui.activities

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.pse.peer2learn.R
import org.junit.Rule
import  org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SignInTest {
    @get: Rule
    val activityRule = ActivityScenarioRule(SignInActivity::class.java)

    @Test
    fun testActivityCorrectView() {
        onView(withId(R.id.sign_in_activity))
            .check(matches(isDisplayed()))
    }
    @Test
    fun isBackgroundDisplayed() {
        onView(withId(R.id.sign_in_activity_background))
            .check(matches(isDisplayed()))
    }
    @Test
    fun isSignInButtonDisplayed() {
        onView(withId(R.id.sign_in_button))
            .check(matches(isDisplayed()))
    }

}
