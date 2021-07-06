package com.example.dudu.ui.task

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dudu.data.TasksRepository
import com.example.dudu.data.local.TaskEntity
import kotlinx.coroutines.launch
import javax.inject.Inject

class CreateTaskViewModel @Inject constructor(
    private val repository: TasksRepository
) : ViewModel() {

    fun createTask(task: TaskEntity) {
        viewModelScope.launch {
            repository.addTask(task)
        }
    }

    fun updateTask(task: TaskEntity) {
        viewModelScope.launch {
            repository.updateTask(task)
        }
    }
}