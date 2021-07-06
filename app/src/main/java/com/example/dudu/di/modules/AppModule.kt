package com.example.dudu.di.modules

import android.app.Application
import com.example.dudu.di.scopes.AppScope
import dagger.Module
import dagger.Provides

@Module
class AppModule(
    private val application: Application
) {

    @Provides
    @AppScope
    fun provideApplication() = application
}