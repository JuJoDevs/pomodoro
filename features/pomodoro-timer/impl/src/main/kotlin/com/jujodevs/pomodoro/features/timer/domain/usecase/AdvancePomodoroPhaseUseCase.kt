package com.jujodevs.pomodoro.features.timer.domain.usecase

import com.jujodevs.pomodoro.features.timer.domain.model.PomodoroPhase
import com.jujodevs.pomodoro.features.timer.domain.model.PomodoroSessionState
import com.jujodevs.pomodoro.features.timer.domain.model.PomodoroStatus
import com.jujodevs.pomodoro.features.timer.domain.provider.TimeProvider
import com.jujodevs.pomodoro.features.timer.domain.repository.PomodoroRepository

class AdvancePomodoroPhaseUseCase(
    private val repository: PomodoroRepository,
    private val timeProvider: TimeProvider
) {
    suspend operator fun invoke() {
        repository.updateSessionState { currentState ->
            val nextPhase = calculateNextPhase(currentState)
            val nextCompletedSessions = calculateNextCompletedSessions(currentState, nextPhase)
            val nextStatus = calculateNextStatus(currentState, nextPhase)
            val nextToken = if (nextStatus == PomodoroStatus.RUNNING) timeProvider.generateToken() else ""
            val nextDurationMillis = calculateNextDuration(currentState, nextPhase)

            val nextEndTimestamp = if (nextStatus == PomodoroStatus.RUNNING) {
                timeProvider.getCurrentTimeMillis() + nextDurationMillis
            } else {
                null
            }

            currentState.copy(
                currentPhase = nextPhase,
                status = nextStatus,
                completedWorkSessions = nextCompletedSessions,
                remainingMillis = nextDurationMillis,
                phaseToken = nextToken,
                lastKnownEndTimestamp = nextEndTimestamp
            )
        }
    }

    private fun calculateNextPhase(state: PomodoroSessionState): PomodoroPhase =
        when (state.currentPhase) {
            PomodoroPhase.WORK -> {
                if (state.completedWorkSessions + 1 >= state.totalSessions) {
                    PomodoroPhase.LONG_BREAK
                } else {
                    PomodoroPhase.SHORT_BREAK
                }
            }
            PomodoroPhase.SHORT_BREAK, PomodoroPhase.LONG_BREAK -> PomodoroPhase.WORK
        }

    private fun calculateNextCompletedSessions(
        state: PomodoroSessionState,
        nextPhase: PomodoroPhase
    ): Int = when {
        state.currentPhase == PomodoroPhase.WORK ->
            (state.completedWorkSessions + 1) % (state.totalSessions + 1)
        nextPhase == PomodoroPhase.WORK && state.currentPhase == PomodoroPhase.LONG_BREAK ->
            0
        else -> state.completedWorkSessions
    }

    private fun calculateNextStatus(
        state: PomodoroSessionState,
        nextPhase: PomodoroPhase
    ): PomodoroStatus {
        val autoStart = when (nextPhase) {
            PomodoroPhase.WORK -> state.autoStartWork
            PomodoroPhase.SHORT_BREAK, PomodoroPhase.LONG_BREAK -> state.autoStartBreaks
        }
        return if (autoStart) PomodoroStatus.RUNNING else PomodoroStatus.IDLE
    }

    private fun calculateNextDuration(
        state: PomodoroSessionState,
        nextPhase: PomodoroPhase
    ): Long = when (nextPhase) {
        PomodoroPhase.WORK -> state.selectedWorkMinutes * MILLIS_IN_MINUTE
        PomodoroPhase.SHORT_BREAK -> state.selectedShortBreakMinutes * MILLIS_IN_MINUTE
        PomodoroPhase.LONG_BREAK -> state.longBreakMinutes * MILLIS_IN_MINUTE
    }

    companion object {
        private const val MILLIS_IN_MINUTE = 60 * 1000L
    }
}
