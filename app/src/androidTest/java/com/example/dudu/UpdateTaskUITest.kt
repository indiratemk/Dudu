package com.example.dudu

import androidx.test.ext.junit.rules.ActivityScenarioRule
import com.example.dudu.pages.TasksPage
import com.example.dudu.pages.UpdateTaskPage
import com.example.dudu.rules.MockServerRule
import com.example.dudu.rules.OkHttpIdlingResourceRule
import com.example.dudu.ui.tasks.TasksActivity
import org.junit.Rule
import org.junit.Test
import org.junit.rules.RuleChain

class UpdateTaskUITest {

    @get:Rule
    var ruleChain: RuleChain = RuleChain.outerRule(MockServerRule())
        .around(OkHttpIdlingResourceRule())
    @get:Rule
    var activityScenario: ActivityScenarioRule<TasksActivity> =
        ActivityScenarioRule(TasksActivity::class.java)


    @Test
    fun shouldUpdateTask() {
        TasksPage.obtain()
            .assertOn()
            .clickOnTask()

        UpdateTaskPage.obtain()
            .assertOn()
            .updateDescription()
            .saveChanges()

        TasksPage.obtain()
            .checkTaskIsUpdated()
    }
}