package com.example.dudu.ui.tasks

import com.example.dudu.data.local.TaskEntity

sealed class TaskEvent {
    data class ShouldUndoRemove(val task: TaskEntity) : TaskEvent()
}