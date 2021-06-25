package com.example.dudu.ui.tasks

import com.example.dudu.models.Task

interface TaskClickListener {

    fun onTaskCheckedClick(isChecked: Boolean)

    fun onTaskClick(task: Task)
}