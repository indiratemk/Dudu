package com.example.dudu.data

import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import com.example.dudu.util.TasksSynchronizationWorker

class SynchronizationWorkerFactory(
    private val repository: TasksRepository
) : WorkerFactory() {

    override fun createWorker(
        appContext: Context,
        workerClassName: String,
        workerParameters: WorkerParameters
    ): ListenableWorker? {
        return when (workerClassName) {
            TasksSynchronizationWorker::class.java.name ->
                TasksSynchronizationWorker(appContext, workerParameters, repository)
            else -> null
        }
    }
}