package com.example.dudu.util

private var lastClickTime = 0L

fun <T> debounce(
    delayInMillis: Long,
    action: (T) -> Unit
): (T) -> Unit {
    return {
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastClickTime >= delayInMillis) {
            lastClickTime = currentTime
            action(it)
        }
    }
}