package com.example.dudu.data.workers.reminder

import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import com.example.dudu.data.TasksRepository

class ReminderWorkerFactory(
    private val repository: TasksRepository
) : WorkerFactory() {

    override fun createWorker(
        appContext: Context,
        workerClassName: String,
        workerParameters: WorkerParameters
    ): ListenableWorker? {
        return when (workerClassName) {
            TasksReminderWorker::class.java.name ->
                TasksReminderWorker(appContext, workerParameters, repository)
            else -> null
        }
    }
}