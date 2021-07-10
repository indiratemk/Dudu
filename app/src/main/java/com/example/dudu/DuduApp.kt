package com.example.dudu

import android.app.Application
import com.example.dudu.di.AppComponent
import com.example.dudu.di.DaggerAppComponent

class DuduApp : Application() {

    companion object {
        lateinit var appComponent: AppComponent
    }

    override fun onCreate() {
        super.onCreate()

        appComponent = DaggerAppComponent.factory()
            .create(application = this)
    }
}