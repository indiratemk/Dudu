package com.example.dudu.data

import com.example.dudu.data.helpers.RequestManager
import com.example.dudu.data.helpers.Resource
import com.example.dudu.data.helpers.mapFromTaskToDto
import com.example.dudu.data.local.LocalDataSource
import com.example.dudu.data.models.Task
import com.example.dudu.data.remote.RemoteDataSource
import com.example.dudu.data.remote.dtos.SyncTasksDto
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

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
        )
    }

    override fun getDoneTasksCount(): Flow<Int> {
        return localSource.getDoneTasksCount()
    }

    override suspend fun addTask(task: Task): Resource<Task> {
        return requestManager.executeRequest(
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

    override suspend fun updateTask(task: Task): Resource<Task> {
        val prevTask = localSource.getTask(task.id)

        return requestManager.executeRequest(
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

    override suspend fun removeTask(task: Task): Resource<Task> {
        return requestManager.executeRequest(
            makeRequest = { remoteSource.removeTask(task) },
            onAfterRequest = { localSource.removeTask(task) },
            onSaveRequest = {
                localSource.removeUnsyncTask(task.id)
                task
            },
            onSynchronization = { synchronizeTasks() }
        )
    }

    override suspend fun shouldSynchronizeTasks(): Boolean {
        return localSource.shouldSynchronizeTasks()
    }

    override suspend fun synchronizeTasks(): Resource<List<Task>> {
        val deletedTasksIds = localSource.getUnsyncDeletedTasksIds()
        val updatedTasks = localSource.getUnsyncUpdatedTasks()
        val syncTasks = SyncTasksDto(
            deletedTasksIds,
            updatedTasks.map { mapFromTaskToDto(it) }
        )
        return requestManager.executeRequest(
            makeRequest = { remoteSource.synchronizeTasks(syncTasks) },
            onSynchronization = {
                localSource.removeUnsyncTasks()
                localSource.refreshTasks(it)
            }
        )
    }

    override suspend fun getTasksByDeadlineCount(deadline: Long): Int {
        return localSource.getTasksByDeadlineCount(deadline)
    }
}