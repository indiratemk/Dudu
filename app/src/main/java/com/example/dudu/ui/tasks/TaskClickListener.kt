package com.example.dudu.ui.tasks

import com.example.dudu.data.models.Task

interface TaskClickListener {

    fun onTaskClick(task: Task)

    fun onCheckBoxClick(task: Task, isChecked: Boolean)
}