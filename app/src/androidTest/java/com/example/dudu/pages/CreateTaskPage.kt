package com.example.dudu.pages

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import com.example.dudu.R
import org.hamcrest.CoreMatchers.not

class CreateTaskPage : PageObject() {

    override fun assertOn(): CreateTaskPage {
        onView(withId(R.id.etDescription))
            .check(matches(isDisplayed()))
        onView(withId(R.id.spinnerPriority))
            .check(matches(isDisplayed()))
        onView(withId(R.id.deadlineLayout))
            .check(matches(isDisplayed()))
        onView(withId(R.id.btnRemove))
            .check(matches(not(isDisplayed())))
        return this
    }

    fun clearDescription(): CreateTaskPage {
        onView(withId(R.id.etDescription))
            .perform(ViewActions.clearText())
        return this
    }

    fun typeDescription(): CreateTaskPage {
        onView(withId(R.id.etDescription))
            .perform(ViewActions.clearText(), ViewActions.typeText("test description"))
        return this
    }

    fun saveTask(): CreateTaskPage {
        onView(withId(R.id.actionSave))
            .perform(ViewActions.click())
        return this
    }

    fun checkErrorIsShowed(): CreateTaskPage {
        onView(withId(R.id.tvDescriptionError))
            .check(matches(isDisplayed()))
        return this
    }

    companion object {
        fun obtain(): CreateTaskPage {
            return CreateTaskPage()
        }
    }
}