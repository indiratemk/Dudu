package com.example.dudu.data.helpers

sealed class Resource<out T: Any> {

    data class Success<out T: Any>(val data: T) : Resource<T>()

    // TODO: 7/7/21 изменить
    data class Error(val exception: Exception) : Resource<Nothing>()
}