package com.jujodevs.pomodoro.features.timer.domain.usecase

import com.jujodevs.pomodoro.features.timer.domain.model.PomodoroPhase
import com.jujodevs.pomodoro.features.timer.domain.model.PomodoroSessionState
import com.jujodevs.pomodoro.features.timer.domain.model.PomodoroStatus
import com.jujodevs.pomodoro.features.timer.domain.provider.TimeProvider
import com.jujodevs.pomodoro.features.timer.domain.repository.PomodoroRepository

class ReconcileExpiredPomodoroPhasesUseCase(
    private val repository: PomodoroRepository,
    private val timeProvider: TimeProvider,
) {
    suspend operator fun invoke(initialState: PomodoroSessionState): List<PomodoroSessionState> {
        val nowMillis = timeProvider.getCurrentTimeMillis()
        if (!initialState.isExpiredRunningPhase(nowMillis)) {
            return emptyList()
        }

        val completedPhases = mutableListOf<PomodoroSessionState>()
        var reconciledState = initialState
        var reconciledCount = 0

        while (reconciledState.isExpiredRunningPhase(nowMillis) && reconciledCount < MAX_RECONCILED_PHASES) {
            completedPhases += reconciledState
            reconciledState = reconciledState.advanceUsingElapsedTimeline(nowMillis)
            reconciledCount++
        }

        repository.updateSessionState(reconciledState)
        return completedPhases
    }

    suspend fun reconcileByToken(token: String): List<PomodoroSessionState> {
        if (token.isBlank()) {
            return emptyList()
        }

        val nowMillis = timeProvider.getCurrentTimeMillis()
        val completedPhases = mutableListOf<PomodoroSessionState>()

        repository.updateSessionState { currentState ->
            if (currentState.phaseToken != token || !currentState.isExpiredRunningPhase(nowMillis)) {
                return@updateSessionState currentState
            }

            var reconciledState = currentState
            var reconciledCount = 0

            while (reconciledState.isExpiredRunningPhase(nowMillis) && reconciledCount < MAX_RECONCILED_PHASES) {
                completedPhases += reconciledState
                reconciledState = reconciledState.advanceUsingElapsedTimeline(nowMillis)
                reconciledCount++
            }

            reconciledState
        }

        return completedPhases
    }

    private fun PomodoroSessionState.advanceUsingElapsedTimeline(nowMillis: Long): PomodoroSessionState {
        val nextPhase = calculateNextPhase(this)
        val nextCompletedSessions = calculateNextCompletedSessions(this, nextPhase)
        val nextStatus = calculateNextStatus(this, nextPhase)
        val nextDurationMillis = calculateNextDurationMillis(this, nextPhase)
        val previousEndTimestamp = lastKnownEndTimestamp ?: nowMillis

        val nextEndTimestamp =
            if (nextStatus == PomodoroStatus.RUNNING) {
                previousEndTimestamp + nextDurationMillis
            } else {
                null
            }
        val nextRemainingMillis =
            if (nextStatus == PomodoroStatus.RUNNING) {
                (nextEndTimestamp ?: nowMillis) - nowMillis
            } else {
                nextDurationMillis
            }.coerceAtLeast(0L)

        return copy(
            currentPhase = nextPhase,
            status = nextStatus,
            completedWorkSessions = nextCompletedSessions,
            remainingMillis = nextRemainingMillis,
            phaseToken = if (nextStatus == PomodoroStatus.RUNNING) timeProvider.generateToken() else "",
            lastKnownEndTimestamp = nextEndTimestamp,
        )
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

            PomodoroPhase.SHORT_BREAK,
            PomodoroPhase.LONG_BREAK,
            -> PomodoroPhase.WORK
        }

    private fun calculateNextCompletedSessions(
        state: PomodoroSessionState,
        nextPhase: PomodoroPhase,
    ): Int =
        when {
            state.currentPhase == PomodoroPhase.WORK ->
                (state.completedWorkSessions + 1) % (state.totalSessions + 1)

            nextPhase == PomodoroPhase.WORK && state.currentPhase == PomodoroPhase.LONG_BREAK ->
                0

            else -> state.completedWorkSessions
        }

    private fun calculateNextStatus(
        state: PomodoroSessionState,
        nextPhase: PomodoroPhase,
    ): PomodoroStatus {
        val autoStartEnabled =
            when (nextPhase) {
                PomodoroPhase.WORK -> state.autoStartWork
                PomodoroPhase.SHORT_BREAK,
                PomodoroPhase.LONG_BREAK,
                -> state.autoStartBreaks
            }
        return if (autoStartEnabled) PomodoroStatus.RUNNING else PomodoroStatus.IDLE
    }

    private fun calculateNextDurationMillis(
        state: PomodoroSessionState,
        nextPhase: PomodoroPhase,
    ): Long =
        when (nextPhase) {
            PomodoroPhase.WORK -> state.selectedWorkMinutes * MILLIS_IN_MINUTE
            PomodoroPhase.SHORT_BREAK -> state.selectedShortBreakMinutes * MILLIS_IN_MINUTE
            PomodoroPhase.LONG_BREAK -> state.longBreakMinutes * MILLIS_IN_MINUTE
        }

    private fun PomodoroSessionState.isExpiredRunningPhase(nowMillis: Long): Boolean =
        status == PomodoroStatus.RUNNING &&
            phaseToken.isNotBlank() &&
            lastKnownEndTimestamp != null &&
            lastKnownEndTimestamp <= nowMillis

    companion object {
        private const val MILLIS_IN_MINUTE = 60 * 1000L
        private const val MAX_RECONCILED_PHASES = 1000
    }
}
