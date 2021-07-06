package com.example.dudu.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {

    @Query("SELECT * FROM tasks WHERE (isDone == :showDone OR isDone = 0)")
    fun getTasks(showDone: Boolean): Flow<List<TaskEntity>>

    @Query("SELECT COUNT(*) FROM tasks WHERE isDone = 1")
    fun getDoneTasksCount(): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: TaskEntity)

    @Update
    suspend fun updateTask(task: TaskEntity)

    @Delete
    suspend fun deleteTask(task: TaskEntity)
}