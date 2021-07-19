package com.example.dudu.di.core

import com.example.dudu.data.TasksRepository
import com.example.dudu.data.TasksRepositoryImpl
import dagger.Binds
import dagger.Module

@Module
interface RepositoryModule {

    @Binds
    @AppScope
    fun bindRepository(tasksRepositoryImpl: TasksRepositoryImpl): TasksRepository
}