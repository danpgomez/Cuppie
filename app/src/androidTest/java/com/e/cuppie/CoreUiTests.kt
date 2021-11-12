package com.e.cuppie

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@SmallTest
class CoreUiTests {

    @get:Rule
    val activityRule = ActivityScenarioRule(MapsActivity::class.java)

    @Test
    fun test_navigatingToFavoritesFragment() {
        onView(withContentDescription("More options")).perform(click())
        onView(withText("Favorites")).perform(click())
        onView(withId(R.id.favoritesConstraintLayout)).check(matches(isDisplayed()))
    }
}
