package com.example.dudu.data.local

import kotlinx.coroutines.flow.Flow

class LocalDataSource(
    private val taskDao: TaskDao
) {

    fun getTasks(showDone: Boolean): Flow<List<TaskEntity>> {
        return taskDao.getTasks(showDone)
    }

    fun getDoneTasksCount(): Flow<Int> {
        return taskDao.getDoneTasksCount()
    }

    suspend fun addTask(task: TaskEntity) {
        taskDao.insertTask(task)
    }

    suspend fun updateTask(task: TaskEntity) {
        taskDao.updateTask(task)
    }

    suspend fun removeTask(task: TaskEntity) {
        taskDao.deleteTask(task)
    }
}