package com.example.algartech.animation

import android.view.View
import android.view.animation.TranslateAnimation

object AnimationUtils {
    fun slideViewUp(view: View) {
        val slideUp = TranslateAnimation(0f, 0f, 0f, -view.height.toFloat())
        slideUp.duration = 300
        view.startAnimation(slideUp)
    }

    fun slideViewDown(view: View) {
        val slideDown = TranslateAnimation(0f, 0f, -view.height.toFloat(), 0f)
        slideDown.duration = 300
        view.startAnimation(slideDown)
    }
}