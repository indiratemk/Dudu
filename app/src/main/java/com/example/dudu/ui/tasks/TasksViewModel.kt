package com.example.dudu.ui.tasks

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.dudu.data.TasksRepository
import com.example.dudu.data.local.TaskEntity
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
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

    val tasks: LiveData<List<TaskEntity>> = _showDone.flatMapLatest {
        repository.getTasks(it)
    }.asLiveData()

    val doneTasksCount: LiveData<Int> = repository.getDoneTasksCount().asLiveData()

    fun setShowDoneTasks(showDone: Boolean) {
        _showDone.value = showDone
    }

    fun onTaskCheckedChanged(task: TaskEntity, isChecked: Boolean) {
        viewModelScope.launch {
            repository.updateTask(task.copy(isDone = isChecked))
        }
    }

    fun onTaskRemoved(task: TaskEntity) {
        viewModelScope.launch {
            repository.removeTask(task)
            taskEventChannel.send(TaskEvent.ShouldUndoRemove(task))
        }
    }

    fun onUndoTaskRemove(task: TaskEntity) {
        viewModelScope.launch {
            repository.addTask(task)
        }
    }
}