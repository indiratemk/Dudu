package com.example.dudu.data

import com.example.dudu.data.local.Task
import kotlinx.coroutines.flow.Flow

interface TasksRepository {

    fun getTasks(showDone: Boolean): Flow<List<Task>>

    fun getDoneTasksCount(): Flow<Int>

    suspend fun addTask(task: Task)

    suspend fun updateTask(task: Task)

    suspend fun removeTask(task: Task)
}