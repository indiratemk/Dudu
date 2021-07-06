package com.example.dudu.data.remote

class RemoteDataSource(
    private val tasksApi: TasksApi
) {

    suspend fun getTasks(): Resource<List<TaskModel>> {
        return handleResponse({ tasksApi.getTasks() }, null, Query.GET)
    }

    suspend fun createTask(task: TaskModel): Resource<TaskModel> {
        return handleResponse({ tasksApi.createTask(task) }, task, Query.POST)
    }

    suspend fun updateTask(taskId: String, task: TaskModel): Resource<TaskModel> {
        return handleResponse({ tasksApi.updateTask(taskId, task) }, task, Query.PUT)
    }

    suspend fun removeTask(task: TaskModel): Resource<TaskModel> {
        return handleResponse({ tasksApi.removeTask(task.id) }, task, Query.DELETE)
    }
}