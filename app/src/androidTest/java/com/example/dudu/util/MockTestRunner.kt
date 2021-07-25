package com.example.dudu.util

import android.app.Application
import android.content.Context
import androidx.test.runner.AndroidJUnitRunner
import com.example.dudu.DuduAppTest

class MockTestRunner : AndroidJUnitRunner() {

    override fun newApplication(
        cl: ClassLoader?,
        className: String?,
        context: Context?
    ): Application {
        return super.newApplication(cl, DuduAppTest::class.java.name, context)
    }
}