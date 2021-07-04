package com.example.dudu.ui.tasks

import com.example.dudu.data.local.Task

sealed class TaskEvent {
    data class ShouldUndoRemove(val task: Task) : TaskEvent()
}