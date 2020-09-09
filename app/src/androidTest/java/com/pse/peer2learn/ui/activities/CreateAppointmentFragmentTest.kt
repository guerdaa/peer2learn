package com.pse.peer2learn.ui.activities

import android.widget.DatePicker
import android.widget.TimePicker
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.PickerActions
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.pse.peer2learn.R
import com.pse.peer2learn.utils.adapters.GroupsRecyclerViewAdapter
import org.hamcrest.Matchers
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class CreateAppointmentFragmentTest {

    @Before
    fun setUp() {
        ActivityScenario.launch(SignInActivity::class.java)
        Thread.sleep(200)
        onView(withId(R.id.sign_in_button)).perform(click())
        Thread.sleep(200)
        onView(withId(R.id.home_activity)).check(matches(isDisplayed()))
        Thread.sleep(1000)
        onView(withId(R.id.groups_recycler_view)).perform(
            RecyclerViewActions.actionOnItemAtPosition<GroupsRecyclerViewAdapter.GroupsViewHolder>(
                0,
                click()
            )
        )
        Thread.sleep(200)
        onView(withText("Appointments")).perform(click())
        Thread.sleep(200)
        onView(withId(R.id.add_category_button)).perform(click())
    }


    @Test
    fun pickDateTest() {
        onView(withId(R.id.add_appointment)).perform(click())
        Thread.sleep(200)
        onView(withId(R.id.date_text_view)).perform(click())
        Thread.sleep(200)
        onView(withClassName(Matchers.equalTo(DatePicker::class.java.name)))
            .perform(PickerActions.setDate(2022, 5, 5))
        onView(withText("OK")).perform(click());
        Thread.sleep(200)

        onView(withId(R.id.date_text_view)).check(matches(withText("05.05.2022")))
    }

    @Test
    fun pickTimeTest() {
        onView(withId(R.id.add_appointment)).perform(click())
        Thread.sleep(200)
        onView(withId(R.id.time_text_view)).perform(click())

        onView(withClassName(Matchers.equalTo(TimePicker::class.java.name)))
            .perform(PickerActions.setTime(12, 12))
        onView(withText("OK")).perform(click());
        Thread.sleep(200)

        onView(withId(R.id.time_text_view)).check(matches(withText("12:12")))
    }

}