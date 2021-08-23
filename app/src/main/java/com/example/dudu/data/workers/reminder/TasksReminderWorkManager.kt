package com.example.dudu.data.workers.reminder

import android.content.Context
import android.util.Log
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import java.util.*
import java.util.concurrent.TimeUnit

object TasksReminderWorkManager {

    private const val REMINDER_WORK = "REMINDER_WORK"

    fun scheduleWork(context: Context) {
        showCurrentWorks(context)
        if (isContainWork(context))
            return

        val currentDate = Calendar.getInstance()
        val notificationDate = Calendar.getInstance()
        notificationDate.set(Calendar.HOUR_OF_DAY, 6)
        notificationDate.set(Calendar.MINUTE, 0)
        notificationDate.set(Calendar.SECOND, 0)
        if (notificationDate.before(currentDate)) {
            notificationDate.add(Calendar.HOUR_OF_DAY, 24)
        }
        val delay = notificationDate.timeInMillis - currentDate.timeInMillis

        val reminderWorkRequest = OneTimeWorkRequestBuilder<TasksReminderWorker>()
            .setInitialDelay(delay, TimeUnit.MILLISECONDS)
            .addTag(REMINDER_WORK)
            .build()
        WorkManager.getInstance(context)
            .enqueue(reminderWorkRequest)
    }

    private fun showCurrentWorks(context: Context) {
        val works = WorkManager.getInstance(context)
            .getWorkInfosByTag(REMINDER_WORK).get()
        if (works.isNotEmpty()) {
            works.filter { it.state != WorkInfo.State.CANCELLED }
                .forEach {
                    Log.d(REMINDER_WORK, "Work ${it.id}: ${it.state}")
                }
        }
    }

    private fun isContainWork(context: Context): Boolean {
        val works = WorkManager.getInstance(context)
            .getWorkInfosByTag(REMINDER_WORK).get()
        if (works.isNotEmpty()) {
            return works.any {
                it.state == WorkInfo.State.ENQUEUED || it.state == WorkInfo.State.RUNNING
            }
        }
        return false
    }
}