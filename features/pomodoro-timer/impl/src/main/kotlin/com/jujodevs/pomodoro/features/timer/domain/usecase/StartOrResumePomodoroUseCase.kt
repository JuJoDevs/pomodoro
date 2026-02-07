package com.jujodevs.pomodoro.features.timer.domain.usecase

import com.jujodevs.pomodoro.features.timer.domain.model.PomodoroStatus
import com.jujodevs.pomodoro.features.timer.domain.provider.TimeProvider
import com.jujodevs.pomodoro.features.timer.domain.repository.PomodoroRepository

class StartOrResumePomodoroUseCase(
    private val repository: PomodoroRepository,
    private val timeProvider: TimeProvider
) {
    suspend operator fun invoke() {
        repository.updateSessionState { currentState ->
            if (currentState.status == PomodoroStatus.RUNNING) return@updateSessionState currentState

            val nextToken = timeProvider.generateToken()
            val nextEndTimestamp = timeProvider.getCurrentTimeMillis() + currentState.remainingMillis

            currentState.copy(
                status = PomodoroStatus.RUNNING,
                phaseToken = nextToken,
                lastKnownEndTimestamp = nextEndTimestamp
            )
        }
    }
}
