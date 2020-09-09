package com.pse.peer2learn.ui.activities

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.*
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.orchestrator.junit.BundleJUnitUtils.getDescription
import com.pse.peer2learn.R
import com.pse.peer2learn.utils.adapters.GroupsRecyclerViewAdapter
import org.hamcrest.CoreMatchers.endsWith
import org.hamcrest.core.AllOf.allOf
import org.junit.Test
import org.junit.runner.RunWith
import java.util.regex.Matcher
import kotlin.concurrent.thread

@RunWith(AndroidJUnit4::class)
class NavigationTest {
    @Test
    fun signInActivityNavigationTest() {
        ActivityScenario.launch(SignInActivity::class.java)
        Thread.sleep(200)
        onView(withId(R.id.sign_in_activity)).check(matches(isDisplayed()))
        Thread.sleep(200)
    }

    @Test
    fun profileActivityNavigationTest() {
        ActivityScenario.launch(SignInActivity::class.java)
        Thread.sleep(200)
        onView(withId(R.id.sign_in_button)).perform(click())
        Thread.sleep(200)
        onView(withId(R.id.profile_image_view)).perform(click())
        onView(withId(R.id.profile_activity)).check(matches(isDisplayed()))
    }

    @Test
    fun homeActivityNavigationTest() {
        ActivityScenario.launch(SignInActivity::class.java)
        Thread.sleep(200)
        onView(withId(R.id.sign_in_button)).perform(click())
        Thread.sleep(200)
        onView(withId(R.id.home_activity)).check(matches(isDisplayed()))
    }

    @Test
    fun homeMenuFragmentNavigationTest() {
        ActivityScenario.launch(SignInActivity::class.java)
        Thread.sleep(200)
        onView(withId(R.id.sign_in_button)).perform(click())
        Thread.sleep(200)
        onView(withId(R.id.home_activity)).check(matches(isDisplayed()))
        Thread.sleep(1000)
        onView(withId(R.id.groups_recycler_view))
            .perform(
                actionOnItemAtPosition<GroupsRecyclerViewAdapter.GroupsViewHolder>(
                    1,
                    clickItemWithId(R.id.menu_image_view)
                )
            )
        onView(withId(R.id.home_menu_fragment)).check(matches(isDisplayed()))
    }


    @Test
    fun groupActivityAndChatsFragmentNavigationTest() {
        ActivityScenario.launch(SignInActivity::class.java)
        Thread.sleep(200)
        onView(withId(R.id.sign_in_button)).perform(click())
        Thread.sleep(200)
        onView(withId(R.id.home_activity)).check(matches(isDisplayed()))
        Thread.sleep(1000)
        onView(withId(R.id.groups_recycler_view)).perform(
            actionOnItemAtPosition<GroupsRecyclerViewAdapter.GroupsViewHolder>(
                0,
                click()
            )
        )
        Thread.sleep(200)
        onView(withId(R.id.group_activity)).check(matches(isDisplayed()))
        onView(withId(R.id.chats_fragment)).check(matches(isDisplayed()))
    }

    @Test
    fun appointmentsOverviewFragmentNavigationTest() {
        ActivityScenario.launch(SignInActivity::class.java)
        Thread.sleep(200)
        onView(withId(R.id.sign_in_button)).perform(click())
        Thread.sleep(200)
        onView(withId(R.id.home_activity)).check(matches(isDisplayed()))
        Thread.sleep(1000)
        onView(withId(R.id.groups_recycler_view)).perform(
            actionOnItemAtPosition<GroupsRecyclerViewAdapter.GroupsViewHolder>(
                0,
                click()
            )
        )
        Thread.sleep(200)
        onView(withText("Appointments")).perform(click())
        onView(withId(R.id.appointments_overview_fragment)).check(matches(isDisplayed()))
    }

