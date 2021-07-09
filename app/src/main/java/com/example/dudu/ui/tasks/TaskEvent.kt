package com.example.dudu.ui.tasks

import com.example.dudu.data.models.Task

sealed class TaskEvent {
    object SuccessUpdating : TaskEvent()
    object SuccessRemoving : TaskEvent()
    data class FailRemoving(val message: String?, val task: Task) : TaskEvent()
    data class FailUpdating(val message: String?) : TaskEvent()
}