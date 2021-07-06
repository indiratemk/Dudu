package com.example.dudu.di.modules

import android.app.Application
import androidx.room.Room
import com.example.dudu.data.local.DuduDatabase
import com.example.dudu.data.local.LocalDataSource
import com.example.dudu.data.local.TaskDao
import com.example.dudu.di.scopes.AppScope
import dagger.Module
import dagger.Provides

@Module(includes = [AppModule::class])
object DatabaseModule {

    private const val DB_NAME = "dudu_database"

    @Provides
    @AppScope
    fun provideDuduDatabase(application: Application): DuduDatabase {
        return Room.databaseBuilder(application, DuduDatabase::class.java, DB_NAME).build()
    }

    @Provides
    @AppScope
    fun provideTaskDao(duduDb: DuduDatabase): TaskDao {
        return duduDb.taskDao()
    }

    @Provides
    @AppScope
    fun provideLocalDataSource(taskDao: TaskDao): LocalDataSource {
        return LocalDataSource(taskDao)
    }
}