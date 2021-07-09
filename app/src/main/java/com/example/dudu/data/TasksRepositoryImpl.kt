package com.example.dudu.data

import com.example.dudu.data.helpers.Resource
import com.example.dudu.data.helpers.networkManagerFromAction
import com.example.dudu.data.helpers.networkManagerFromFlow
import com.example.dudu.data.local.LocalDataSource
import com.example.dudu.data.models.Task
import com.example.dudu.data.remote.RemoteDataSource
import kotlinx.coroutines.flow.Flow

class TasksRepositoryImpl(
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
            localRequest = { localSource.addTask(task) },
            remoteRequest = { remoteSource.createTask(task) },
            revertDataRequest = { localSource.removeTask(task) }
        )
    }

    override suspend fun updateTask(task: Task): Resource<Task> {
        val prevTask = localSource.getTask(task.id)
        return networkManagerFromAction(
            localRequest = { localSource.updateTask(task) },
            remoteRequest = { remoteSource.updateTask(task) },
            revertDataRequest = { localSource.updateTask(prevTask) }
        )
    }

    override suspend fun removeTask(task: Task): Resource<Task> {
        return networkManagerFromAction(
            localRequest = {},
            remoteRequest = { remoteSource.removeTask(task) },
            revertDataRequest = {},
            syncDataIfNeeded = { localSource.removeTask(task) }
        )
    }
}