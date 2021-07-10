package com.example.dudu.di.modules

import com.example.dudu.data.TasksRepository
import com.example.dudu.data.TasksRepositoryImpl
import com.example.dudu.di.scopes.AppScope
import dagger.Binds
import dagger.Module

@Module
interface RepositoryModule {

    @Binds
    @AppScope
    fun bindRepository(tasksRepositoryImpl: TasksRepositoryImpl): TasksRepository
}