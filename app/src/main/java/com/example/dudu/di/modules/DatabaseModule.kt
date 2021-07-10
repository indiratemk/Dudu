package com.example.dudu.di.modules

import android.app.Application
import androidx.room.Room
import com.example.dudu.data.local.*
import com.example.dudu.data.local.daos.TaskDao
import com.example.dudu.data.local.daos.UnsyncTaskDao
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
    fun provideUnsyncTaskDao(duduDb: DuduDatabase): UnsyncTaskDao {
        return duduDb.unsyncTaskDao()
    }

    @Provides
    @AppScope
    fun provideLocalDataSource(
        taskDao: TaskDao,
        unsyncTaskDao: UnsyncTaskDao
    ): LocalDataSource {
        return LocalDataSource(taskDao, unsyncTaskDao)
    }
}