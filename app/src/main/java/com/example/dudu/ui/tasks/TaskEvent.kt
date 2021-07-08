package com.example.dudu.ui.tasks

import com.example.dudu.data.models.Task

sealed class TaskEvent {
    data class Error(val message: String?) : TaskEvent()
    data class SuccessRemoving(val task: Task) : TaskEvent()
    object SuccessCreating : TaskEvent()
    object SuccessUpdating : TaskEvent()
}