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

class StopPomodoroUseCaseTest {
    private lateinit var repository: FakePomodoroRepository
    private lateinit var useCase: StopPomodoroUseCase

    @BeforeEach
    fun setUp() {
        repository = FakePomodoroRepository()
        useCase = StopPomodoroUseCase(repository)
    }

    @Test
    fun `GIVEN running short break WHEN stopping THEN status should be idle and remaining reset`() =
        runTest {
            // GIVEN
            repository.updateSessionState(
                PomodoroSessionState(
                    currentPhase = PomodoroPhase.SHORT_BREAK,
                    status = PomodoroStatus.RUNNING,
                    selectedShortBreakMinutes = 5,
                    remainingMillis = 123_000L,
                    phaseToken = "active-token",
                    lastKnownEndTimestamp = 88_000L,
                ),
            )

            // WHEN
            useCase()

            // THEN
            val state = repository.getSessionState().first()
            state.status shouldBeEqualTo PomodoroStatus.IDLE
            state.remainingMillis shouldBeEqualTo 5 * 60 * 1000L
            state.phaseToken shouldBeEqualTo ""
            state.lastKnownEndTimestamp shouldBeEqualTo null
        }

    @Test
    fun `GIVEN running long break WHEN stopping THEN remaining should use clamped long duration`() =
        runTest {
            // GIVEN
            repository.updateSessionState(
                PomodoroSessionState(
                    currentPhase = PomodoroPhase.LONG_BREAK,
                    status = PomodoroStatus.RUNNING,
                    selectedShortBreakMinutes = 15,
                    remainingMillis = 10_000L,
                ),
            )

            // WHEN
            useCase()

            // THEN
            val state = repository.getSessionState().first()
            state.status shouldBeEqualTo PomodoroStatus.IDLE
            state.remainingMillis shouldBeEqualTo 30 * 60 * 1000L
        }
}
