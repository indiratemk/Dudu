package com.example.dudu.ui.task

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.dudu.data.TasksRepository
import com.example.dudu.data.local.Task
import kotlinx.coroutines.launch

class CreateTaskViewModel(
    private val repository: TasksRepository
) : ViewModel() {

    fun addTask(task: Task) {
        viewModelScope.launch {
            repository.addTask(task)
        }
    }

    fun updateTask(task: Task) {
        viewModelScope.launch {
            repository.updateTask(task)
        }
    }
}

class CreateTaskViewModelFactory(
    private val repository: TasksRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CreateTaskViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CreateTaskViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}