package com.example.dudu.data

import com.example.dudu.data.helpers.mapFromDtoToTask
import com.example.dudu.data.remote.dtos.TaskDto
import org.junit.Assert.assertEquals
import org.junit.Test

class TaskMappersTest {

    @Test
    fun `mapFromDtoToTask should return task with task dto input`() {
        val taskDto = TaskDto(
            id = "test",
            text = "test",
            importance = "basic",
            isDone = false,
            deadline = 0L,
            createdAt = 0L,
            updatedAt = 0L)

        val task = mapFromDtoToTask(taskDto)

        assertEquals(taskDto.id, task.id)
        assertEquals(taskDto.text, task.description)
        assertEquals(taskDto.deadline, task.deadline)
        assertEquals(taskDto.importance, task.priority)
        assertEquals(taskDto.isDone, task.isDone)
    }
}