package com.example.dudu.data.remote

import com.example.dudu.data.remote.errors.BackendException
import com.example.dudu.data.remote.errors.NetworkException
import retrofit2.Response

suspend fun <T: Any> handleResponse(
    action: suspend () -> Response<T>
): T {
    try {
        val response = action()
        if (response.isSuccessful) {
            return response.body()!!
        }
        throw BackendException(response.code(), response.message())
    } catch (e: Exception) {
        e.printStackTrace()
        throw NetworkException()
    }
}