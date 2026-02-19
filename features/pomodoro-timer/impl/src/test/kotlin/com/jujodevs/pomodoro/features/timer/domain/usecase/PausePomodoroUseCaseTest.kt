package com.jujodevs.pomodoro.features.timer.domain.usecase

import com.jujodevs.pomodoro.features.timer.domain.model.PomodoroPhase
import com.jujodevs.pomodoro.features.timer.domain.model.PomodoroSessionState
import com.jujodevs.pomodoro.features.timer.domain.model.PomodoroStatus
import com.jujodevs.pomodoro.features.timer.fakes.FakePomodoroRepository
import com.jujodevs.pomodoro.features.timer.fakes.FakeTimeProvider
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.amshove.kluent.shouldBeEqualTo
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class PausePomodoroUseCaseTest {
    private lateinit var repository: FakePomodoroRepository
    private lateinit var timeProvider: FakeTimeProvider
    private lateinit var useCase: PausePomodoroUseCase

    @BeforeEach
    fun setUp() {
        repository = FakePomodoroRepository()
        timeProvider = FakeTimeProvider()
        useCase = PausePomodoroUseCase(repository, timeProvider)
    }

    @Test
    fun `GIVEN running session WHEN pausing THEN status should be paused and remaining recalculated`() =
        runTest {
            // GIVEN
            timeProvider.currentTime = 1_000L
            repository.updateSessionState(
                PomodoroSessionState(
                    currentPhase = PomodoroPhase.WORK,
                    status = PomodoroStatus.RUNNING,
                    remainingMillis = 1_500L,
                    phaseToken = "active-token",
                    lastKnownEndTimestamp = 4_000L,
                ),
            )

            // WHEN
            useCase()

            // THEN
            val state = repository.getSessionState().first()
            state.status shouldBeEqualTo PomodoroStatus.PAUSED
            state.remainingMillis shouldBeEqualTo 3_000L
            state.phaseToken shouldBeEqualTo ""
            state.lastKnownEndTimestamp shouldBeEqualTo null
        }

    @Test
    fun `GIVEN running session without end timestamp WHEN pausing THEN remaining should become zero`() =
        runTest {
            // GIVEN
            timeProvider.currentTime = 5_000L
            repository.updateSessionState(
                PomodoroSessionState(
                    status = PomodoroStatus.RUNNING,
                    remainingMillis = 60_000L,
                    phaseToken = "active-token",
                    lastKnownEndTimestamp = null,
                ),
            )

            // WHEN
            useCase()

            // THEN
            val state = repository.getSessionState().first()
            state.status shouldBeEqualTo PomodoroStatus.PAUSED
            state.remainingMillis shouldBeEqualTo 0L
            state.phaseToken shouldBeEqualTo ""
        }

    @Test
    fun `GIVEN non running session WHEN pausing THEN state should remain unchanged`() =
        runTest {
            // GIVEN
            val initial =
                PomodoroSessionState(
                    status = PomodoroStatus.IDLE,
                    remainingMillis = 11_000L,
                    phaseToken = "token",
                    lastKnownEndTimestamp = 99_000L,
                )
            repository.updateSessionState(initial)

            // WHEN
            useCase()

            // THEN
            val state = repository.getSessionState().first()
            state shouldBeEqualTo initial
        }
}
