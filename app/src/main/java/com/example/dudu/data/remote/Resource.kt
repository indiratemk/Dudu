package com.example.dudu.data.remote

sealed class Resource<T> {

    class Success<T>(val data: T) : Resource<T>()

    class Loading<T>(val data: T?) : Resource<T>()

    class Error<T>(val exception: Exception, val data: T? =  null, val query: Query) : Resource<T>()
}