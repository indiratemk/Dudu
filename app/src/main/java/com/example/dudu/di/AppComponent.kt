package com.example.dudu.di

import android.app.Application
import com.example.dudu.DuduApp
import com.example.dudu.di.modules.DatabaseModule
import com.example.dudu.di.modules.NetworkModule
import com.example.dudu.di.modules.RepositoryModule
import com.example.dudu.di.modules.ViewModelModule
import com.example.dudu.di.scopes.AppScope
import com.example.dudu.ui.MainActivity
import com.example.dudu.ui.task.CreateTaskActivity
import dagger.BindsInstance
import dagger.Component

@AppScope
@Component(modules = [
    DatabaseModule::class,
    NetworkModule::class,
    ViewModelModule::class,
    RepositoryModule::class
])
interface AppComponent {

    @Component.Factory
    interface Factory {
        fun create(
            @BindsInstance application: Application
        ): AppComponent
    }

    fun inject(application: DuduApp)

    fun inject(activity: MainActivity)

    fun inject(activity: CreateTaskActivity)
}