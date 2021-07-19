package com.example.dudu.di

import android.app.Application
import com.example.dudu.DuduApp
import com.example.dudu.di.core.AppModule
import com.example.dudu.di.core.DatabaseModule
import com.example.dudu.di.core.NetworkModule
import com.example.dudu.di.core.RepositoryModule
import com.example.dudu.di.core.AppScope
import com.example.dudu.di.task.CreateTaskComponent
import com.example.dudu.di.tasks.TasksComponent
import dagger.BindsInstance
import dagger.Component

@AppScope
@Component(modules = [
    DatabaseModule::class,
    NetworkModule::class,
    AppModule::class,
    RepositoryModule::class
])
interface AppComponent {

    @Component.Factory
    interface Factory {
        fun create(
            @BindsInstance application: Application
        ): AppComponent
    }

    fun createTaskComponent(): CreateTaskComponent

    fun tasksComponent(): TasksComponent

    fun inject(application: DuduApp)
}