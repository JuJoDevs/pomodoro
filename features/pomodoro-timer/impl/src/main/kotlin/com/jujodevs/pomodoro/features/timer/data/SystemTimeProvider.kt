package com.jujodevs.pomodoro.features.timer.data

import com.jujodevs.pomodoro.features.timer.domain.provider.TimeProvider
import java.util.UUID

class SystemTimeProvider : TimeProvider {
    override fun getCurrentTimeMillis(): Long {
        return System.currentTimeMillis()
    }

    override fun generateToken(): String {
        return UUID.randomUUID().toString()
    }
}
