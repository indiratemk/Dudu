package com.example.dudu.data

import com.example.dudu.data.local.LocalDataSource
import com.example.dudu.data.local.TaskEntity
import com.example.dudu.data.remote.RemoteDataSource
import kotlinx.coroutines.flow.Flow

class TasksRepositoryImpl(
    private val localSource: LocalDataSource,
    private val remoteSource: RemoteDataSource
) : TasksRepository {

    override fun getTasks(showDone: Boolean): Flow<List<TaskEntity>> {
        return localSource.getTasks(showDone)
    }

    override fun getDoneTasksCount(): Flow<Int> {
        return localSource.getDoneTasksCount()
    }

    override suspend fun addTask(task: TaskEntity) {
        localSource.addTask(task)
    }

    override suspend fun updateTask(task: TaskEntity) {
        localSource.updateTask(task)
    }

    override suspend fun removeTask(task: TaskEntity) {
        localSource.removeTask(task)
    }
}