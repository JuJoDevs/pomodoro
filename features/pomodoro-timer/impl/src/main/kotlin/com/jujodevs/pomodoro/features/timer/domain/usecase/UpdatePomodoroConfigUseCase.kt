package com.jujodevs.pomodoro.features.timer.domain.usecase

import com.jujodevs.pomodoro.features.timer.domain.model.PomodoroPhase
import com.jujodevs.pomodoro.features.timer.domain.model.PomodoroStatus
import com.jujodevs.pomodoro.features.timer.domain.repository.PomodoroRepository

class UpdatePomodoroConfigUseCase(
    private val repository: PomodoroRepository
) {
    suspend fun updateWorkDuration(minutes: Int) {
        repository.updateSessionState { currentState ->
            val updated = currentState.copy(selectedWorkMinutes = minutes)
            if (currentState.status == PomodoroStatus.IDLE &&
                currentState.currentPhase == PomodoroPhase.WORK
            ) {
                updated.copy(remainingMillis = minutes * 60 * 1000L)
            } else {
                updated
            }
        }
    }

    suspend fun updateShortBreakDuration(minutes: Int) {
        repository.updateSessionState { currentState ->
            val updated = currentState.copy(selectedShortBreakMinutes = minutes)
            if (currentState.status == PomodoroStatus.IDLE &&
                currentState.currentPhase == PomodoroPhase.SHORT_BREAK
            ) {
                updated.copy(remainingMillis = minutes * 60 * 1000L)
            } else {
                updated
            }
        }
    }

    suspend fun toggleAutoStartBreaks(enabled: Boolean) {
        repository.updateSessionState { it.copy(autoStartBreaks = enabled) }
    }

    suspend fun toggleAutoStartWork(enabled: Boolean) {
        repository.updateSessionState { it.copy(autoStartWork = enabled) }
    }

    suspend fun updateExactAlarmWarningSnoozedUntil(snoozedUntilMillis: Long?) {
        repository.updateSessionState { it.copy(exactAlarmWarningSnoozedUntilMillis = snoozedUntilMillis) }
    }
}
