package com.example.dudu

import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.dudu.pages.CreateTaskPage
import com.example.dudu.pages.TasksPage
import com.example.dudu.rules.MockServerRule
import com.example.dudu.rules.OkHttpIdlingResourceRule
import com.example.dudu.ui.tasks.TasksActivity
import org.junit.Rule
import org.junit.Test
import org.junit.rules.RuleChain
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CreateTaskUITest {

    @get:Rule
    var ruleChain: RuleChain = RuleChain.outerRule(MockServerRule())
        .around(OkHttpIdlingResourceRule())
    @get:Rule
    var activityScenario: ActivityScenarioRule<TasksActivity> =
        ActivityScenarioRule(TasksActivity::class.java)

    @Test
    fun shouldShowErrorWhenSaveTaskWithEmptyDescription() {
        TasksPage.obtain()
            .assertOn()
            .clickOnCreateTask()

        CreateTaskPage.obtain()
            .assertOn()
            .clearDescription()
            .saveTask()
            .checkErrorIsShowed()
    }


    @Test
    fun shouldSaveTaskWhenTaskWithNotEmptyDescription() {
        TasksPage.obtain()
            .assertOn()
            .clickOnCreateTask()

        CreateTaskPage.obtain()
            .assertOn()
            .clearDescription()
            .typeDescription()
            .saveTask()

        TasksPage.obtain()
            .assertOn()
            .checkTaskIsCreated()
    }
}