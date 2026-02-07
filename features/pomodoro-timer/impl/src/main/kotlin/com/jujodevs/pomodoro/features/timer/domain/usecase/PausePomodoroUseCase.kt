package com.jujodevs.pomodoro.features.timer.domain.usecase

import com.jujodevs.pomodoro.features.timer.domain.model.PomodoroStatus
import com.jujodevs.pomodoro.features.timer.domain.provider.TimeProvider
import com.jujodevs.pomodoro.features.timer.domain.repository.PomodoroRepository

class PausePomodoroUseCase(
    private val repository: PomodoroRepository,
    private val timeProvider: TimeProvider
) {
    suspend operator fun invoke() {
        repository.updateSessionState { currentState ->
            if (currentState.status != PomodoroStatus.RUNNING) return@updateSessionState currentState

            val now = timeProvider.getCurrentTimeMillis()
            val remaining = (currentState.lastKnownEndTimestamp ?: now) - now

            currentState.copy(
                status = PomodoroStatus.PAUSED,
                remainingMillis = remaining.coerceAtLeast(0),
                phaseToken = "", // Invalidate current alarm token
                lastKnownEndTimestamp = null
            )
        }
    }
}
