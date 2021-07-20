package com.example.dudu.ui.task

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dudu.data.TasksRepository
import com.example.dudu.data.helpers.Resource
import com.example.dudu.data.models.Task
import kotlinx.coroutines.launch
import javax.inject.Inject

class CreateTaskViewModel @Inject constructor(
    private val repository: TasksRepository
) : ViewModel() {

    private val _task = MutableLiveData<Resource<Task>>()
    val task: LiveData<Resource<Task>>
        get() = _task

    fun createTask(task: Task) {
        viewModelScope.launch {
            _task.value = Resource.Loading
            _task.value = repository.addTask(task)
        }
    }

    fun updateTask(task: Task) {
        viewModelScope.launch {
            _task.value = Resource.Loading
            _task.value = repository.updateTask(task)
        }
    }

    fun removeTask(task: Task) {
        viewModelScope.launch {
            _task.value = Resource.Loading
            _task.value = repository.removeTask(task)
        }
    }
}