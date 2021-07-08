package com.example.dudu.data.local

import com.example.dudu.data.helpers.mapFromEntityToTask
import com.example.dudu.data.helpers.mapFromTaskToEntity
import com.example.dudu.data.models.Task
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

class LocalDataSource(
    private val taskDao: TaskDao
) {

    fun getTasks(showDone: Boolean): Flow<List<Task>> {
        return flow {
            emitAll(taskDao.getTasks(showDone).map { tasks ->
                tasks.map { mapFromEntityToTask(it) }
            })
        }
    }

    fun getDoneTasksCount(): Flow<Int> {
        return taskDao.getDoneTasksCount()
    }

    suspend fun getTask(taskId: String): Task {
        return mapFromEntityToTask(taskDao.getTask(taskId))
    }

    suspend fun addTask(task: Task) {
        taskDao.insertTask(mapFromTaskToEntity(task))
    }

    suspend fun updateTask(task: Task) {
        taskDao.updateTask(mapFromTaskToEntity(task))
    }

    suspend fun removeTask(task: Task) {
        taskDao.deleteTask(mapFromTaskToEntity(task))
    }

    suspend fun removeTask(task: TaskEntity) {
        taskDao.deleteTask(task)
    }
}