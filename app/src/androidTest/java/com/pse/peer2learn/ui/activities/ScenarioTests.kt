package com.pse.peer2learn.ui.activities

import android.app.Activity
import android.view.View
import android.widget.TimePicker
import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.PickerActions
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry.getInstrumentation
import androidx.test.runner.lifecycle.ActivityLifecycleMonitorRegistry
import androidx.test.runner.lifecycle.Stage
import com.pse.peer2learn.R
import com.pse.peer2learn.utils.adapters.CategoriesRecyclerViewAdapter
import com.pse.peer2learn.utils.adapters.GroupsRecyclerViewAdapter
import com.pse.peer2learn.utils.adapters.ResultsGroupsRecyclerViewAdapter
import junit.framework.Assert.assertEquals
import junit.framework.Assert.assertNotNull
import org.hamcrest.CoreMatchers
import org.hamcrest.Matchers
import org.hamcrest.core.AllOf
import org.junit.Test
import org.junit.runner.RunWith
import java.util.*


@RunWith(AndroidJUnit4::class)

class ScenarioTests {
    /**
     * Log In
     */
    @Test
    fun tf110Test() {
        ActivityScenario.launch(SignInActivity::class.java)
        Thread.sleep(200)
        onView(withId(R.id.sign_in_button)).perform(click())
        Thread.sleep(200)
        onView(withId(R.id.home_activity))
            .check(matches(isDisplayed()))
    }

    /**
     * log out
     */
    @Test
    fun ztf120Test() {
        ActivityScenario.launch(SignInActivity::class.java)
        Thread.sleep(200)
        onView(withId(R.id.sign_in_button)).perform(click())
        Thread.sleep(200)
        onView(withId(R.id.profile_image_view)).perform(click())
        onView(withId(R.id.profile_activity))
            .check(matches(isDisplayed()))
        onView(withId(R.id.settings_button)).perform(click())
        Thread.sleep(200)
        onView(withText("Sign out")).perform(click())
        Thread.sleep(200)
        onView(withId(R.id.sign_in_activity)).check(matches(isDisplayed()))
    }

    /**
     * Einsehen der beigetreten Lerngruppen
     * Vorbedingung : Der Bunutzer hat mindestens eine Gruppe beigetretten
     */
    @Test
    fun tf200Test() {
        ActivityScenario.launch(SignInActivity::class.java)
        Thread.sleep(200)
        onView(withId(R.id.sign_in_button)).perform(click())
        Thread.sleep(1000)
        var recyclerViewAfter: RecyclerView = this.getCurrentActivity()
            ?.findViewById<RecyclerView>(R.id.groups_recycler_view) as RecyclerView
        var countAfter = recyclerViewAfter.adapter?.itemCount
        assertNotNull(countAfter)
    }

    /**
     * Profil bearbeiten
     */
    @Test
    fun tf210Test() {
        ActivityScenario.launch(SignInActivity::class.java)
        Thread.sleep(200)
        onView(withId(R.id.sign_in_button)).perform(click())
        Thread.sleep(200)
        onView(withId(R.id.profile_image_view)).perform(click())
        Thread.sleep(200)
        onView(withId(R.id.username_edit_text)).perform(
            replaceText("newUsername"),
            closeSoftKeyboard()
        )
        Thread.sleep(200)
        onView(withId(R.id.submit_button)).perform(click())
        Thread.sleep(1000)
        onView(withId(R.id.profile_image_view)).perform(click())
        onView(withId(R.id.username_edit_text)).check(matches(withText("newUsername")));
    }

