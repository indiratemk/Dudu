package com.example.dudu.di.tasks

import androidx.lifecycle.ViewModel
import com.example.dudu.di.keys.ViewModelKey
import com.example.dudu.ui.tasks.TasksViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
interface TasksModule {

    @Binds
    @IntoMap
    @ViewModelKey(TasksViewModel::class)
    @TasksActivityScope
    fun bindTasksViewModel(viewModel: TasksViewModel): ViewModel
}