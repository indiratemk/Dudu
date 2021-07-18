package com.example.dudu.data.helpers

import com.example.dudu.data.local.entities.TaskEntity
import com.example.dudu.data.models.Task
import com.example.dudu.data.remote.dtos.TaskDto
import com.example.dudu.util.DateFormatter

fun mapFromEntityToTask(data: TaskEntity): Task {
    return Task(
        id = data.id,
        description = data.description,
        deadline = data.deadline,
        priority = data.priority,
        isDone = data.isDone
    )
}

fun mapFromDtoToTask(data: TaskDto): Task {
    return Task(
        id = data.id,
        description = data.text,
        deadline = data.deadline,
        priority = data.importance,
        isDone = data.isDone
    )
}

fun mapFromTaskToEntity(data: Task): TaskEntity {
    return TaskEntity(
        id = data.id,
        description = data.description,
        deadline = data.deadline,
        priority = data.priority,
        isDone = data.isDone
    )
}

fun mapFromDtoToEntity(data: TaskDto): TaskEntity {
    return TaskEntity(
        id = data.id,
        description = data.text,
        deadline = data.deadline,
        priority = data.importance,
        isDone = data.isDone
    )
}

fun mapFromTaskToDto(data: Task): TaskDto {
    return TaskDto(
        id = data.id,
        text = data.description,
        deadline = data.deadline,
        importance = data.priority,
        isDone = data.isDone,
        createdAt = DateFormatter.getCurrentDateInSeconds(),
        updatedAt = DateFormatter.getCurrentDateInSeconds()
    )
}