package com.jujodevs.pomodoro.features.timer.domain.usecase

import com.jujodevs.pomodoro.features.timer.domain.model.PomodoroPhase
import com.jujodevs.pomodoro.features.timer.domain.model.PomodoroStatus
import com.jujodevs.pomodoro.features.timer.domain.repository.PomodoroRepository

class ResetPomodoroUseCase(
    private val repository: PomodoroRepository,
) {
    suspend operator fun invoke() {
        repository.updateSessionState { currentState ->
            currentState.copy(
                currentPhase = PomodoroPhase.WORK,
                status = PomodoroStatus.IDLE,
                completedWorkSessions = 0,
                remainingMillis = currentState.selectedWorkMinutes * 60 * 1000L,
                phaseToken = "",
                lastKnownEndTimestamp = null,
            )
        }
    }
}
