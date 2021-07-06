package com.example.dudu.data.remote

import okhttp3.Interceptor
import okhttp3.Response

class RequestInterceptor : Interceptor {

    companion object {
        private const val AUTH_HEADER = "Authorization"
        private const val TOKEN = "Bearer e3b917577ee34cd7ab0e1ee052c71eac"
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        val requestBuilder = chain.request().newBuilder()
        requestBuilder.addHeader(AUTH_HEADER, TOKEN)
        return chain.proceed(requestBuilder.build())
    }
}