package com.example.dudu

import android.view.View
import android.view.animation.AlphaAnimation

fun View.startAlphaAnimation(duration: Long, visibility: Int) {
    val animation = if (visibility == View.VISIBLE)
        AlphaAnimation(0f, 1f)
    else
        AlphaAnimation(1f, 0f)
    animation.duration = duration
    animation.fillAfter = true
    this.startAnimation(animation)
}