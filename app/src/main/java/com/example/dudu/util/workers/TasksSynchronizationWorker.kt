package com.example.dudu.util.workers

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.dudu.data.TasksRepository
import com.example.dudu.data.helpers.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class TasksSynchronizationWorker(
    appContext: Context,
    workerParams: WorkerParameters,
    private val repository: TasksRepository
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        return withContext(Dispatchers.IO) {
            when (repository.synchronizeTasks()) {
                is Resource.Loaded -> Result.success()
                else -> Result.failure()
            }
        }
    }
}