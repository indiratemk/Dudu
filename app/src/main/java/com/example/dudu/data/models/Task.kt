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
) : Parcelable {

    override fun equals(other: Any?): Boolean {
        if (this === other)
            return true
        if (other !is Task)
            return false
        return this.id == other.id &&
                this.isDone == other.isDone &&
                this.description == other.description &&
                this.deadline == other.deadline &&
                this.priority == other.priority
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + description.hashCode()
        result = 31 * result + deadline.hashCode()
        result = 31 * result + priority.hashCode()
        result = 31 * result + isDone.hashCode()
        return result
    }
}