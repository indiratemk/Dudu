package com.example.dudu.data.workers.synchronization

import android.content.Context
import android.util.Log
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit

object TasksSynchronizationWorkManager {

    private const val SYNCHRONIZATION_WORK = "SYNCHRONIZATION_WORK"

    fun scheduleWork(context: Context) {
        showCurrentWorks(context)
        if (isContainWork(context))
            return

        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val synchronizationWorkRequest =
            PeriodicWorkRequestBuilder<TasksSynchronizationWorker>(8,
                TimeUnit.HOURS, 15, TimeUnit.MINUTES)
                .setConstraints(constraints)
                .addTag(SYNCHRONIZATION_WORK)
                .build()
        WorkManager.getInstance(context)
            .enqueue(synchronizationWorkRequest)
    }

    private fun showCurrentWorks(context: Context) {
        val works = WorkManager.getInstance(context)
            .getWorkInfosByTag(SYNCHRONIZATION_WORK).get()
        if (works.isNotEmpty()) {
            works.filter { it.state != WorkInfo.State.CANCELLED }
                .forEach {
                    Log.d(SYNCHRONIZATION_WORK, "Work ${it.id}: ${it.state}")
                }
        }
    }

    private fun isContainWork(context: Context): Boolean {
        val works = WorkManager.getInstance(context)
            .getWorkInfosByTag(SYNCHRONIZATION_WORK).get()
        if (works.isNotEmpty()) {
            return works.any {
                it.state == WorkInfo.State.ENQUEUED || it.state == WorkInfo.State.RUNNING
            }
        }
        return false
    }
}