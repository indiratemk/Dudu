package com.example.dudu.data.remote

import com.example.dudu.data.helpers.ResponseHandler
import com.example.dudu.data.helpers.mapFromDtoToTask
import com.example.dudu.data.helpers.mapFromTaskToDto
import com.example.dudu.data.models.Task
import com.example.dudu.data.remote.dtos.SyncTasksDto
import com.example.dudu.di.core.AppScope
import javax.inject.Inject

@AppScope
class RemoteDataSource @Inject constructor(
    private val tasksApi: TasksApi,
    private val responseHandler: ResponseHandler
) {

    suspend fun getTasks(): List<Task> {
        val tasksDto = responseHandler.handleResponse { tasksApi.getTasks() }
        return tasksDto.map { mapFromDtoToTask(it) }
    }

    suspend fun createTask(task: Task): Task {
        val taskDto = mapFromTaskToDto(task)
        return mapFromDtoToTask(
            responseHandler.handleResponse { tasksApi.createTask(taskDto) }
        )
    }

    suspend fun updateTask(task: Task): Task {
        val taskDto = mapFromTaskToDto(task)
        return mapFromDtoToTask(
            responseHandler.handleResponse { tasksApi.updateTask(task.id, taskDto) }
        )
    }

    suspend fun removeTask(task: Task): Task {
        return mapFromDtoToTask(
            responseHandler.handleResponse { tasksApi.removeTask(task.id) }
        )
    }

    suspend fun synchronizeTasks(syncTasks: SyncTasksDto): List<Task> {
        return responseHandler.handleResponse { tasksApi.synchronizeTasks(syncTasks) }
            .map { mapFromDtoToTask(it) }
    }
}