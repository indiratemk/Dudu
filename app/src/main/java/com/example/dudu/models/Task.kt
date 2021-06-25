package com.example.dudu.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.*

@Parcelize
data class Task(
    val id: String,
    val description: String,
    val deadline: Date?,
    val priority: Int,
    var isDone: Boolean
) : Parcelable