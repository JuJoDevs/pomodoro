package com.jujodevs.pomodoro.features.timer.fakes

import com.jujodevs.pomodoro.features.timer.domain.provider.TimeProvider

class FakeTimeProvider : TimeProvider {
    var currentTime: Long = 1000L
    var nextToken: String = "fake-token"

    override fun getCurrentTimeMillis(): Long = currentTime

    override fun generateToken(): String = nextToken
}
