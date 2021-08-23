package com.example.dudu.pages

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import com.example.dudu.R

class UpdateTaskPage : PageObject() {

    override fun assertOn(): UpdateTaskPage {
        onView(withId(R.id.etDescription))
            .check(matches(isDisplayed()))
        onView(withId(R.id.spinnerPriority))
            .check(matches(isDisplayed()))
        onView(withId(R.id.deadlineLayout))
            .check(matches(isDisplayed()))
        onView(withId(R.id.btnRemove))
            .check(matches(isDisplayed()))
        return this
    }

    fun updateDescription(): UpdateTaskPage {
        onView(withId(R.id.etDescription))
            .perform(typeText(" test description"))
        return this
    }

    fun saveChanges(): UpdateTaskPage {
        onView(withId(R.id.actionSave))
            .perform(ViewActions.click())
        return this
    }

    companion object {
        fun obtain(): UpdateTaskPage {
            return UpdateTaskPage()
        }
    }
}