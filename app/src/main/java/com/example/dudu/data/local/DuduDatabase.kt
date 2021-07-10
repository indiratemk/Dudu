package com.example.dudu.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.dudu.data.local.entities.DeletedTaskEntity
import com.example.dudu.data.local.entities.TaskEntity
import com.example.dudu.data.local.entities.UpdatedTaskEntity

@Database(
    entities = [TaskEntity::class, UpdatedTaskEntity::class, DeletedTaskEntity::class],
    version = 1,
    exportSchema = false
)
abstract class DuduDatabase : RoomDatabase() {

    abstract fun taskDao(): TaskDao

    abstract fun unsyncTaskDao(): UnsyncTaskDao
}