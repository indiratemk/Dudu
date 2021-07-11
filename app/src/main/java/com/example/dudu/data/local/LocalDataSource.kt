package com.example.dudu.data.local

import com.example.dudu.data.helpers.mapFromEntityToTask
import com.example.dudu.data.helpers.mapFromTaskToEntity
import com.example.dudu.data.local.daos.TaskDao
import com.example.dudu.data.local.daos.UnsyncTaskDao
import com.example.dudu.data.local.entities.DeletedTaskEntity
import com.example.dudu.data.local.entities.UpdatedTaskEntity
import com.example.dudu.data.models.Task
import com.example.dudu.di.scopes.AppScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@AppScope
class LocalDataSource @Inject constructor(
    private val taskDao: TaskDao,
    private val unsyncTaskDao: UnsyncTaskDao
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

    suspend fun refreshTasks(tasks: List<Task>) {
        taskDao.refreshTasks(tasks.map { mapFromTaskToEntity(it) })
    }

    suspend fun removeUnsyncTask(taskId: String) {
        val deletedTask = DeletedTaskEntity(taskId)
        unsyncTaskDao.deleteUnsyncTask(deletedTask)
    }

    suspend fun updateUnsyncTask(taskId: String) {
        val updatedTask = UpdatedTaskEntity(taskId)
        unsyncTaskDao.insertUpdatedTask(updatedTask)
    }

    suspend fun getUnsyncDeletedTasksIds(): List<String> {
        return unsyncTaskDao.getDeletedTasks()
            .map { it.id }
    }

    suspend fun getUnsyncUpdatedTasks(): List<Task> {
        return unsyncTaskDao.getUpdatedTasks()
            .map { it.id }
            .map { id -> mapFromEntityToTask(taskDao.getTask(id)) }
    }

    suspend fun shouldSynchronizeTasks(): Boolean {
        return getUnsyncUpdatedTasks().isNotEmpty() || getUnsyncDeletedTasksIds().isNotEmpty()
    }

    suspend fun removeUnsyncTasks() {
        unsyncTaskDao.clearUnsyncTasks()
    }
}