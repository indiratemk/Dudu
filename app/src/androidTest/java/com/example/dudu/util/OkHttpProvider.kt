package com.example.dudu.util

import okhttp3.OkHttpClient

object OkHttpProvider {

    val instance: OkHttpClient = OkHttpClient.Builder().build()
}