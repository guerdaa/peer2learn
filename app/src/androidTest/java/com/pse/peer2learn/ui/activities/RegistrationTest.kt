package com.pse.peer2learn.ui.activities

import android.view.KeyEvent
import android.view.View
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.*
import androidx.test.espresso.ViewAction
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.pse.peer2learn.R
import com.pse.peer2learn.utils.adapters.GroupsRecyclerViewAdapter
import org.hamcrest.CoreMatchers.anything
import org.hamcrest.CoreMatchers.endsWith
import org.hamcrest.core.AllOf.allOf
import org.junit.AfterClass
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.concurrent.thread


/**Vorraussetzungen für diesen Testfall: Man darf noch nicht registriert sein.
 * Der Account wird danach direkt gelöscht.**/
@RunWith(AndroidJUnit4::class)
class RegistrationTest {

    @Test
    fun registrationTest() {
        ActivityScenario.launch(SignInActivity::class.java)
        Thread.sleep(200)
        onView(withText("Sign in")).perform(click())
        Thread.sleep(5000)
        onView(withId(R.id.username_edit_text)).perform(typeText("A2utomc25al42ly User123"))
        closeSoftKeyboard()
        Thread.sleep(500)
        onView(withId(R.id.university_edit_text)).perform(typeText("KIT"))
        closeSoftKeyboard()
        Thread.sleep(500)
        onView(withId(R.id.study_course_edit_text)).perform(typeText("B.Sc. Informatik"))
        closeSoftKeyboard()
        Thread.sleep(500)
        onView(withId(R.id.submit_button)).perform(click())
        Thread.sleep(1000)
        onView(withId(R.id.profile_image_view)).perform(click())
        Thread.sleep(1000)
        onView(withId(R.id.settings_button)).perform(click())
        Thread.sleep(1000)
        onView(withText("Delete account")).perform(click())
        Thread.sleep(2000)
    }
}
