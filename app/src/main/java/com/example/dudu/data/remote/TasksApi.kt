package com.example.dudu.data.remote

import retrofit2.Response
import retrofit2.http.*

interface TasksApi {

    @GET("tasks/")
    suspend fun getTasks(): Response<List<TaskModel>>

    @POST("tasks/")
    suspend fun createTask(
        @Body task: TaskModel
    ): Response<TaskModel>

    @PUT("tasks/{id}")
    suspend fun updateTask(
        @Path("id") taskId: String,
        @Body task: TaskModel
    ): Response<TaskModel>

    @DELETE("tasks/{id}")
    suspend fun removeTask(
        @Path("id") taskId: String
    ): Response<TaskModel>
}