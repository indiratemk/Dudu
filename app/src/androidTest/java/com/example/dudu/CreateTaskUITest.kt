package com.example.dudu

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.dudu.rules.MockServerRule
import com.example.dudu.rules.OkHttpIdlingResourceRule
import com.example.dudu.ui.task.CreateTaskActivity
import com.example.dudu.util.Constants
import com.example.dudu.util.MockRequestDispatcher
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import okhttp3.mockwebserver.MockWebServer
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.rules.RuleChain
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CreateTaskUITest {

    private val mockWebServer = MockWebServer()

    @get:Rule
    var ruleChain: RuleChain = RuleChain.outerRule(MockServerRule(mockWebServer))
        .around(OkHttpIdlingResourceRule())
    @get:Rule
    var activityScenario: ActivityScenarioRule<CreateTaskActivity> =
        ActivityScenarioRule(CreateTaskActivity::class.java)

    @Test
    fun shouldShowErrorWhenSaveTaskWithEmptyDescription() {
        onView(withId(R.id.etDescription))
            .perform(clearText())
        onView(withId(R.id.actionSave))
            .perform(click())

        onView(withId(R.id.tvDescriptionError))
            .check(matches(isDisplayed()))
    }


    @Test
    fun shouldSaveTaskWhenSaveTaskWithNotEmptyDescription() {
        mockWebServer.setDispatcher(MockRequestDispatcher())

        onView(withId(R.id.etDescription))
            .perform(clearText(), typeText("test description"))
        onView(withId(R.id.actionSave))
            .perform(click())

        assertEquals(Constants.RESULT_TASK_CREATED, activityScenario.scenario.result.resultCode)
        runBlocking { delay(300) }
    }
}