package com.example.dudu.ui.tasks

import com.example.dudu.models.Task

interface TaskClickListener {

    fun onTaskCheckedClick(position: Int)

    fun onTaskClick(task: Task)
}