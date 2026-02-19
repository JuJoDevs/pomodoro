package com.jujodevs.pomodoro.features.timer.domain.usecase

import com.jujodevs.pomodoro.features.timer.domain.model.PomodoroSessionState
import com.jujodevs.pomodoro.features.timer.domain.repository.PomodoroRepository
import kotlinx.coroutines.flow.Flow

class ObservePomodoroSessionStateUseCase(
    private val repository: PomodoroRepository,
) {
    operator fun invoke(): Flow<PomodoroSessionState> = repository.getSessionState()
}
