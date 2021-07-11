package com.example.dudu.data

import com.example.dudu.data.helpers.Resource
import com.example.dudu.data.helpers.mapFromTaskToDto
import com.example.dudu.data.helpers.networkManagerFromAction
import com.example.dudu.data.helpers.networkManagerFromFlow
import com.example.dudu.data.local.LocalDataSource
import com.example.dudu.data.models.Task
import com.example.dudu.data.remote.RemoteDataSource
import com.example.dudu.data.remote.dtos.SyncTasksDto
import com.example.dudu.di.scopes.AppScope
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@AppScope
class TasksRepositoryImpl @Inject constructor(
    private val localSource: LocalDataSource,
    private val remoteSource: RemoteDataSource
) : TasksRepository {

    override fun getTasks(
        showDone: Boolean,
        shouldFetchRemote: Boolean
    ): Flow<Resource<List<Task>>> {
        return networkManagerFromFlow(
            localRequest = { localSource.getTasks(showDone) },
            remoteRequest = { remoteSource.getTasks() },
            saveRemoteResult = { tasks -> localSource.refreshTasks(tasks) },
            shouldFetchRemote
        )
    }

    override fun getDoneTasksCount(): Flow<Int> {
        return localSource.getDoneTasksCount()
    }

    override suspend fun addTask(task: Task): Resource<Task> {
        return networkManagerFromAction(
            onLocalRequest = { localSource.addTask(task) },
            onRemoteRequest = { remoteSource.createTask(task) },
            onRevertDataRequest = { localSource.removeTask(task) },
            onNetworkError = {
                localSource.updateUnsyncTask(task.id)
                task
            }
        )
    }

    override suspend fun updateTask(task: Task): Resource<Task> {
        val prevTask = localSource.getTask(task.id)
        return networkManagerFromAction(
            onLocalRequest = { localSource.updateTask(task) },
            onRemoteRequest = { remoteSource.updateTask(task) },
            onRevertDataRequest = { localSource.updateTask(prevTask) },
            onNetworkError = {
                localSource.updateUnsyncTask(task.id)
                task
            }
        )
    }

    override suspend fun removeTask(task: Task): Resource<Task> {
        return networkManagerFromAction(
            onLocalRequest = {},
            onRemoteRequest = { remoteSource.removeTask(task) },
            onRevertDataRequest = {},
            onSyncDataIfNeeded = { localSource.removeTask(task) },
            onNetworkError = {
                localSource.removeUnsyncTask(task.id)
                task
            }
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
        return networkManagerFromAction(
            onRemoteRequest = { remoteSource.synchronizeTasks(syncTasks) },
            onSyncData = {
                localSource.removeUnsyncTasks()
                localSource.refreshTasks(it)
            }
        )
    }
}