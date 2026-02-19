package com.jujodevs.pomodoro.features.timer.domain.repository

import com.jujodevs.pomodoro.features.timer.domain.model.PomodoroSessionState
import kotlinx.coroutines.flow.Flow

interface PomodoroRepository {
    fun getSessionState(): Flow<PomodoroSessionState>

    suspend fun updateSessionState(state: PomodoroSessionState)

    suspend fun updateSessionState(update: (PomodoroSessionState) -> PomodoroSessionState)
}
