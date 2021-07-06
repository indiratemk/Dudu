package com.example.dudu.data.remote

import com.example.dudu.data.remote.errors.BackendError
import com.example.dudu.data.remote.errors.ServerError
import retrofit2.Response

suspend fun <T: Any> handleResponse(
    action: suspend () -> Response<T>,
    inputData: T?,
    queryType: Query
): Resource<T> {
    try {
        val response = action()
        if (response.isSuccessful) {
            return Resource.Success(response.body()!!)
        }
        return Resource.Error(BackendError(response.code(), response.message()), inputData, queryType)
    } catch (e: Exception) {
        e.printStackTrace()
        return Resource.Error(ServerError(), inputData, queryType)
    }
}