    /**
     * Lerngruppe erstellen
     */
    @Test
    fun tf220Test() {
        ActivityScenario.launch(SignInActivity::class.java)
        Thread.sleep(200)
        //sign in
        onView(withId(R.id.sign_in_button)).perform(click())
        Thread.sleep(2000)
        //getting count of recyclerView before
        var recyclerViewBefore: RecyclerView = this.getCurrentActivity()
            ?.findViewById<RecyclerView>(R.id.groups_recycler_view) as RecyclerView
        var countBefore = recyclerViewBefore.adapter?.itemCount
        //go to search group
        onView(withId(R.id.add_group_button)).perform(click())
        Thread.sleep(200)
        //click open floating menu
        onView(
            AllOf.allOf(
                withParent(withId(R.id.menu)),
                withClassName(CoreMatchers.endsWith("ImageView")),
                isDisplayed()
            )
        )
            .perform(click());
        Thread.sleep(200)
        //open create group activity
        onView(withId(R.id.create_group_button)).perform(click())
        Thread.sleep(200)
        onView(withId(R.id.module_edit_text)).perform(
            replaceText("testgroup"),
            closeSoftKeyboard()
        )
        Thread.sleep(1000)
        onView(withId(R.id.description_edit_text)).perform(
            replaceText("testdescription"),
            closeSoftKeyboard()
        )
        Thread.sleep(100)
        onView(withId(R.id.language_edit_text)).perform(
            replaceText("testlanguage"),
            closeSoftKeyboard()
        )
        Thread.sleep(100)
        onView(withId(R.id.submit_button)).perform(click())
        Thread.sleep(1000)
        onView(withId(R.id.home_activity)).check(matches(isDisplayed()))
        Thread.sleep(2500)
        //Testing the recyclerView
        var recyclerViewAfter: RecyclerView = this.getCurrentActivity()
            ?.findViewById<RecyclerView>(R.id.groups_recycler_view) as RecyclerView
        var countAfter = recyclerViewAfter.adapter?.itemCount
        if (countBefore != null) {
            assertEquals(countBefore + 1, countAfter)
        }
    }

    /**
     * Lerngruppe verlassen
     * Vorbedingung : Der Bunutzer hat mindestens eine Gruppe beigetretten
     */
    @Test
    fun tf240Test() {
        ActivityScenario.launch(SignInActivity::class.java)
        Thread.sleep(200)
        onView(withId(R.id.sign_in_button)).perform(click())
        Thread.sleep(200)
        onView(withId(R.id.home_activity)).check(matches(isDisplayed()))
        Thread.sleep(1000)
        var recyclerViewBefore: RecyclerView = this.getCurrentActivity()
            ?.findViewById<RecyclerView>(R.id.groups_recycler_view) as RecyclerView
        var countBefore = recyclerViewBefore.adapter?.itemCount
        onView(withId(R.id.groups_recycler_view))
            .perform(
                RecyclerViewActions.actionOnItemAtPosition<GroupsRecyclerViewAdapter.GroupsViewHolder>(
                    0,
                    clickItemWithId(R.id.menu_image_view)
                )
            )
        onView(withId(R.id.quit_group_text_view)).perform(click())
        Thread.sleep(1000)
        var recyclerViewAfter: RecyclerView = this.getCurrentActivity()
            ?.findViewById<RecyclerView>(R.id.groups_recycler_view) as RecyclerView
        var countAfter = recyclerViewAfter.adapter?.itemCount
        if (countBefore != null) {
            assertEquals(countBefore - 1, countAfter)
        }
    }

