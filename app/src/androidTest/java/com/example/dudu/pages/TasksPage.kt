package com.example.dudu.pages

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import com.example.dudu.R
import com.example.dudu.ui.tasks.TaskVH

class TasksPage : PageObject() {

    override fun assertOn(): TasksPage {
        onView(withId(R.id.rvTasks)).check(matches(isDisplayed()))
        onView(withId(R.id.fabCreateTask)).check(matches(isDisplayed()))
        return this
    }

    fun clickOnTask(): TasksPage {
        onView(withId(R.id.rvTasks))
            .perform(RecyclerViewActions.actionOnItemAtPosition<TaskVH>(0, click()))
        return this
    }

    fun clickOnCreateTask(): TasksPage {
        onView(withId(R.id.fabCreateTask))
            .perform(click())
        return this
    }

    fun checkTaskIsUpdated(): TasksPage {
        onView(ViewMatchers.withText(R.string.main_task_updated_message))
            .check(matches(isDisplayed()))
        return this
    }

    fun checkTaskIsCreated(): TasksPage {
        onView(ViewMatchers.withText(R.string.main_task_create_message))
            .check(matches(isDisplayed()))
        return this
    }

    fun checkTaskIsRemoved(): TasksPage {
        onView(ViewMatchers.withText(R.string.main_task_removed_message))
            .check(matches(isDisplayed()))
        return this
    }

    companion object {
        fun obtain(): TasksPage {
            return TasksPage()
        }
    }
}