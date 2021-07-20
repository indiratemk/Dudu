package com.example.dudu.di.task

import androidx.lifecycle.ViewModel
import com.example.dudu.di.keys.ViewModelKey
import com.example.dudu.ui.task.CreateTaskViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
interface CreateTaskModule {

    @Binds
    @IntoMap
    @ViewModelKey(CreateTaskViewModel::class)
    @CreateTaskActivityScope
    fun bindCreateTaskViewModel(viewModel: CreateTaskViewModel): ViewModel
}