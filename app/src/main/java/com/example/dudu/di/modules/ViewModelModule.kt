package com.example.dudu.di.modules

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.dudu.di.keys.ViewModelKey
import com.example.dudu.ui.task.CreateTaskViewModel
import com.example.dudu.ui.tasks.TasksViewModel
import com.example.dudu.util.ViewModelFactory
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
interface ViewModelModule {

    @Binds
    fun bindViewModelFactory(factory: ViewModelFactory): ViewModelProvider.Factory

    @Binds
    @IntoMap
    @ViewModelKey(TasksViewModel::class)
    fun bindTasksViewModel(viewModel: TasksViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(CreateTaskViewModel::class)
    fun bindCreateTaskViewModel(viewModel: CreateTaskViewModel): ViewModel
}