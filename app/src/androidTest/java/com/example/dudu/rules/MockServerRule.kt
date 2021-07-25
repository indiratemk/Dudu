package com.example.dudu.rules

import okhttp3.mockwebserver.MockWebServer
import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement

class MockServerRule(
    private val mockWebServer: MockWebServer
) : TestRule {

    override fun apply(base: Statement?, description: Description?): Statement =
        object : Statement() {
            override fun evaluate() {
                mockWebServer.start(8080)
                try {
                    base?.evaluate()
                } finally {
                    mockWebServer.shutdown()
                }
            }
        }
}