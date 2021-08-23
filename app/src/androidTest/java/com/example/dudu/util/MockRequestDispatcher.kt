package com.example.dudu.util

import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.RecordedRequest

class MockRequestDispatcher : Dispatcher() {

    override fun dispatch(request: RecordedRequest?): MockResponse {
        val path = request!!.path
        val method = request.method
        return when {
            path.equals("/tasks/") && method == "POST" ->
                MockResponse()
                    .setResponseCode(200)
                    .setBody(FileReader.readStringFromFile("create_task_response.json"))
            path.equals("/tasks/") && method == "GET" ->
                MockResponse()
                    .setResponseCode(200)
                    .setBody(FileReader.readStringFromFile("tasks_response.json"))
            path.contains("/tasks/\\d".toRegex()) && method == "PUT" ->
                MockResponse()
                    .setResponseCode(200)
                    .setBody(FileReader.readStringFromFile("update_task_response.json"))
            else -> MockResponse().setResponseCode(404)
        }
    }
}