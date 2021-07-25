package com.example.dudu.util

import android.util.Log
import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.RecordedRequest

class MockRequestDispatcher : Dispatcher() {

    override fun dispatch(request: RecordedRequest?): MockResponse {
        if (request!!.path.equals("/tasks/") && request.method == "POST") {
            return MockResponse()
                .setResponseCode(200)
                .setBody(FileReader.readStringFromFile("create_task_success_response.json"))
        }
        return MockResponse().setResponseCode(404)
    }
}