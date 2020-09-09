package com.pse.peer2learn.ui.activities

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.*
import androidx.test.espresso.NoMatchingViewException
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.pse.peer2learn.R
import com.pse.peer2learn.utils.adapters.GroupsRecyclerViewAdapter
import org.hamcrest.CoreMatchers.endsWith
import org.hamcrest.core.AllOf.allOf
import org.junit.After
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ChatTest {


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
    fun joinGroup() {
        Thread.sleep(300)
        onView(withId(R.id.add_group_button)).perform(click())
        Thread.sleep(300)
        onView(withId(R.id.search_edit_text)).perform(ViewActions.typeText("la2"))
        Thread.sleep(500)
        onView(withId(R.id.submit_button)).perform(click())
        Thread.sleep(500)
        onView(withText("JOIN")).perform(click())
        Thread.sleep(2000)

    }



    @Test
    fun sendMessage() {
        Thread.sleep(500)
        onView(withId(R.id.groups_recycler_view)).perform(
            RecyclerViewActions.actionOnItemAtPosition<GroupsRecyclerViewAdapter.GroupsViewHolder>(
                0,
                click()
            )
        )
        onView(withId(R.id.send_edit_text)).perform(ViewActions.typeText("Der EspressoTest Funktioniert!!"))
        Thread.sleep(500)
        onView(withId(R.id.send_button)).perform(click())
        Thread.sleep(3000)
    }
}
