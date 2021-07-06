package com.example.dudu.data

import com.example.dudu.data.local.TaskEntity
import kotlinx.coroutines.flow.Flow

interface TasksRepository {

    fun getTasks(showDone: Boolean): Flow<List<TaskEntity>>

    fun getDoneTasksCount(): Flow<Int>

    suspend fun addTask(task: TaskEntity)

    suspend fun updateTask(task: TaskEntity)

    suspend fun removeTask(task: TaskEntity)
}