package com.example.dudu.models

import java.util.*

data class Task(
    val id: Int,
    val description: Int,
    val deadline: Date,
    val priority: Priority,
    val isDone: Boolean
)