package com.jujodevs.pomodoro.features.timer.data

import com.jujodevs.pomodoro.features.timer.domain.provider.TimeProvider
import java.util.UUID

class SystemTimeProvider : TimeProvider {
    override fun getCurrentTimeMillis(): Long = System.currentTimeMillis()

    override fun generateToken(): String = UUID.randomUUID().toString()
}
