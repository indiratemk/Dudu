package com.example.dudu.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tasks")
data class TaskEntity(
    @PrimaryKey
    val id: String,
    val description: String,
    val deadline: Long,
    val priority: String,
    val isDone: Boolean
)