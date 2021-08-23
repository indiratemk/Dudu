package com.example.dudu.di.core

import android.app.Application
import com.example.dudu.BuildConfig
import com.example.dudu.DuduApp
import com.example.dudu.data.remote.TasksApi
import com.example.dudu.data.remote.interceptors.RequestInterceptor
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoSet
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

@Module
object NetworkModule {

    @Provides
    @AppScope
    @IntoSet
    fun provideRequestInterceptor(): Interceptor {
        return RequestInterceptor()
    }

    @Provides
    @AppScope
    @IntoSet
    fun httpLoggingInterceptor(): Interceptor {
        return if (BuildConfig.DEBUG)
            HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
        else
            HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.NONE)
    }

    @Provides
    @AppScope
    fun provideOkHttpClient(
        interceptors: Set<@JvmSuppressWildcards Interceptor>
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .addInterceptors(interceptors)
            .build()
    }

    @Provides
    @AppScope
    fun provideGsonConverterFactory(): GsonConverterFactory {
        return GsonConverterFactory.create()
    }

    // TODO: 7/24/21 Изменить получение base url для запросов
    @Provides
    @AppScope
    fun provideRetrofit(
        gsonConverterFactory: GsonConverterFactory,
        okHttpClient: OkHttpClient,
        application: Application
    ): Retrofit {
        return Retrofit.Builder()
            .client(okHttpClient)
            .baseUrl((application as DuduApp).getBaseUrl())
            .addConverterFactory(gsonConverterFactory)
            .build()
    }

    @Provides
    @AppScope
    fun provideTasksApi(retrofit: Retrofit): TasksApi {
        return retrofit.create(TasksApi::class.java)
    }
}