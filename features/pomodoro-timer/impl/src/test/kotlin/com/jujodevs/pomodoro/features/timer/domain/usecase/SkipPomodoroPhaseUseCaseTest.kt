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

class SkipPomodoroPhaseUseCaseTest {
    private lateinit var repository: FakePomodoroRepository
    private lateinit var useCase: SkipPomodoroPhaseUseCase

    @BeforeEach
    fun setUp() {
        repository = FakePomodoroRepository()
        useCase = SkipPomodoroPhaseUseCase(repository)
    }

    @Test
    fun `GIVEN first work session WHEN skipping THEN next phase should be short break`() =
        runTest {
            // GIVEN
            repository.updateSessionState(
                PomodoroSessionState(
                    currentPhase = PomodoroPhase.WORK,
                    status = PomodoroStatus.RUNNING,
                    selectedShortBreakMinutes = 5,
                    completedWorkSessions = 0,
                    phaseToken = "token",
                    lastKnownEndTimestamp = 10_000L,
                ),
            )

            // WHEN
            useCase()

            // THEN
            val state = repository.getSessionState().first()
            state.currentPhase shouldBeEqualTo PomodoroPhase.SHORT_BREAK
            state.completedWorkSessions shouldBeEqualTo 1
            state.status shouldBeEqualTo PomodoroStatus.IDLE
            state.remainingMillis shouldBeEqualTo 5 * 60 * 1000L
            state.phaseToken shouldBeEqualTo ""
            state.lastKnownEndTimestamp shouldBeEqualTo null
        }

    @Test
    fun `GIVEN fourth work session WHEN skipping THEN next phase should be long break`() =
        runTest {
            // GIVEN
            repository.updateSessionState(
                PomodoroSessionState(
                    currentPhase = PomodoroPhase.WORK,
                    selectedShortBreakMinutes = 15,
                    completedWorkSessions = 3,
                ),
            )

            // WHEN
            useCase()

            // THEN
            val state = repository.getSessionState().first()
            state.currentPhase shouldBeEqualTo PomodoroPhase.LONG_BREAK
            state.completedWorkSessions shouldBeEqualTo 4
            state.remainingMillis shouldBeEqualTo 30 * 60 * 1000L
        }

    @Test
    fun `GIVEN long break phase WHEN skipping THEN progress should reset and return to work`() =
        runTest {
            // GIVEN
            repository.updateSessionState(
                PomodoroSessionState(
                    currentPhase = PomodoroPhase.LONG_BREAK,
                    selectedWorkMinutes = 25,
                    completedWorkSessions = 4,
                ),
            )

            // WHEN
            useCase()

            // THEN
            val state = repository.getSessionState().first()
            state.currentPhase shouldBeEqualTo PomodoroPhase.WORK
            state.completedWorkSessions shouldBeEqualTo 0
            state.remainingMillis shouldBeEqualTo 25 * 60 * 1000L
        }
}
