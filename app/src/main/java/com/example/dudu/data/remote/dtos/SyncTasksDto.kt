package com.example.dudu.data.remote.dtos

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class SyncTasksDto(

    @SerializedName("deleted")
    @Expose
    val deletedTasksIds: List<String>,

    @SerializedName("other")
    @Expose
    val updatedTasks: List<TaskDto>
)