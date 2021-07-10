package com.example.dudu.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "unsync_deleted_tasks")
data class DeletedTaskEntity(
    @PrimaryKey
    val id: String
)