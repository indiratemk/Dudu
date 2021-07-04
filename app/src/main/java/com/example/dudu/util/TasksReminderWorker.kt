package com.example.dudu.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.work.*
import com.example.dudu.R
import com.example.dudu.data.local.Task
import com.example.dudu.ui.MainActivity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.File
import java.io.FileInputStream
import java.util.*
import java.util.concurrent.TimeUnit

class TasksReminderWorker(
    private val appContext: Context,
    workerParams: WorkerParameters
) : Worker(appContext, workerParams) {

    override fun doWork(): Result {
        val tasks = getCachedTasks()
        val currentDateInMillis = DateFormatter.getCurrentDateWithoutTime().time

//        val todayTasks = tasks.filter {
//            it.deadline != null &&
//                    it.deadline == currentDateInMillis &&
//                    !it.isDone
//        }

//        if (todayTasks.isNotEmpty())
        sendReminderNotification(1)
        scheduleWork()

        return Result.success()
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
        val currentDate = Calendar.getInstance()
        val notificationDate = Calendar.getInstance()
        notificationDate.set(Calendar.HOUR_OF_DAY, 6)
        notificationDate.set(Calendar.MINUTE, 0)
        notificationDate.set(Calendar.SECOND, 0)
        if (notificationDate.before(currentDate)) {
            notificationDate.add(Calendar.HOUR_OF_DAY, 24)
        }
        val timeDiff = notificationDate.timeInMillis.minus(currentDate.timeInMillis)
        val dailyWorkRequest = OneTimeWorkRequestBuilder<TasksReminderWorker>()
            .setInitialDelay(timeDiff, TimeUnit.MILLISECONDS)
            .build()
        WorkManager.getInstance(applicationContext)
            .enqueue(dailyWorkRequest)
    }

    private fun getCachedTasks(): List<Task> {
        val tasks = mutableListOf<Task>()
        try {
            val cachePath = appContext.cacheDir
            val inputStream =
                FileInputStream(File("$cachePath/${Constants.FILE_NAME}${Constants.FILE_EXTENSION}"))
            val size: Int = inputStream.available()
            val buffer = ByteArray(size)
            inputStream.use { it.read(buffer) }

            val json = String(buffer, Charsets.UTF_8)
            val type = object : TypeToken<List<Task>>() {}.type
            tasks.addAll(Gson().fromJson<List<Task>>(json, type))
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return tasks
    }
}