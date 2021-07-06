package com.example.dudu.data.remote

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class TaskModel(

    @SerializedName("id")
    @Expose
    val id: String,

    @SerializedName("text")
    @Expose
    val text: String,

    @SerializedName("importance")
    @Expose
    val importance: String,

    @SerializedName("done")
    @Expose
    val isDone: Boolean,

    @SerializedName("deadline")
    @Expose
    val deadline: Long,

    @SerializedName("created_at")
    @Expose
    val createdAt: Long,

    @SerializedName("updated_at")
    @Expose
    val updatedAt: Long
) : Serializable