package com.jujodevs.pomodoro.features.timer.domain.provider

interface TimeProvider {
    fun getCurrentTimeMillis(): Long
    fun generateToken(): String
}
