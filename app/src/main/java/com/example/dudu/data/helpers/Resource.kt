package com.example.dudu.data.helpers

sealed class Resource<out T: Any> {

    object Loading : Resource<Nothing>()

    data class Loaded<out T: Any>(val data: T) : Resource<T>()

    data class Error(val message: String) : Resource<Nothing>()
}