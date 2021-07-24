package com.example.dudu.di.core

import androidx.lifecycle.ViewModelProvider
import com.example.dudu.util.ViewModelProviderFactory
import dagger.Binds
import dagger.Module

@Module
interface AppModule {

    @Binds
    fun bindViewModelBuilder(
        viewModelProvider: ViewModelProviderFactory
    ): ViewModelProvider.Factory
}