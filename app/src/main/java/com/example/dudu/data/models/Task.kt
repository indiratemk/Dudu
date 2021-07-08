package com.example.dudu.data.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Task(
    val id: String,
    val description: String,
    val deadline: Long,
    val priority: String,
    val isDone: Boolean
) : Parcelable