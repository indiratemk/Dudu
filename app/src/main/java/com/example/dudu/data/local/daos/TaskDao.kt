package com.example.dudu.data.local.daos

import androidx.room.*
import com.example.dudu.data.local.entities.TaskEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {

    @Query("SELECT * FROM tasks WHERE (isDone == :showDone OR isDone = 0)")
    fun getTasks(showDone: Boolean): Flow<List<TaskEntity>>

    @Query("SELECT COUNT(*) FROM tasks WHERE isDone = 1")
    fun getDoneTasksCount(): Flow<Int>

    @Query("SELECT * FROM tasks WHERE id = :taskId")
    suspend fun getTask(taskId: String): TaskEntity

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: TaskEntity)

    @Update
    suspend fun updateTask(task: TaskEntity)

    @Delete
    suspend fun deleteTask(task: TaskEntity)

    @Query("DELETE FROM tasks")
    suspend fun deleteAllTasks()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTasks(tasks: List<TaskEntity>)

    @Transaction
    suspend fun refreshTasks(tasks: List<TaskEntity>) {
        deleteAllTasks()
        insertTasks(tasks)
    }
}