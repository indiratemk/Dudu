package com.example.dudu.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Task::class], version = 1, exportSchema = false)
abstract class DuduDatabase : RoomDatabase() {

    abstract fun taskDao(): TaskDao

    companion object {
        @Volatile
        private var INSTANCE: DuduDatabase? = null

        fun getDatabase(context: Context): DuduDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    DuduDatabase::class.java,
                    "dudu_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}