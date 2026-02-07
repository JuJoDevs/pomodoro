package com.jujodevs.pomodoro.features.timer.domain.usecase

import com.jujodevs.pomodoro.features.timer.domain.model.PomodoroPhase
import com.jujodevs.pomodoro.features.timer.domain.model.PomodoroSessionState
import com.jujodevs.pomodoro.features.timer.domain.model.PomodoroStatus
import com.jujodevs.pomodoro.features.timer.fakes.FakePomodoroRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.amshove.kluent.shouldBeEqualTo
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class ResetPomodoroUseCaseTest {

    private lateinit var repository: FakePomodoroRepository
    private lateinit var useCase: ResetPomodoroUseCase

    @BeforeEach
    fun setUp() {
        repository = FakePomodoroRepository()
        useCase = ResetPomodoroUseCase(repository)
    }

    @Test
    fun `GIVEN active long break WHEN resetting THEN state should return to idle work start`() = runTest {
        // GIVEN
        repository.updateSessionState(
            PomodoroSessionState(
                selectedWorkMinutes = 30,
                selectedShortBreakMinutes = 10,
                currentPhase = PomodoroPhase.LONG_BREAK,
                status = PomodoroStatus.RUNNING,
                remainingMillis = 123_000L,
                completedWorkSessions = 4,
                phaseToken = "active-token",
                lastKnownEndTimestamp = 90_000L
            )
        )

        // WHEN
        useCase()

        // THEN
        val state = repository.getSessionState().first()
        state.currentPhase shouldBeEqualTo PomodoroPhase.WORK
        state.status shouldBeEqualTo PomodoroStatus.IDLE
        state.completedWorkSessions shouldBeEqualTo 0
        state.remainingMillis shouldBeEqualTo 30 * 60 * 1000L
        state.phaseToken shouldBeEqualTo ""
        state.lastKnownEndTimestamp shouldBeEqualTo null
        state.selectedShortBreakMinutes shouldBeEqualTo 10
    }
}
