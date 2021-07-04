package com.example.dudu.data

import com.example.dudu.data.local.Task
import com.example.dudu.data.local.TaskDao
import kotlinx.coroutines.flow.Flow

class TasksRepositoryImpl(
    private val taskDao: TaskDao
) : TasksRepository {

    override fun getTasks(showDone: Boolean): Flow<List<Task>> {
        return taskDao.getTasks(showDone)
    }

    override fun getDoneTasksCount(): Flow<Int> {
        return taskDao.getDoneTasksCount()
    }

    override suspend fun addTask(task: Task) {
        taskDao.insertTask(task)
    }

    override suspend fun updateTask(task: Task) {
        taskDao.updateTask(task)
    }

    override suspend fun removeTask(task: Task) {
        taskDao.deleteTask(task)
    }
}