    @Test
    fun createAppointmentsFragmentNavigationTest() {
        ActivityScenario.launch(SignInActivity::class.java)
        Thread.sleep(200)
        onView(withId(R.id.sign_in_button)).perform(click())
        Thread.sleep(200)
        onView(withId(R.id.home_activity)).check(matches(isDisplayed()))
        Thread.sleep(1000)
        onView(withId(R.id.groups_recycler_view)).perform(
            actionOnItemAtPosition<GroupsRecyclerViewAdapter.GroupsViewHolder>(
                0,
                click()
            )
        )
        Thread.sleep(200)
        onView(withText("Appointments")).perform(click())
        onView(withId(R.id.add_category_button)).perform(click())
        onView(withId(R.id.create_appointment_fragment)).check(matches(isDisplayed()))
    }

    @Test
    fun groupOverviewFragmentNavigationTest() {
        ActivityScenario.launch(SignInActivity::class.java)
        Thread.sleep(200)
        onView(withId(R.id.sign_in_button)).perform(click())
        Thread.sleep(200)
        onView(withId(R.id.home_activity)).check(matches(isDisplayed()))
        Thread.sleep(1000)
        onView(withId(R.id.groups_recycler_view)).perform(
            actionOnItemAtPosition<GroupsRecyclerViewAdapter.GroupsViewHolder>(
                0,
                click()
            )
        )
        Thread.sleep(200)
        onView(withText("Overview")).perform(click())
        onView(withId(R.id.group_overview_fragment)).check(matches(isDisplayed()))
    }

    @Test
    fun groupSearchActivityNavigationTest() {
        ActivityScenario.launch(SignInActivity::class.java)
        Thread.sleep(200)
        onView(withId(R.id.sign_in_button)).perform(click())
        Thread.sleep(200)
        onView(withId(R.id.add_group_button)).perform(click())
        Thread.sleep(200)
        onView(withId(R.id.group_search_public_activity)).check(matches(isDisplayed()))
    }

    @Test
    fun groupSearchPrivateFragmentNavigationTest() {
        ActivityScenario.launch(SignInActivity::class.java)
        Thread.sleep(200)
        onView(withId(R.id.sign_in_button)).perform(click())
        Thread.sleep(200)
        onView(withId(R.id.add_group_button)).perform(click())
        Thread.sleep(200)
        onView(withId(R.id.group_search_public_activity)).check(matches(isDisplayed()))
        Thread.sleep(200)
        onView(
            allOf(
                withParent(withId(R.id.menu)),
                withClassName(endsWith("ImageView")),
                isDisplayed()
            )
        )
            .perform(click())
        Thread.sleep(200)
        onView(withId(R.id.private_search_button)).perform(click())
        Thread.sleep(200)
        onView(withId(R.id.group_search_private_fragment)).check(matches(isDisplayed()))
    }

    @Test
    fun createGroupActivityNavigationTest() {
        ActivityScenario.launch(SignInActivity::class.java)
        //sign in
        onView(withId(R.id.sign_in_button)).perform(click())
        Thread.sleep(200)
        //go to search group
        onView(withId(R.id.add_group_button)).perform(click())
        Thread.sleep(200)
        //click open floating menu
        onView(
            allOf(
                withParent(withId(R.id.menu)),
                withClassName(endsWith("ImageView")),
                isDisplayed()
            )
        )
            .perform(click())
        Thread.sleep(200)
        //open create group activity
        onView(withId(R.id.create_group_button)).perform(click())
        Thread.sleep(200)
        onView(withId(R.id.create_group_activity)).check(matches(isDisplayed()))
        Thread.sleep(200)

    }

    /**
     * Help method for recyclerView
     */
    private fun clickItemWithId(id: Int): ViewAction {
        return object : ViewAction {
            override fun getConstraints(): org.hamcrest.Matcher<View>? {
                return null
            }

            override fun getDescription(): String {
                return "Click on a child view with specified id."
            }

            override fun perform(uiController: UiController, view: View) {
                val v = view.findViewById(id) as View
                v.performClick()
            }
        }
    }
}