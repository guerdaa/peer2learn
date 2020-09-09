package com.pse.peer2learn.ui.activities

import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.pse.peer2learn.R
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class GroupTest {

    @Test
    fun backNavigationTest() {
        Espresso.pressBack()
        Thread.sleep(200)
        Espresso.onView(ViewMatchers.withId(R.id.home_activity))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    @Test
    fun backButtonNavigationTest() {
        Espresso.onView(ViewMatchers.withId(R.id.back)).perform(ViewActions.click())
        Thread.sleep(200)
        Espresso.onView(ViewMatchers.withId(R.id.home_activity))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    @Test
    fun showChatFragmentTest() {
        Espresso.onView(ViewMatchers.withId(R.id.chat_text_view)).perform(ViewActions.click())
        Thread.sleep(200)
        Espresso.onView(ViewMatchers.withId(R.id.chats_fragment))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    @Test
    fun showOverviewFragmentTest() {
        Espresso.onView(ViewMatchers.withId(R.id.overview_text_view)).perform(ViewActions.click())
        Thread.sleep(200)
        Espresso.onView(ViewMatchers.withId(R.id.group_overview_fragment))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    @Test
    fun showAppointmentFragmentTest() {
        Espresso.onView(ViewMatchers.withId(R.id.appointments_text_view)).perform(ViewActions.click())
        Thread.sleep(200)
        Espresso.onView(ViewMatchers.withId(R.id.appointments_overview_fragment))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }
}