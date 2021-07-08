package com.example.dudu.data

import com.example.dudu.data.helpers.Resource
import com.example.dudu.data.local.LocalDataSource
import com.example.dudu.data.models.Task
import com.example.dudu.data.remote.RemoteDataSource
import com.example.dudu.data.remote.errors.BackendException
import com.example.dudu.data.remote.errors.NetworkException
import kotlinx.coroutines.flow.*

class TasksRepositoryImpl(
    private val localSource: LocalDataSource,
    private val remoteSource: RemoteDataSource
) : TasksRepository {

    override fun getTasks(showDone: Boolean, fetchRemote: Boolean): Flow<Resource<List<Task>>> {
        return flow {
            val query = localSource.getTasks(showDone)

            if (fetchRemote) {
                val data = query.first()
                emit(Resource.Loading(data))

                val flow = try {
                    val tasks = remoteSource.getTasks()
                    localSource.refreshTasks(tasks)
                    query.map { Resource.Loaded(it) }
                } catch (exception: BackendException) {
                    query.map { Resource.Error(exception) }
                } catch (exception: NetworkException) {
                    emit(Resource.Error(exception))
                    query.map { Resource.Loaded(it) }
                }
                emitAll(flow)
            } else {
                emitAll(query.map { Resource.Loaded(it) })
            }
        }
    }

    override fun getDoneTasksCount(): Flow<Int> {
        return localSource.getDoneTasksCount()
    }

    override suspend fun addTask(task: Task): Resource<Task> {
        return try {
            localSource.addTask(task)
            val createdTask = remoteSource.createTask(task)
            Resource.Loaded(createdTask)
        } catch (exception: BackendException) {
            localSource.removeTask(task)
            Resource.Error(exception)
        } catch (exception: NetworkException) {
            // TODO: 7/7/21 сохранить запрос
            localSource.removeTask(task)
            Resource.Error(exception)
//            Resource.Success(task)
        }
    }

    override suspend fun updateTask(task: Task): Resource<Task> {
        val prevTask = localSource.getTask(task.id)
        return try {
            localSource.updateTask(task)
            val updatedTask = remoteSource.updateTask(task)
            Resource.Loaded(updatedTask)
        } catch (exception: BackendException) {
            localSource.updateTask(prevTask)
            Resource.Error(exception)
        } catch (exception: NetworkException) {
            // TODO: 7/7/21 сохранить запрос
            localSource.updateTask(prevTask)
            Resource.Error(exception)
//            Resource.Success(task)
        }
    }

    override suspend fun removeTask(task: Task): Resource<Task> {
        return try {
            localSource.removeTask(task)
            val deletedTask = remoteSource.removeTask(task)
            Resource.Loaded(deletedTask)
        } catch (exception: BackendException) {
            localSource.addTask(task)
            Resource.Error(exception)
        } catch (exception: NetworkException) {
            // TODO: 7/7/21 сохранить запрос
            localSource.addTask(task)
            Resource.Error(exception)
//            Resource.Success(task)
        }
    }
}