package com.example.dudu.data.helpers

import com.example.dudu.data.errors.RequestException
import com.example.dudu.data.errors.ErrorType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

suspend fun <ResultType : Any> networkManagerFromAction(
    onLocalRequest: suspend () -> Unit,
    onRemoteRequest: suspend () -> ResultType,
    onRevertOptimisticUpdate: suspend () -> Unit,
    onSyncLocalData: suspend () -> Unit = {},
    onSaveRequest: suspend () -> ResultType,
    onUpdateData: suspend () -> Unit
): Resource<ResultType> {
    return try {
        onLocalRequest()
        val result = onRemoteRequest()
        onSyncLocalData()
        Resource.Loaded(result)
    } catch (exception: RequestException) {
        return when (exception.type) {
            ErrorType.NOT_FOUND -> {
                onUpdateData()
                Resource.Error(exception.matchMessage())
            }
            ErrorType.UNKNOWN, ErrorType.TIMEOUT -> {
                onRevertOptimisticUpdate()
                Resource.Error(exception.matchMessage())
            }
            ErrorType.SERVER, ErrorType.CONNECTION -> {
                onSyncLocalData()
                Resource.Loaded(onSaveRequest())
            }

        }
    }
}

suspend fun <ResultType : Any> networkManagerFromAction(
    onRemoteRequest: suspend () -> ResultType,
    onSyncLocalData: suspend (ResultType) -> Unit
): Resource<ResultType> {
    return try {
        val result = onRemoteRequest()
        onSyncLocalData(result)
        Resource.Loaded(result)
    } catch (exception: RequestException) {
        Resource.Error(exception.matchMessage())
    }
}

fun <ResultType : Any, RequestType> networkManagerFromFlow(
    localRequest: () -> Flow<ResultType>,
    remoteRequest: suspend () -> RequestType,
    saveRemoteResult: suspend (RequestType) -> Unit,
    shouldFetchRemote: Boolean
): Flow<Resource<ResultType>> {
    return flow {
        if (shouldFetchRemote) {
            emit(Resource.Loading)

            val resultFlow = try {
                saveRemoteResult(remoteRequest())
                localRequest().map { Resource.Loaded(it) }
            } catch (exception: RequestException) {
                emit(Resource.Error(exception.matchMessage()))
                localRequest().map { Resource.Loaded(it) }
            }
            emitAll(resultFlow)
        } else {
            emitAll(localRequest().map { Resource.Loaded(it) })
        }
    }
}