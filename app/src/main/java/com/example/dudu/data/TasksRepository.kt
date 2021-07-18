package com.example.dudu.data

import com.example.dudu.data.helpers.Resource
import com.example.dudu.data.models.Task
import kotlinx.coroutines.flow.Flow

interface TasksRepository {

    fun getTasks(showDone: Boolean, shouldFetchRemote: Boolean): Flow<Resource<List<Task>>>

    fun getDoneTasksCount(): Flow<Int>

    suspend fun addTask(task: Task): Resource<Task>

    suspend fun updateTask(task: Task): Resource<Task>

    suspend fun removeTask(task: Task): Resource<Task>

    suspend fun shouldSynchronizeTasks(): Boolean

    suspend fun synchronizeTasks(): Resource<List<Task>>

    suspend fun getTasksByDeadlineCount(deadline: Long): Int
}