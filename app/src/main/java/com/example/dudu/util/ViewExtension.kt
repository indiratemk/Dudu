package com.example.dudu.util

import android.view.View

fun View.setOnClickListenerWithDebounce(
    delayInMillis: Long = 300L,
    listener: (View) -> Unit
) {
    setOnClickListener(debounce<View>(delayInMillis) { listener(it) })
}