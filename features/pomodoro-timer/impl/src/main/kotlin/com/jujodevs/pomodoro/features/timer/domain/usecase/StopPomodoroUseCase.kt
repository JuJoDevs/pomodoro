package com.jujodevs.pomodoro.features.timer.domain.usecase

import com.jujodevs.pomodoro.features.timer.domain.model.PomodoroStatus
import com.jujodevs.pomodoro.features.timer.domain.repository.PomodoroRepository

class StopPomodoroUseCase(
    private val repository: PomodoroRepository,
) {
    suspend operator fun invoke() {
        repository.updateSessionState { currentState ->
            currentState.copy(
                status = PomodoroStatus.IDLE,
                remainingMillis = currentState.currentPhaseDurationMillis,
                phaseToken = "",
                lastKnownEndTimestamp = null,
            )
        }
    }
}
