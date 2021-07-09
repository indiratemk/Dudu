package com.example.dudu.ui.tasks

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.dudu.data.TasksRepository
import com.example.dudu.data.helpers.Resource
import com.example.dudu.data.models.Task
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class TasksViewModel @Inject constructor(
    private val repository: TasksRepository
) : ViewModel() {

    private val taskEventChannel = Channel<TaskEvent>()
    val taskEvent = taskEventChannel.receiveAsFlow()

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
        _shouldFetchRemote.value = false
    }

    fun onTaskCheckedChanged(task: Task, isChecked: Boolean) {
        viewModelScope.launch {
            when (val resource = repository.updateTask(task.copy(isDone = isChecked))) {
                is Resource.Loaded -> taskEventChannel.send(TaskEvent.SuccessUpdating)
                is Resource.Error ->
                    taskEventChannel.send(TaskEvent.Error(resource.message))
            }
        }
    }

    fun onTaskRemoved(task: Task) {
        viewModelScope.launch {
            when (val resource = repository.removeTask(task)) {
                is Resource.Loaded -> taskEventChannel.send(TaskEvent.SuccessRemoving(task))
                is Resource.Error ->
                    taskEventChannel.send(TaskEvent.Error(resource.message))
            }
        }
    }

    fun onUndoTaskRemove(task: Task) {
        viewModelScope.launch {
            when (val resource = repository.addTask(task)) {
                is Resource.Loaded -> taskEventChannel.send(TaskEvent.SuccessCreating)
                is Resource.Error ->
                    taskEventChannel.send(TaskEvent.Error(resource.message))
            }
        }
    }
}