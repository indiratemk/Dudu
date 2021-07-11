package com.example.dudu

import android.app.Application
import androidx.work.Configuration
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.dudu.data.DuduDelegatingWorkerFactory
import com.example.dudu.data.local.SharedPrefs
import com.example.dudu.di.AppComponent
import com.example.dudu.di.DaggerAppComponent
import com.example.dudu.util.Constants
import com.example.dudu.util.TasksReminderWorker
import java.util.concurrent.TimeUnit
import javax.inject.Inject


class DuduApp : Application(), Configuration.Provider {

    @Inject
    lateinit var workerFactory: DuduDelegatingWorkerFactory
    @Inject
    lateinit var sharedPrefs: SharedPrefs

    override fun onCreate() {
        super.onCreate()

        appComponent = DaggerAppComponent.factory()
            .create(application = this)
        appComponent.inject(this)

        scheduleWorks()
    }

    override fun getWorkManagerConfiguration(): Configuration {
        return Configuration.Builder()
            .setMinimumLoggingLevel(android.util.Log.INFO)
            .setWorkerFactory(workerFactory)
            .build()
    }

    private fun scheduleWorks() {
        if (sharedPrefs.isAppFirstOpened) {
            val reminderWorkRequest = OneTimeWorkRequestBuilder<TasksReminderWorker>()
            .setInitialDelay(TasksReminderWorker.getWorkRepetitionTimeInMillis(),
                TimeUnit.MILLISECONDS)
                .addTag(Constants.REMINDER_WORK_TAG)
                .build()
            WorkManager.getInstance(this)
                .enqueue(reminderWorkRequest)

            sharedPrefs.isAppFirstOpened = false
        }
    }

    companion object {
        lateinit var appComponent: AppComponent
    }
}