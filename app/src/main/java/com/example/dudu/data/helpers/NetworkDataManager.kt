package com.example.dudu.data.helpers

import com.example.dudu.data.remote.errors.BackendException
import com.example.dudu.data.remote.errors.NetworkException
import kotlinx.coroutines.flow.*

suspend fun <ResultType : Any> networkManagerFromAction(
    localRequest: suspend () -> Unit,
    remoteRequest: suspend () -> ResultType,
    revertDataRequest: suspend () -> Unit
): Resource<ResultType> {
    return try {
        localRequest()
        Resource.Loaded(remoteRequest())
    } catch (exception : BackendException) {
        revertDataRequest()
        Resource.Error(exception)
    } catch (exception : NetworkException) {
        // TODO: 7/7/21 сохранить запрос
        revertDataRequest()
        Resource.Error(exception)
//                Resource.Success(task)
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
            val data = localRequest().first()
            emit(Resource.Loading(data))

            val resultFlow = try {
                saveRemoteResult(remoteRequest())
                localRequest().map { Resource.Loaded(it) }
            } catch (exception: Exception) {
                emit(Resource.Error(exception))
                localRequest().map { Resource.Loaded(it) }
            }
            emitAll(resultFlow)
        } else {
            emitAll(localRequest().map { Resource.Loaded(it) })
        }
    }
}