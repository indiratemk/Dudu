package com.example.dudu.di.core

import android.app.Application
import androidx.room.Room
import com.example.dudu.data.local.DuduDatabase
import com.example.dudu.data.local.daos.TaskDao
import com.example.dudu.data.local.daos.UnsyncTaskDao
import dagger.Module
import dagger.Provides

@Module
object DatabaseModule {

    private const val DB_NAME = "dudu_database"

    @Provides
    @AppScope
    fun provideDuduDatabase(application: Application): DuduDatabase {
        return Room.databaseBuilder(application, DuduDatabase::class.java, DB_NAME)
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    @AppScope
    fun provideTaskDao(duduDb: DuduDatabase): TaskDao {
        return duduDb.taskDao()
    }

    @Provides
    @AppScope
    fun provideUnsyncTaskDao(duduDb: DuduDatabase): UnsyncTaskDao {
        return duduDb.unsyncTaskDao()
    }
}