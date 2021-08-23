package com.example.dudu.di.core

import okhttp3.Interceptor
import okhttp3.OkHttpClient

fun OkHttpClient.Builder.addInterceptors(
    interceptors: Iterable<Interceptor>
): OkHttpClient.Builder {
    interceptors.forEach { interceptor ->
        this.addInterceptor(interceptor)
    }
    return this
}