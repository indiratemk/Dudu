package com.example.dudu.util.workers

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.work.CoroutineWorker
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.example.dudu.R
import com.example.dudu.data.TasksRepository
import com.example.dudu.ui.MainActivity
import com.example.dudu.util.Constants
import com.example.dudu.util.DateFormatter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.*
import java.util.concurrent.TimeUnit

class TasksReminderWorker(
    private val appContext: Context,
    workerParams: WorkerParameters,
    private val repository: TasksRepository
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        return withContext(Dispatchers.IO) {
            val tasksCount = repository.getTasksByDeadlineCount(DateFormatter.getCurrentDateInSeconds())
            if (tasksCount > 0)
                sendReminderNotification(tasksCount)

            scheduleWork()

            Result.success()
        }
    }

    private fun sendReminderNotification(size: Int) {
        val notificationManager =
            appContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                appContext.getString(R.string.channel_id),
                appContext.getString(R.string.channel_name),
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = appContext.getString(R.string.channel_description)
            }
            notificationManager.createNotificationChannel(channel)
        }

        val intent = Intent(appContext, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent: PendingIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PendingIntent.getActivity(appContext,
                0, intent, PendingIntent.FLAG_IMMUTABLE)
        } else {
            PendingIntent.getActivity(appContext,
                0, intent, 0)
        }

        val builder = NotificationCompat.Builder(appContext, appContext.getString(R.string.channel_id))
            .setSmallIcon(R.drawable.ic_checked)
            .setColor(ContextCompat.getColor(appContext, R.color.green))
            .setContentTitle(appContext.getString(R.string.notification_title))
            .setContentText(appContext.resources.getQuantityString(R.plurals.notification_content, size, size))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        notificationManager.notify(1, builder.build())
    }

    private fun scheduleWork() {
        val reminderWorkRequest = OneTimeWorkRequestBuilder<TasksReminderWorker>()
            .setInitialDelay(getWorkRepetitionTimeInMillis(), TimeUnit.MILLISECONDS)
            .addTag(Constants.REMINDER_WORK_TAG)
            .build()
        WorkManager.getInstance(appContext)
            .enqueue(reminderWorkRequest)
    }

    companion object {
        fun getWorkRepetitionTimeInMillis(): Long {
            val currentDate = Calendar.getInstance()
            val notificationDate = Calendar.getInstance()
            notificationDate.set(Calendar.HOUR_OF_DAY, 6)
            notificationDate.set(Calendar.MINUTE, 0)
            notificationDate.set(Calendar.SECOND, 0)
            if (notificationDate.before(currentDate)) {
                notificationDate.add(Calendar.HOUR_OF_DAY, 24)
            }
            return notificationDate.timeInMillis.minus(currentDate.timeInMillis)
        }
    }
}