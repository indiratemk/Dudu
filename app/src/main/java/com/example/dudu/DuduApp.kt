package com.example.dudu

import android.app.Application
import androidx.work.Configuration
import com.example.dudu.di.AppComponent
import com.example.dudu.di.DaggerAppComponent
import com.example.dudu.data.workers.DuduDelegatingWorkerFactory
import com.example.dudu.data.workers.reminder.TasksReminderWorkManager
import com.example.dudu.data.workers.synchronization.TasksSynchronizationWorkManager
import javax.inject.Inject


class DuduApp : Application(), Configuration.Provider {

    lateinit var appComponent: AppComponent
        private set
    @Inject
    lateinit var workerFactory: DuduDelegatingWorkerFactory

    override fun onCreate() {
        super.onCreate()

        appComponent = DaggerAppComponent.factory()
            .create(application = this)
        appComponent.inject(this)

        TasksReminderWorkManager.scheduleWork(this)
        TasksSynchronizationWorkManager.scheduleWork(this)
    }

    override fun getWorkManagerConfiguration(): Configuration {
        return Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()
    }
}