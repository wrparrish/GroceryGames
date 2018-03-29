package com.ruthlessprogramming.interviewapp.extensions

import android.view.View

fun View.enable() {
    if (this.alpha == 1.0f && this.isClickable) {
        return
    }
    this.alpha = 1f
    this.isClickable = true
}

fun View.disable() {
    this.alpha = 0.5f
    this.isClickable = false
}