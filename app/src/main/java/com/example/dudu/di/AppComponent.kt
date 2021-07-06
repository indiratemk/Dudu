package com.example.dudu.di

import com.example.dudu.di.modules.*
import com.example.dudu.di.scopes.AppScope
import com.example.dudu.ui.MainActivity
import com.example.dudu.ui.task.CreateTaskActivity
import dagger.Component

@AppScope
@Component(modules = [
    AppModule::class,
    DatabaseModule::class,
    NetworkModule::class,
    ViewModelModule::class,
    RepositoryModule::class
])
interface AppComponent {

    fun inject(activity: MainActivity)

    fun inject(activity: CreateTaskActivity)
}