package com.example.dudu.ui.tasks

import com.example.dudu.data.local.TaskEntity

interface TaskClickListener {

    fun onTaskClick(task: TaskEntity)

    fun onCheckBoxClick(task: TaskEntity, isChecked: Boolean)
}