package com.pse.peer2learn.ui.activities

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.*
import androidx.test.espresso.NoMatchingViewException
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.pse.peer2learn.R
import org.hamcrest.CoreMatchers.endsWith
import org.hamcrest.core.AllOf.allOf
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ProfileTest {

    @Before
    fun createAccount(){
        ActivityScenario.launch(SignInActivity::class.java)
        Thread.sleep(2000)
        try {
            onView(withText("Sort by")).check(matches(isDisplayed()))

        } catch (e: NoMatchingViewException) {
            onView(withText("Sign in")).perform(click())
            Thread.sleep(5000)
            onView(withId(R.id.username_edit_text)).perform(ViewActions.typeText("A2utomc25al42ly User123"))
            closeSoftKeyboard()
            Thread.sleep(500)
            onView(withId(R.id.university_edit_text)).perform(ViewActions.typeText("KIT"))
            closeSoftKeyboard()
            Thread.sleep(500)
            onView(withId(R.id.study_course_edit_text)).perform(ViewActions.typeText("B.Sc. Informatik"))
            closeSoftKeyboard()
            Thread.sleep(500)
            onView(withId(R.id.submit_button)).perform(click())
            Thread.sleep(1000)
        }
    }


    @Test
    fun changeProfile() {
        onView(withId(R.id.profile_image_view)).perform(click())
        Thread.sleep(1000)
        onView(withId(R.id.username_edit_text)).perform(ViewActions.clearText())
        onView(withId(R.id.username_edit_text)).perform(ViewActions.typeText("New Name"))
        closeSoftKeyboard()
        Thread.sleep(1000)
        onView(withId(R.id.study_course_edit_text)).perform(ViewActions.clearText())
        onView(withId(R.id.study_course_edit_text)).perform(ViewActions.typeText("Franz"))
        closeSoftKeyboard()
        Thread.sleep(1000)
        onView(withId(R.id.university_edit_text)).perform(ViewActions.clearText())
        onView(withId(R.id.university_edit_text)).perform(ViewActions.typeText("TUM"))
        closeSoftKeyboard()
        Thread.sleep(1000)
        onView(withId(R.id.submit_button)).perform(click())
        Thread.sleep(2000)
    }


    @After
    fun deleteAccount() {
        onView(withId(R.id.profile_image_view)).perform(click())
        Thread.sleep(1000)
        onView(withId(R.id.settings_button)).perform(click())
        Thread.sleep(1000)
        onView(withText("Delete account")).perform(click())
        Thread.sleep(2000)
    }
}
