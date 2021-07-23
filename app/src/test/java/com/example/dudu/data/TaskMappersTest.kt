package com.example.dudu.data

import com.example.dudu.data.helpers.mapFromDtoToTask
import com.example.dudu.data.remote.dtos.TaskDto
import org.hamcrest.CoreMatchers.`is`
import org.junit.Assert.assertThat
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

        assertThat(task.id, `is`(taskDto.id))
        assertThat(task.description, `is`(taskDto.text))
        assertThat(task.deadline, `is`(taskDto.deadline))
        assertThat(task.priority, `is`(taskDto.importance))
        assertThat(task.isDone, `is`(taskDto.isDone))
    }
}