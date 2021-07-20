package com.example.dudu.data

import com.example.dudu.data.helpers.RequestManager
import com.example.dudu.data.helpers.Resource
import com.example.dudu.data.helpers.mapFromTaskToDto
import com.example.dudu.data.local.LocalDataSource
import com.example.dudu.data.models.Task
import com.example.dudu.data.remote.RemoteDataSource
import com.example.dudu.data.remote.dtos.SyncTasksDto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * Репозиторий решает откуда брать данные из локального или удаленного источника и как
 * синхронизировать.
 */

class TasksRepositoryImpl @Inject constructor(
    private val localSource: LocalDataSource,
    private val remoteSource: RemoteDataSource,
    private val requestManager: RequestManager
) : TasksRepository {

    override fun getTasks(
        showDone: Boolean,
        shouldFetchRemote: Boolean
    ): Flow<Resource<List<Task>>> {
        return requestManager.executeRequest(
            makeRequest = { remoteSource.getTasks() },
            onAfterRequest = { localSource.getTasks(showDone) },
            onSynchronization = { tasks -> localSource.refreshTasks(tasks) },
            shouldFetchRemote
        ).flowOn(Dispatchers.IO)
    }

    override fun getDoneTasksCount(): Flow<Int> {
        return localSource.getDoneTasksCount()
            .flowOn(Dispatchers.IO)
    }

    override suspend fun addTask(task: Task): Resource<Task> {
        return withContext(Dispatchers.IO) {
            requestManager.executeRequest(
                onBeforeRequest = { localSource.addTask(task) },
                makeRequest = { remoteSource.createTask(task) },
                onRevertOptimisticUpdate = { localSource.removeTask(task) },
                onSaveRequest = {
                    localSource.updateUnsyncTask(task.id)
                    task
                },
                onSynchronization = { synchronizeTasks() }
            )
        }
    }

    override suspend fun updateTask(task: Task): Resource<Task> {
        return withContext(Dispatchers.IO) {
            val prevTask = localSource.getTask(task.id)

            requestManager.executeRequest(
                onBeforeRequest = { localSource.updateTask(task) },
                makeRequest = { remoteSource.updateTask(task) },
                onRevertOptimisticUpdate = { localSource.updateTask(prevTask) },
                onSaveRequest = {
                    localSource.updateUnsyncTask(task.id)
                    task
                },
                onSynchronization = { synchronizeTasks() }
            )
        }
    }

    override suspend fun removeTask(task: Task): Resource<Task> {
        return withContext(Dispatchers.IO) {
            requestManager.executeRequest(
                makeRequest = { remoteSource.removeTask(task) },
                onAfterRequest = { localSource.removeTask(task) },
                onSaveRequest = {
                    localSource.removeUnsyncTask(task.id)
                    task
                },
                onSynchronization = { synchronizeTasks() }
            )
        }
    }

    override suspend fun shouldSynchronizeTasks(): Boolean {
        return withContext(Dispatchers.IO) {
            localSource.shouldSynchronizeTasks()
        }
    }

    override suspend fun synchronizeTasks(): Resource<List<Task>> {
        return withContext(Dispatchers.IO) {
            val deletedTasksIds = localSource.getUnsyncDeletedTasksIds()
            val updatedTasks = localSource.getUnsyncUpdatedTasks()
            val syncTasks = SyncTasksDto(
                deletedTasksIds,
                updatedTasks.map { mapFromTaskToDto(it) }
            )

            requestManager.executeRequest(
                makeRequest = { remoteSource.synchronizeTasks(syncTasks) },
                onSynchronization = {
                    localSource.removeUnsyncTasks()
                    localSource.refreshTasks(it)
                }
            )
        }
    }

    override suspend fun getTasksByDeadlineCount(deadline: Long): Int {
        return withContext(Dispatchers.IO) {
            localSource.getTasksByDeadlineCount(deadline)
        }
    }
}