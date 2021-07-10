package com.example.dudu.data.remote

import com.example.dudu.data.helpers.handleResponse
import com.example.dudu.data.helpers.mapFromDtoToTask
import com.example.dudu.data.helpers.mapFromTaskToDto
import com.example.dudu.data.models.Task
import com.example.dudu.data.remote.dtos.SyncTasksDto
import javax.inject.Inject

class RemoteDataSource @Inject constructor(
    private val tasksApi: TasksApi
) {

    suspend fun getTasks(): List<Task> {
        val tasksDto = handleResponse { tasksApi.getTasks() }
        return tasksDto.map { mapFromDtoToTask(it) }
    }

    suspend fun createTask(task: Task): Task {
        val taskDto = mapFromTaskToDto(task)
        return mapFromDtoToTask(
            handleResponse { tasksApi.createTask(taskDto) }
        )
    }

    suspend fun updateTask(task: Task): Task {
        val taskDto = mapFromTaskToDto(task)
        return mapFromDtoToTask(
            handleResponse { tasksApi.updateTask(task.id, taskDto) }
        )
    }

    suspend fun removeTask(task: Task): Task {
        return mapFromDtoToTask(
            handleResponse { tasksApi.removeTask(task.id) }
        )
    }

    suspend fun synchronizeTasks(syncTasks: SyncTasksDto): List<Task> {
        return handleResponse { tasksApi.synchronizeTasks(syncTasks) }
            .map { mapFromDtoToTask(it) }
    }
}