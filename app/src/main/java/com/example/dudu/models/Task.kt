package com.example.dudu.models

import java.util.*

data class Task(
    val id: Int,
    val description: String,
    val deadline: Date?,
    val priority: Int,
    var isDone: Boolean
)