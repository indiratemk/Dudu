package com.example.dudu

import android.app.Application
import androidx.work.Configuration
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.dudu.data.local.SharedPrefs
import com.example.dudu.di.AppComponent
import com.example.dudu.di.DaggerAppComponent
import com.example.dudu.util.workers.DuduDelegatingWorkerFactory
import com.example.dudu.util.workers.TasksReminderWorker
import com.example.dudu.util.workers.TasksSynchronizationWorker
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
            .setWorkerFactory(workerFactory)
            .setMinimumLoggingLevel(android.util.Log.DEBUG)
            .build()
    }

    private fun scheduleWorks() {
        if (sharedPrefs.isAppFirstOpened) {
            scheduleReminderWork()
            scheduleSynchronizationWork()
            sharedPrefs.isAppFirstOpened = false
        }
    }

    private fun scheduleReminderWork() {
        val reminderWorkRequest = OneTimeWorkRequestBuilder<TasksReminderWorker>()
            .setInitialDelay(TasksReminderWorker.getWorkRepetitionTimeInMillis(),
                TimeUnit.MILLISECONDS)
            .addTag(TasksReminderWorker.REMINDER_WORK_TAG)
            .build()
        WorkManager.getInstance(this)
            .enqueue(reminderWorkRequest)
    }

    private fun scheduleSynchronizationWork() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val synchronizationWorkRequest =
            PeriodicWorkRequestBuilder<TasksSynchronizationWorker>(8,
                TimeUnit.HOURS, 15, TimeUnit.MINUTES)
                .setConstraints(constraints)
                .build()
        WorkManager.getInstance(this)
            .enqueue(synchronizationWorkRequest)
    }

    companion object {
        lateinit var appComponent: AppComponent
    }
}