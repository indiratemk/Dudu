package com.example.dudu.ui.tasks

import androidx.lifecycle.*
import com.example.dudu.data.TasksRepository
import com.example.dudu.data.local.Task
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class TasksViewModel(
    private val repository: TasksRepository
) : ViewModel() {

    private val taskEventChannel = Channel<TaskEvent>()
    val taskEvent = taskEventChannel.receiveAsFlow()

    private val _showDone = MutableStateFlow(false)
    val showDone: LiveData<Boolean>
        get() = _showDone.asLiveData()

    val tasks: LiveData<List<Task>> = _showDone.flatMapLatest {
        repository.getTasks(it)
    }.asLiveData()

    val doneTasksCount: LiveData<Int> = repository.getDoneTasksCount().asLiveData()

    fun setShowDoneTasks(showDone: Boolean) {
        _showDone.value = showDone
    }

    fun onTaskCheckedChanged(task: Task, isChecked: Boolean) {
        viewModelScope.launch {
            repository.updateTask(task.copy(isDone = isChecked))
        }
    }

    fun onTaskRemoved(task: Task) {
        viewModelScope.launch {
            repository.removeTask(task)
            taskEventChannel.send(TaskEvent.ShouldUndoRemove(task))
        }
    }

    fun onUndoTaskRemove(task: Task) {
        viewModelScope.launch {
            repository.addTask(task)
        }
    }
}

class TasksViewModelFactory(
    private val repository: TasksRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TasksViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TasksViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}