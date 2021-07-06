package com.example.dudu.di.modules

import com.example.dudu.data.TasksRepository
import com.example.dudu.data.TasksRepositoryImpl
import com.example.dudu.data.local.LocalDataSource
import com.example.dudu.data.remote.RemoteDataSource
import com.example.dudu.di.scopes.AppScope
import dagger.Module
import dagger.Provides

@Module(includes = [NetworkModule::class, DatabaseModule::class])
object RepositoryModule {

    @Provides
    @AppScope
    fun provideRepository(
        remoteSource: RemoteDataSource,
        localSource: LocalDataSource
    ): TasksRepository {
        return TasksRepositoryImpl(localSource, remoteSource)
    }
}