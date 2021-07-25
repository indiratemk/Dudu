package com.example.dudu.rules

import com.example.dudu.util.MockRequestDispatcher
import okhttp3.mockwebserver.MockWebServer
import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement

class MockServerRule : TestRule {

    private val mockWebServer = MockWebServer()

    override fun apply(base: Statement?, description: Description?): Statement =
        object : Statement() {
            override fun evaluate() {
                mockWebServer.start(8080)
                mockWebServer.setDispatcher(MockRequestDispatcher())
                try {
                    base?.evaluate()
                } finally {
                    mockWebServer.shutdown()
                }
            }
        }
}