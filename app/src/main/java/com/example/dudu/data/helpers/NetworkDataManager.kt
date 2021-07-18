package com.example.dudu.data.helpers

import com.example.dudu.data.remote.errors.BackendException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

suspend fun <ResultType : Any> networkManagerFromAction(
    onLocalRequest: suspend () -> Unit,
    onRemoteRequest: suspend () -> ResultType,
    onRevertDataRequest: suspend () -> Unit,
    onSyncDataIfNeeded: suspend () -> Unit = {},
    onNetworkError: suspend () -> ResultType
): Resource<ResultType> {
    return try {
        onLocalRequest()
        val result = onRemoteRequest()
        onSyncDataIfNeeded()
        Resource.Loaded(result)
    } catch (exception : BackendException) {
        onRevertDataRequest()
        Resource.Error(exception.errorMessage)
    } catch (exception : Exception) {
        onSyncDataIfNeeded()
        Resource.Loaded(onNetworkError())
    }
}

suspend fun <ResultType : Any> networkManagerFromAction(
    onRemoteRequest: suspend () -> ResultType,
    onSyncData: suspend (ResultType) -> Unit
): Resource<ResultType> {
    return try {
        val result = onRemoteRequest()
        onSyncData(result)
        Resource.Loaded(result)
    } catch (exception: BackendException) {
        Resource.Error(exception.errorMessage)
    } catch (exception: Exception) {
        Resource.Error(exception.message)
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
            } catch (exception: Exception) {
                emit(
                    if (exception is BackendException)
                        Resource.Error(exception.errorMessage)
                    else
                        Resource.Error(exception.message)
                )
                localRequest().map { Resource.Loaded(it) }
            }
            emitAll(resultFlow)
        } else {
            emitAll(localRequest().map { Resource.Loaded(it) })
        }
    }
}