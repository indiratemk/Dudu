package com.example.dudu.data.helpers

import com.example.dudu.data.errors.RequestException
import com.example.dudu.data.errors.ErrorType
import com.example.dudu.di.core.AppScope
import retrofit2.Response
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import javax.inject.Inject

@AppScope
class ResponseHandler @Inject constructor() {

    suspend fun <T : Any> handleResponse(
        action: suspend () -> Response<T>
    ): T {
        try {
            val response = action()
            if (response.isSuccessful) {
                return response.body()!!
            }
            throw when (response.code()) {
                404 -> RequestException(ErrorType.NOT_FOUND)
                500 -> RequestException(ErrorType.SERVER)
                else -> RequestException(
                    ErrorType.UNKNOWN
                )
            }
        } catch (exception: SocketTimeoutException) {
            throw RequestException(ErrorType.TIMEOUT)
        } catch (exception: UnknownHostException) {
            throw RequestException(ErrorType.CONNECTION)
        }
    }
}