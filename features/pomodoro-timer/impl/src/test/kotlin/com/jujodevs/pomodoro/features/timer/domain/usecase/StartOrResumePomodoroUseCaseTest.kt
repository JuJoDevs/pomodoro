package com.jujodevs.pomodoro.features.timer.domain.usecase

import com.jujodevs.pomodoro.features.timer.domain.model.PomodoroSessionState
import com.jujodevs.pomodoro.features.timer.domain.model.PomodoroStatus
import com.jujodevs.pomodoro.features.timer.fakes.FakePomodoroRepository
import com.jujodevs.pomodoro.features.timer.fakes.FakeTimeProvider
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.amshove.kluent.shouldBeEqualTo
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class StartOrResumePomodoroUseCaseTest {
    private lateinit var repository: FakePomodoroRepository
    private lateinit var timeProvider: FakeTimeProvider
    private lateinit var useCase: StartOrResumePomodoroUseCase

    @BeforeEach
    fun setUp() {
        repository = FakePomodoroRepository()
        timeProvider = FakeTimeProvider()
        useCase = StartOrResumePomodoroUseCase(repository, timeProvider)
    }

    @Test
    fun `GIVEN idle state WHEN starting THEN status should be running with new token and end timestamp`() =
        runTest {
            // GIVEN
            timeProvider.currentTime = 1_000L
            timeProvider.nextToken = "new-token"
            repository.updateSessionState(
                PomodoroSessionState(
                    status = PomodoroStatus.IDLE,
                    remainingMillis = 5_000L,
                ),
            )

            // WHEN
            useCase()

            // THEN
            val state = repository.getSessionState().first()
            state.status shouldBeEqualTo PomodoroStatus.RUNNING
            state.phaseToken shouldBeEqualTo "new-token"
            state.lastKnownEndTimestamp shouldBeEqualTo 6_000L
        }

    @Test
    fun `GIVEN paused state WHEN resuming THEN timer should continue from remaining millis`() =
        runTest {
            // GIVEN
            timeProvider.currentTime = 20_000L
            timeProvider.nextToken = "resume-token"
            repository.updateSessionState(
                PomodoroSessionState(
                    status = PomodoroStatus.PAUSED,
                    remainingMillis = 90_000L,
                ),
            )

            // WHEN
            useCase()

            // THEN
            val state = repository.getSessionState().first()
            state.status shouldBeEqualTo PomodoroStatus.RUNNING
            state.phaseToken shouldBeEqualTo "resume-token"
            state.lastKnownEndTimestamp shouldBeEqualTo 110_000L
        }

    @Test
    fun `GIVEN already running state WHEN invoking THEN state should remain unchanged`() =
        runTest {
            // GIVEN
            val initial =
                PomodoroSessionState(
                    status = PomodoroStatus.RUNNING,
                    phaseToken = "active-token",
                    lastKnownEndTimestamp = 77_000L,
                )
            repository.updateSessionState(initial)

            // WHEN
            useCase()

            // THEN
            val state = repository.getSessionState().first()
            state shouldBeEqualTo initial
        }
}
