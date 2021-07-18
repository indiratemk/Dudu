package com.example.dudu.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "unsync_updated_tasks")
data class UpdatedTaskEntity(
    @PrimaryKey
    val id: String
)