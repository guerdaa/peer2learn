package com.pse.peer2learn.ui.activities

import android.view.ViewManager
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.pse.peer2learn.R
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class HomeMenuFragmentTest {
    @Before
    fun setUp() {
        ActivityScenario.launch(SignInActivity::class.java)
        //Thread.sleep(2000)
        //Espresso.onView(ViewMatchers.withId(R.id.sign_in_button)).perform(ViewActions.click())
        Thread.sleep(2000)
        Espresso.onView(ViewMatchers.withId(R.id.home_activity))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Thread.sleep(1000)

        Espresso.onView(ViewMatchers.withId(R.id.menu_image_view)).perform(
            ViewActions.click()
        )
        Thread.sleep(1000)
    }

    @Test
    fun testOpenGroup() {
        Espresso.onView(ViewMatchers.withText("Open group")).perform(ViewActions.click())
        Thread.sleep(2000)
        Espresso.onView(ViewMatchers.withId(R.id.group_activity)).check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    @Test
    fun testLeaveGroup() {
        Espresso.onView(ViewMatchers.withText("Quit group")).check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }
}