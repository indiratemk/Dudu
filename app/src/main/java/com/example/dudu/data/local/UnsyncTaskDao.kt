package com.example.dudu.data.local

import androidx.room.*
import com.example.dudu.data.local.entities.DeletedTaskEntity
import com.example.dudu.data.local.entities.UpdatedTaskEntity

@Dao
interface UnsyncTaskDao {

    @Query("SELECT * FROM unsync_deleted_tasks")
    suspend fun getDeletedTasks(): List<DeletedTaskEntity>

    @Query("SELECT * FROM unsync_updated_tasks")
    suspend fun getUpdatedTasks(): List<UpdatedTaskEntity>

    @Query("DELETE FROM unsync_updated_tasks WHERE id = :taskId")
    suspend fun deleteUpdatedTask(taskId: String)

    @Query("DELETE FROM unsync_deleted_tasks")
    suspend fun deleteAllDeletedTasks()

    @Query("DELETE FROM unsync_updated_tasks")
    suspend fun deleteAllUpdatedTasks()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDeletedTask(deletedTask: DeletedTaskEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUpdatedTask(updatedTaskEntity: UpdatedTaskEntity)

    @Transaction
    suspend fun deleteUnsyncTask(deletedTask: DeletedTaskEntity) {
        deleteUpdatedTask(deletedTask.id)
        insertDeletedTask(deletedTask)
    }

    @Transaction
    suspend fun clearUnsyncTasks() {
        deleteAllDeletedTasks()
        deleteAllUpdatedTasks()
    }
}