package com.example.dudu.di.modules

import com.example.dudu.BuildConfig
import com.example.dudu.data.remote.RemoteDataSource
import com.example.dudu.data.remote.interceptors.RequestInterceptor
import com.example.dudu.data.remote.TasksApi
import com.example.dudu.di.scopes.AppScope
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

@Module
object NetworkModule {

    private const val BASE_URL = "https://d5dps3h13rv6902lp5c8.apigw.yandexcloud.net/"

    @Provides
    @AppScope
    fun provideRequestInterceptor(): RequestInterceptor {
        return RequestInterceptor()
    }

    @Provides
    @AppScope
    fun httpLoggingInterceptor(): HttpLoggingInterceptor {
        return if (BuildConfig.DEBUG)
            HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
        else
            HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.NONE)
    }

    @Provides
    @AppScope
    fun provideOkHttpClient(
        requestInterceptor: RequestInterceptor
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .addInterceptor(requestInterceptor)
            .addInterceptor(httpLoggingInterceptor())
            .build()
    }

    @Provides
    @AppScope
    fun provideGsonConverterFactory(): GsonConverterFactory {
        return GsonConverterFactory.create()
    }

    @Provides
    @AppScope
    fun provideRetrofit(
        gsonConverterFactory: GsonConverterFactory,
        okHttpClient: OkHttpClient
    ): Retrofit {
        return Retrofit.Builder()
            .client(okHttpClient)
            .baseUrl(BASE_URL)
            .addConverterFactory(gsonConverterFactory)
            .build()
    }

    @Provides
    @AppScope
    fun provideTasksApi(retrofit: Retrofit): TasksApi {
        return retrofit.create(TasksApi::class.java)
    }

    @Provides
    @AppScope
    fun provideRemoteDataSource(tasksApi: TasksApi): RemoteDataSource {
        return RemoteDataSource(tasksApi)
    }
}