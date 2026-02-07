package com.jujodevs.pomodoro.features.timer.fakes

import com.jujodevs.pomodoro.features.timer.domain.model.PomodoroSessionState
import com.jujodevs.pomodoro.features.timer.domain.repository.PomodoroRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

class FakePomodoroRepository : PomodoroRepository {
    private val _state = MutableStateFlow(PomodoroSessionState())

    override fun getSessionState(): Flow<PomodoroSessionState> = _state

    override suspend fun updateSessionState(state: PomodoroSessionState) {
        _state.value = state
    }

    override suspend fun updateSessionState(update: (PomodoroSessionState) -> PomodoroSessionState) {
        _state.update(update)
    }
}
