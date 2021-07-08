package com.example.dudu.ui.task

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dudu.data.models.Task
import com.example.dudu.data.TasksRepository
import kotlinx.coroutines.launch
import javax.inject.Inject

class CreateTaskViewModel @Inject constructor(
    private val repository: TasksRepository
) : ViewModel() {

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean>
        get() = _isLoading


    fun createTask(task: Task) {
        viewModelScope.launch {
            _isLoading.value = true
            repository.addTask(task)
            _isLoading.value = false
        }
    }

    fun updateTask(task: Task) {
        viewModelScope.launch {
            _isLoading.value = true
            repository.updateTask(task)
            _isLoading.value = false
        }
    }
}