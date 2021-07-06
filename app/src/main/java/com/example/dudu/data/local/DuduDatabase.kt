package com.example.dudu.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [TaskEntity::class], version = 1, exportSchema = false)
abstract class DuduDatabase : RoomDatabase() {

    abstract fun taskDao(): TaskDao
}