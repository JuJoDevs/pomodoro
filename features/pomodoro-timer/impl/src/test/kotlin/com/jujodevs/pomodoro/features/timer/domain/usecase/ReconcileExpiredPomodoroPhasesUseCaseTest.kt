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

class ReconcileExpiredPomodoroPhasesUseCaseTest {
    private lateinit var repository: FakePomodoroRepository
    private lateinit var timeProvider: FakeTimeProvider
    private lateinit var useCase: ReconcileExpiredPomodoroPhasesUseCase

    @BeforeEach
    fun setUp() {
        repository = FakePomodoroRepository()
        timeProvider = FakeTimeProvider()
        useCase = ReconcileExpiredPomodoroPhasesUseCase(repository, timeProvider)
    }

    @Test
    fun `GIVEN running state that has not expired WHEN reconciling THEN should keep state unchanged`() =
        runTest {
            // GIVEN
            val initialState =
                PomodoroSessionState(
                    currentPhase = PomodoroPhase.WORK,
                    status = PomodoroStatus.RUNNING,
                    phaseToken = "token-1",
                    lastKnownEndTimestamp = timeProvider.currentTime + 60_000L,
                )
            repository.updateSessionState(initialState)

            // WHEN
            val completedPhases = useCase(initialState)

            // THEN
            completedPhases.size shouldBeEqualTo 0
            repository.getSessionState().first() shouldBeEqualTo initialState
        }

    @Test
    fun `GIVEN expired running phase without auto-start WHEN reconciling THEN should move to next idle phase`() =
        runTest {
            // GIVEN
            val initialState =
                PomodoroSessionState(
                    selectedWorkMinutes = 1,
                    selectedShortBreakMinutes = 1,
                    currentPhase = PomodoroPhase.WORK,
                    status = PomodoroStatus.RUNNING,
                    remainingMillis = 60_000L,
                    phaseToken = "token-1",
                    lastKnownEndTimestamp = timeProvider.currentTime - 1_000L,
                )
            repository.updateSessionState(initialState)

            // WHEN
            val completedPhases = useCase(initialState)

            // THEN
            completedPhases.map { it.currentPhase } shouldBeEqualTo listOf(PomodoroPhase.WORK)
            val state = repository.getSessionState().first()
            state.currentPhase shouldBeEqualTo PomodoroPhase.SHORT_BREAK
            state.status shouldBeEqualTo PomodoroStatus.IDLE
            state.completedWorkSessions shouldBeEqualTo 1
            state.lastKnownEndTimestamp shouldBeEqualTo null
        }

    @Test
    fun `GIVEN several expired phases and auto-start enabled WHEN reconciling THEN catches up completed phases`() =
        runTest {
            // GIVEN
            val initialState =
                PomodoroSessionState(
                    selectedWorkMinutes = 1,
                    selectedShortBreakMinutes = 1,
                    autoStartBreaks = true,
                    autoStartWork = true,
                    currentPhase = PomodoroPhase.WORK,
                    status = PomodoroStatus.RUNNING,
                    remainingMillis = 60_000L,
                    completedWorkSessions = 0,
                    phaseToken = "token-1",
                    lastKnownEndTimestamp = timeProvider.currentTime - 125_000L,
                )
            repository.updateSessionState(initialState)

            // WHEN
            val completedPhases = useCase(initialState)

            // THEN
            completedPhases.map { it.currentPhase } shouldBeEqualTo
                listOf(
                    PomodoroPhase.WORK,
                    PomodoroPhase.SHORT_BREAK,
                    PomodoroPhase.WORK,
                )

            val state = repository.getSessionState().first()
            state.currentPhase shouldBeEqualTo PomodoroPhase.SHORT_BREAK
            state.status shouldBeEqualTo PomodoroStatus.RUNNING
            state.completedWorkSessions shouldBeEqualTo 2
            state.lastKnownEndTimestamp shouldBeEqualTo timeProvider.currentTime + 55_000L
            state.remainingMillis shouldBeEqualTo 55_000L
        }
}
