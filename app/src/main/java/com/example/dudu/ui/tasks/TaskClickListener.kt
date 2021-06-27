package com.example.dudu.ui.tasks

import com.example.dudu.models.Task

interface TaskClickListener {

    fun onTaskCheckedClick(task: Task)

    fun onTaskClick(task: Task)
}