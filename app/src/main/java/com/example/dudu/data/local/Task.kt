package com.example.dudu.data.local

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Entity(tableName = "tasks")
@Parcelize
data class Task(
    @PrimaryKey
    val id: String,
    val description: String,
    val deadline: Long,
    val priority: Int,
    val isDone: Boolean
) : Parcelable