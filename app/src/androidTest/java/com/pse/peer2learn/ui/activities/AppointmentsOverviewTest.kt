package com.pse.peer2learn.ui.activities

import android.widget.DatePicker
import android.widget.TimePicker
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.ViewAssertion
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.PickerActions
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.pse.peer2learn.R
import com.pse.peer2learn.utils.adapters.CategoriesRecyclerViewAdapter
import com.pse.peer2learn.utils.adapters.GroupsRecyclerViewAdapter
import org.hamcrest.Matchers
import org.junit.*
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AppointmentsOverviewTest {

    @Before
    fun setUp() {
        ActivityScenario.launch(SignInActivity::class.java)
        Thread.sleep(1000)
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
        Espresso.onView(ViewMatchers.withText("Appointments")).perform(ViewActions.click())
        Thread.sleep(200)
        Espresso.onView(ViewMatchers.withId(R.id.add_category_button)).perform(ViewActions.click())
        Thread.sleep(200)
        onView(withId(R.id.category_name_edit_text)).perform(ViewActions.typeText("Test"), ViewActions.closeSoftKeyboard())
        onView(withId(R.id.add_appointment)).perform(ViewActions.click())
        onView(withId(R.id.appointment_name_edit_text)).perform(ViewActions.typeText("Besprechung"), ViewActions.closeSoftKeyboard())
        Thread.sleep(200)

        // Select Date
        onView(withId(R.id.date_text_view)).perform(ViewActions.click())
        Thread.sleep(200)
        onView(ViewMatchers.withClassName(Matchers.equalTo(DatePicker::class.java.name)))
            .perform(PickerActions.setDate(2022, 5, 5))
        onView(withText("OK")).perform(ViewActions.click())
        Thread.sleep(200)
        onView(withId(R.id.date_text_view)).check(ViewAssertions.matches(ViewMatchers.withText("05.05.2022")))

        // Select Time
        onView(withId(R.id.time_text_view)).perform(ViewActions.click())
        Thread.sleep(200)
        onView(ViewMatchers.withClassName(Matchers.equalTo(TimePicker::class.java.name)))
            .perform(PickerActions.setTime(12, 12))
        onView(withText("OK")).perform(ViewActions.click())
        Thread.sleep(200)
        onView(withId(R.id.time_text_view)).check(ViewAssertions.matches(withText("12:12")))

        onView(withId(R.id.submit_button)).perform(ViewActions.click())
        Thread.sleep(2000)

    }


    @Test
    fun appointmentIsDisplayedTest(){

        onView(withId(R.id.category_name_text_view)).check(matches(withText("TEST")))
    }

    @After
    fun cleanUp() {
        ActivityScenario.launch(SignInActivity::class.java)
        Thread.sleep(1000)
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
        Espresso.onView(ViewMatchers.withText("Appointments")).perform(ViewActions.click())
        Thread.sleep(500)

        onView(withId(R.id.delete_image_view)).perform(ViewActions.click())


    }
}