    /**
     * Letzter Admin verlasst die Gruppe
     * Vorbedinung : Der Bunutzer hat mindestens eine Gruppe beigetretten und ist letzter Mitglied und Admin dieser Gruppe
     */
    @Test
    fun tf250Test() {
        //search for the a group that we are already inside
        ActivityScenario.launch(SignInActivity::class.java)
        Thread.sleep(200)
        //sign in
        onView(withId(R.id.sign_in_button)).perform(click())
        Thread.sleep(2000)
        //go to search group
        onView(withId(R.id.add_group_button)).perform(click())
        Thread.sleep(200)
        onView(withId(R.id.search_edit_text)).perform(
            replaceText("testgroup"),
            closeSoftKeyboard()
        )
        onView(withId(R.id.submit_button)).perform(click())
        Thread.sleep(2000)
        var recyclerViewBefore: RecyclerView = this.getCurrentActivity()
            ?.findViewById<RecyclerView>(R.id.results_group_recycler_view) as RecyclerView
        var countBefore = recyclerViewBefore.adapter?.itemCount
        Espresso.pressBack()
        //home menu and leave group
        Thread.sleep(1000)
        onView(withId(R.id.groups_recycler_view))
            .perform(
                RecyclerViewActions.actionOnItemAtPosition<GroupsRecyclerViewAdapter.GroupsViewHolder>(
                    0,
                    clickItemWithId(R.id.menu_image_view)
                )
            )
        onView(withId(R.id.quit_group_text_view)).perform(click())
        Thread.sleep(1000)
        //search for the a group that we quited
        //go to search group
        onView(withId(R.id.add_group_button)).perform(click())
        Thread.sleep(200)
        onView(withId(R.id.search_edit_text)).perform(
            replaceText("testgroup"),
            closeSoftKeyboard()
        )
        onView(withId(R.id.submit_button)).perform(click())
        Thread.sleep(2000)
        var recyclerViewAfter: RecyclerView = this.getCurrentActivity()
            ?.findViewById<RecyclerView>(R.id.results_group_recycler_view) as RecyclerView
        var countAfter = recyclerViewAfter.adapter?.itemCount
        if (countBefore != null) {
            assertEquals(countBefore - 1, countAfter)
        }
    }

    /**
     * Lerngruppe suchen
     * Vorbedingung : Die Gruppe "gbi" existiert
     */
    @Test
    fun tf260Test() {
        ActivityScenario.launch(SignInActivity::class.java)
        Thread.sleep(200)
        //sign in
        onView(withId(R.id.sign_in_button)).perform(click())
        Thread.sleep(2000)
        //go to search group
        onView(withId(R.id.add_group_button)).perform(click())
        Thread.sleep(200)
        onView(withId(R.id.search_edit_text)).perform(
            replaceText("gbi"),
            closeSoftKeyboard()
        )
        onView(withId(R.id.submit_button)).perform(click())
        Thread.sleep(2000)
        var recyclerView: RecyclerView = this.getCurrentActivity()
            ?.findViewById<RecyclerView>(R.id.results_group_recycler_view) as RecyclerView
        var count = recyclerView.adapter?.itemCount
        assertNotNull(count)
    }

    /**
     *  Lerngruppe beitreten
     *  Vorbedingung : Der Benutzer ist kein Mitglied der Gruppe "gbi" und sie existriert
     */
    @Test
    fun tf230Test() {
        ActivityScenario.launch(SignInActivity::class.java)
        Thread.sleep(200)
        //sign in
        onView(withId(R.id.sign_in_button)).perform(click())
        Thread.sleep(2000)
        var recyclerViewBefore: RecyclerView = this.getCurrentActivity()
            ?.findViewById<RecyclerView>(R.id.groups_recycler_view) as RecyclerView
        var countBefore = recyclerViewBefore.adapter?.itemCount
        //go to search group
        onView(withId(R.id.add_group_button)).perform(click())
        Thread.sleep(200)
        onView(withId(R.id.search_edit_text)).perform(
            replaceText("gbi"),
            closeSoftKeyboard()
        )
        onView(withId(R.id.submit_button)).perform(click())
        Thread.sleep(200)
        onView(withId(R.id.results_group_recycler_view)).perform(
            RecyclerViewActions.actionOnItemAtPosition<ResultsGroupsRecyclerViewAdapter.GroupsViewHolder>(
                0,
                click()
            )
        )
    }

    /**
     * Lerngruppe bearbeiten
     * Vorbedingung : Der Bunutzer hat mindestens eine Gruppe beigetretten
     */
    @Test
    fun tf300Test() {
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
        onView(withText("Overview")).perform(click())
        Thread.sleep(200)
        onView(withId(R.id.description_text_view)).perform(
            replaceText("Description from espresso"),
            closeSoftKeyboard()
        )
        Thread.sleep(200)
        onView(withId(R.id.submit_button)).perform(click())
        Thread.sleep(200)
        Espresso.pressBack()
        Thread.sleep(2000)
        onView(withId(R.id.groups_recycler_view)).perform(
            RecyclerViewActions.actionOnItemAtPosition<GroupsRecyclerViewAdapter.GroupsViewHolder>(
                0,
                click()
            )
        )
        Thread.sleep(200)
        onView(withText("Overview")).perform(click())
        onView(withId(R.id.description_text_view))
            .check(matches(withText("Description from espresso")))
    }

