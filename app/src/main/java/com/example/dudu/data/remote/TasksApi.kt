package com.example.dudu.data.remote

import com.example.dudu.data.remote.dtos.SyncTasksDto
import com.example.dudu.data.remote.dtos.TaskDto
import retrofit2.Response
import retrofit2.http.*

interface TasksApi {

    @GET("tasks/")
    suspend fun getTasks(): Response<List<TaskDto>>

    @POST("tasks/")
    suspend fun createTask(
        @Body task: TaskDto
    ): Response<TaskDto>

    @PUT("tasks/{id}")
    suspend fun updateTask(
        @Path("id") taskId: String,
        @Body task: TaskDto
    ): Response<TaskDto>

    @DELETE("tasks/{id}")
    suspend fun removeTask(
        @Path("id") taskId: String
    ): Response<TaskDto>

    @PUT("tasks/")
    suspend fun synchronizeTasks(
        @Body syncTasks: SyncTasksDto
    ): Response<List<TaskDto>>
}