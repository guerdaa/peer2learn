package com.pse.peer2learn.ui.activities

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.*
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.pse.peer2learn.R
import org.hamcrest.CoreMatchers.endsWith
import org.hamcrest.core.AllOf.allOf
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CreateGroupTest {
    @Before
    fun setUp() {
        ActivityScenario.launch(SignInActivity::class.java)
        Thread.sleep(200)
        //sign in
        onView(withId(R.id.sign_in_button)).perform(click())
        Thread.sleep(2000)
        ActivityScenario.launch(CreateGroupActivity::class.java)
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
            .perform(click());
        Thread.sleep(200)
        //open create group activity
        onView(withId(R.id.create_group_button)).perform(click())
    }

    @Test
    fun backNavigationTest() {
        pressBack()
        onView(withId(R.id.home_activity)).check(matches(isDisplayed()))
    }

    @Test
    fun testActivityCorrectView() {
        onView(withId(R.id.create_group_activity))
            .check(matches(isDisplayed()))
    }

    @Test
    fun isPeer2LearnDisplayedTest() {
        onView(withId(R.id.Peer2Learn_image_view))
            .check(matches(isDisplayed()))
    }

    @Test
    fun isProfileIconDisplayedTest() {
        onView(withId(R.id.profile_image_view))
            .check(matches(isDisplayed()))
    }

    @Test
    fun isTitleBarIconDisplayedTest() {
        onView(withId(R.id.title_bar))
            .check(matches(isDisplayed()))
    }

    @Test
    fun isGroupTextViewDisplayedAndCorrectTest() {
        onView(withId(R.id.groups_text_view))
            .check(matches(isDisplayed()))
        //check if text matches
        onView(withId(R.id.groups_text_view))
            .check(matches(withText(R.string.create_group_title)))
    }

    @Test
    fun isModuleLayoutDisplayedTest() {
        onView(withId(R.id.module_layout))
            .check(matches(isDisplayed()))
    }

    @Test
    fun isModuleTextDisplayedAndCorrectTest() {
        onView(withId(R.id.name_of_the_module_text_title_text_view))
            .check(matches(isDisplayed()))
        //check if text matches
        onView(withId(R.id.name_of_the_module_text_title_text_view))
            .check(matches(withText(R.string.name_of_module)))
    }

    @Test
    fun isModuleEditTextDisplayedTest() {
        onView(withId(R.id.module_edit_text))
            .check(matches(isDisplayed()))
    }

    @Test
    fun isDescriptionLayoutDisplayedTest() {
        onView(withId(R.id.description_layout))
            .check(matches(isDisplayed()))
    }

    @Test
    fun isDescriptionTextDisplayedAndCorrectTest() {
        onView(withId(R.id.description_text_title))
            .check(matches(isDisplayed()))
        //check if text matches
        onView(withId(R.id.description_text_title))
            .check(matches(withText(R.string.description)))
    }

    @Test
    fun isDescriptionEditTextDisplayedTest() {
        onView(withId(R.id.description_edit_text))
            .check(matches(isDisplayed()))
    }

    @Test
    fun isMainLayoutDisplayedTest() {
        onView(withId(R.id.number_of_users_layout))
            .check(matches(isDisplayed()))
    }

    @Test
    fun isNumberOfUsersTextDisplayedAndCorrectTest() {
        onView(withId(R.id.number_of_users_title))
            .check(matches(isDisplayed()))
        //check if text matches
        onView(withId(R.id.number_of_users_title))
            .check(matches(withText(R.string.number_of_users)))
    }

    @Test
    fun isNumberOfTextEditTextDisplayedTest() {
        onView(withId(R.id.number_of_users_text_view))
            .check(matches(isDisplayed()))
    }

    @Test
    fun isDecrementButtonDisplayedTest() {
        onView(withId(R.id.decrement_button))
            .check(matches(isDisplayed()))
    }

    @Test
    fun isPriveGroupTitleDisplayedTest() {
        onView(withId(R.id.private_group_title))
            .check(matches(isDisplayed()))
    }

    @Test
    fun isPriveGroupSwitchDisplayedTest() {
        onView(withId(R.id.private_group_switch_button))
            .check(matches(isDisplayed()))
    }

    @Test
    fun isAccessCodeLayoutDisplayedTest() {
        onView(withId(R.id.access_code_layout))
            .check(matches(isDisplayed()))
    }

    @Test
    fun isAccessCodeTextTitleDisplayedTest() {
        onView(withId(R.id.access_code_text_title))
            .check(matches(isDisplayed()))
    }

    @Test
    fun isAccessCodeTextViewDisplayedTest() {
        onView(withId(R.id.access_code_text_view))
            .check(matches(isDisplayed()))
    }

    @Test
    fun isLanguageLayoutDisplayedTest() {
        onView(withId(R.id.language_layout))
            .check(matches(isDisplayed()))
    }

    @Test
    fun isLanguageTextTitleDisplayedTest() {
        onView(withId(R.id.language_text_title))
            .check(matches(isDisplayed()))
    }

    @Test
    fun isLanguageEditTextDisplayedTest() {
        onView(withId(R.id.language_edit_text))
            .check(matches(isDisplayed()))
    }

    @Test
    fun isSubmitButtonDisplayedTest() {
        onView(withId(R.id.submit_button))
            .check(matches(isDisplayed()))
    }
}