    /**
     * Termine erstellen
     * Vorbedingung : Der Bunutzer hat mindestens eine Gruppe beigetretten
     */
    @Test
    fun tf350Test() {
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
        Thread.sleep(200)
        onView(withId(R.id.category_name_edit_text)).perform(
            replaceText("categorytest"),
            closeSoftKeyboard()
        )
        onView(withId(R.id.add_appointment)).perform(click())
        Thread.sleep(200)
        onView(withId(R.id.appointment_name_edit_text)).perform(
            replaceText("appointmenttest"),
            closeSoftKeyboard()
        )
        val c: Calendar = Calendar.getInstance()
        var hour = c.get(Calendar.HOUR)
        var minutes = c.get(Calendar.MINUTE)
        onView(withId(R.id.time_text_view)).perform(click())
        Thread.sleep(1000)
        onView(
            withClassName(
                Matchers.equalTo(
                    TimePicker::class.java.name
                )
            )
        ).perform(PickerActions.setTime(hour + 15, minutes + 2))
        onView(withId(android.R.id.button1)).perform(click())
        Thread.sleep(1000)
        onView(withId(R.id.submit_button)).perform(click())
    }

    /**
     * Kategorien verwalten
     * Vorbedingung : Der Bunutzer hat mindestens eine Gruppe beigetretten
     */
    @Test
    fun tf360Test() {
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
        onView(withId(R.id.categories_recycler_view))
            .perform(
                RecyclerViewActions.actionOnItemAtPosition<CategoriesRecyclerViewAdapter.CategoriesViewHolder>(
                    0,
                    clickItemWithId(R.id.edit_image_view)
                )
            )
        onView(withId(R.id.category_name_edit_text)).perform(
            replaceText("categorynewname"),
            closeSoftKeyboard()
        )
        onView(withId(R.id.submit_button)).perform(click())
    }
    /**
     * Termin Löschen
     * Vorbedingung : Der Bunutzer hat mindestens eine Gruppe beigetretten
     */
    @Test
    fun tf380Test() {
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
        onView(withId(R.id.categories_recycler_view))
            .perform(
                RecyclerViewActions.actionOnItemAtPosition<CategoriesRecyclerViewAdapter.CategoriesViewHolder>(
                    0,
                    clickItemWithId(R.id.edit_image_view)
                )
            )
        Thread.sleep(1000)
        onView(withId(R.id.appointments_holder)).perform(longClick())
        Thread.sleep(1000)
        onView(withId(R.id.submit_button)).perform(click())
    }
    /**
     * Termin Errinerung
     * Vorbedingung : Der Bunutzer hat mindestens eine Gruppe beigetretten
     */
    @Test
    fun tf370Test() {
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
        onView(withId(R.id.categories_recycler_view))
            .perform(
                RecyclerViewActions.actionOnItemAtPosition<CategoriesRecyclerViewAdapter.CategoriesViewHolder>(
                    0,
                    longClickItemWithId(R.id.appointments_holder)
                )
            )
        Thread.sleep(2000)
    }

    /**
     * Help method to get current activity
     */
    private fun getCurrentActivity(): Activity? {
        var currentActivity: Activity? = null
        getInstrumentation().runOnMainSync {
            run {
                currentActivity =
                    ActivityLifecycleMonitorRegistry.getInstance().getActivitiesInStage(
                        Stage.RESUMED
                    ).elementAtOrNull(0)
            }
        }
        return currentActivity
    }
    /**
     * Help method for recyclerView
     */
    private fun longClickItemWithId(id: Int): ViewAction {
        return object : ViewAction {
            override fun getConstraints(): org.hamcrest.Matcher<View>? {
                return null
            }

            override fun getDescription(): String {
                return "Long click on a child view with specified id."
            }

            override fun perform(uiController: UiController, view: View) {
                val v = view.findViewById(id) as View
                v.performLongClick()
            }
        }
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