package com.example.dudu.ui.tasks

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.dudu.data.TasksRepository
import com.example.dudu.data.helpers.Resource
import com.example.dudu.data.models.Task
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

class TasksViewModel @Inject constructor(
    private val repository: TasksRepository
) : ViewModel() {

    private val _updateTask = MutableLiveData<Resource<Task>>()
    val updateTask: LiveData<Resource<Task>>
        get() = _updateTask

    private val _removeTask = MutableLiveData<Resource<Task>>()
    val removeTask: LiveData<Resource<Task>>
        get() = _removeTask

    private val _syncTasks = MutableLiveData<Resource<List<Task>>>()
    val syncTasks: LiveData<Resource<List<Task>>>
        get() = _syncTasks

    private val _showDone = MutableStateFlow(false)
    val showDone: LiveData<Boolean>
        get() = _showDone.asLiveData()

    private val _shouldFetchRemote = MutableStateFlow(true)

    val tasksResource: LiveData<Resource<List<Task>>> = combine(
        _showDone, _shouldFetchRemote
    ) { showDone, shouldRetchRemote -> Pair(showDone, shouldRetchRemote) }.flatMapLatest {
        repository.getTasks(it.first, it.second)
    }.asLiveData()

    val doneTasksCount: LiveData<Int> = repository.getDoneTasksCount().asLiveData()

    fun setShowDoneTasks(showDone: Boolean) {
        _showDone.value = showDone
    }

    fun setShouldFetchRemote(shouldFetchRemote: Boolean) {
        _shouldFetchRemote.value = shouldFetchRemote
    }

    fun onTaskCheckedChanged(task: Task, isChecked: Boolean) {
        viewModelScope.launch {
            _updateTask.value = repository.updateTask(task.copy(isDone = isChecked))
        }
    }

    fun removeTask(task: Task) {
        viewModelScope.launch {
            _removeTask.value = repository.removeTask(task)
        }
    }

    fun synchronizeTasks() {
        viewModelScope.launch {
            if (repository.shouldSynchronizeTasks()) {
                _syncTasks.value = Resource.Loading
                _syncTasks.value = repository.synchronizeTasks()
            }
        }
    }
}