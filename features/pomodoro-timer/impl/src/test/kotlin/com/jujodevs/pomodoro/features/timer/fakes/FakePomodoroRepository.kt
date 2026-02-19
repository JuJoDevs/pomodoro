package com.jujodevs.pomodoro.features.timer.fakes

import com.jujodevs.pomodoro.features.timer.domain.model.PomodoroSessionState
import com.jujodevs.pomodoro.features.timer.domain.repository.PomodoroRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

class FakePomodoroRepository : PomodoroRepository {
    private val sessionState = MutableStateFlow(PomodoroSessionState())

    override fun getSessionState(): Flow<PomodoroSessionState> = sessionState

    override suspend fun updateSessionState(state: PomodoroSessionState) {
        sessionState.value = state
    }

    override suspend fun updateSessionState(update: (PomodoroSessionState) -> PomodoroSessionState) {
        sessionState.update(update)
    }
}
