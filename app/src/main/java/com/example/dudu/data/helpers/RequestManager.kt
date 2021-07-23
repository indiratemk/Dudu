package com.example.dudu.data.helpers

import com.example.dudu.data.errors.ErrorType
import com.example.dudu.data.errors.RequestException
import com.example.dudu.di.core.AppScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * Вспомогательный класс для запросов. В нем прописана логика в каких случая требуется
 * синхронизация данных/сохранение проваленного запроса. Так как используется
 * optimistic update => когда запрос провалился необходимо возвращать все назад,
 * эта логика также прописана здесь.
 */

@AppScope
class RequestManager @Inject constructor() {

    suspend fun <ResultType : Any> executeRequest(
        onBeforeRequest: suspend () -> Unit = {},
        makeRequest: suspend () -> ResultType,
        onAfterRequest: suspend () -> Unit = {},
        onRevertOptimisticUpdate: suspend () -> Unit = {},
        onSaveRequest: suspend () -> ResultType,
        onSynchronization: suspend () -> Unit
    ): Resource<ResultType> {
        return try {
            onBeforeRequest()
            val result = makeRequest()
            onAfterRequest()
            Resource.Loaded(result)
        } catch (exception: RequestException) {
            when (exception.type) {
                ErrorType.NOT_FOUND -> {
                    onSynchronization()
                    Resource.Error(exception.matchMessage())
                }
                ErrorType.UNKNOWN, ErrorType.TIMEOUT -> {
                    onRevertOptimisticUpdate()
                    Resource.Error(exception.matchMessage())
                }
                ErrorType.SERVER, ErrorType.CONNECTION -> {
                    onAfterRequest()
                    Resource.Loaded(onSaveRequest())
                }
            }
        }
    }

    suspend fun <ResultType : Any> executeRequest(
        makeRequest: suspend () -> ResultType,
        onSynchronization: suspend (ResultType) -> Unit
    ): Resource<ResultType> {
        return try {
            val result = makeRequest()
            onSynchronization(result)
            Resource.Loaded(result)
        } catch (exception: RequestException) {
            Resource.Error(exception.matchMessage())
        }
    }

    fun <ResultType : Any, RequestType> executeRequest(
        makeRequest: suspend () -> RequestType,
        onAfterRequest: () -> Flow<ResultType>,
        onSynchronization: suspend (RequestType) -> Unit,
        shouldFetchRemote: Boolean
    ): Flow<Resource<ResultType>> {
        return flow {
            if (shouldFetchRemote) {
                emit(Resource.Loading)

                try {
                    val result = makeRequest()
                    onSynchronization(result)
                } catch (exception: RequestException) {
                    emit(Resource.Error(exception.matchMessage()))
                }
            }
            emitAll(onAfterRequest().map { Resource.Loaded(it) })
        }
    }
}