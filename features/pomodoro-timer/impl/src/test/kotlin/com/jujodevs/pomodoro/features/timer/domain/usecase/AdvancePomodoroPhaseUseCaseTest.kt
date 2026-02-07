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

class AdvancePomodoroPhaseUseCaseTest {

    private lateinit var repository: FakePomodoroRepository
    private lateinit var timeProvider: FakeTimeProvider
    private lateinit var useCase: AdvancePomodoroPhaseUseCase

    @BeforeEach
    fun setUp() {
        repository = FakePomodoroRepository()
        timeProvider = FakeTimeProvider()
        useCase = AdvancePomodoroPhaseUseCase(repository, timeProvider)
    }

    @Test
    fun `GIVEN a work session is finished WHEN advancing THEN phase should be short break`() = runTest {
        // GIVEN
        repository.updateSessionState(
            PomodoroSessionState(
                currentPhase = PomodoroPhase.WORK,
                completedWorkSessions = 0,
                autoStartBreaks = false
            )
        )

        // WHEN
        useCase()

        // THEN
        val state = repository.getSessionState().first()
        state.currentPhase shouldBeEqualTo PomodoroPhase.SHORT_BREAK
        state.completedWorkSessions shouldBeEqualTo 1
        state.status shouldBeEqualTo PomodoroStatus.IDLE
    }

    @Test
    fun `GIVEN 4 work sessions are finished WHEN advancing THEN phase should be long break`() = runTest {
        // GIVEN
        repository.updateSessionState(
            PomodoroSessionState(
                currentPhase = PomodoroPhase.WORK,
                completedWorkSessions = 3, // Completing the 4th session
                autoStartBreaks = false
            )
        )

        // WHEN
        useCase()

        // THEN
        val state = repository.getSessionState().first()
        state.currentPhase shouldBeEqualTo PomodoroPhase.LONG_BREAK
        state.completedWorkSessions shouldBeEqualTo 4
    }

    @Test
    fun `GIVEN long break is finished WHEN advancing THEN phase should be work and counter reset`() = runTest {
        // GIVEN
        repository.updateSessionState(
            PomodoroSessionState(
                currentPhase = PomodoroPhase.LONG_BREAK,
                completedWorkSessions = 4,
                autoStartWork = false
            )
        )

        // WHEN
        useCase()

        // THEN
        val state = repository.getSessionState().first()
        state.currentPhase shouldBeEqualTo PomodoroPhase.WORK
        state.completedWorkSessions shouldBeEqualTo 0
    }

    @Test
    fun `GIVEN auto-start breaks is enabled WHEN advancing from work THEN status should be running`() = runTest {
        // GIVEN
        repository.updateSessionState(
            PomodoroSessionState(
                currentPhase = PomodoroPhase.WORK,
                autoStartBreaks = true
            )
        )

        // WHEN
        useCase()

        // THEN
        val state = repository.getSessionState().first()
        state.status shouldBeEqualTo PomodoroStatus.RUNNING
        state.phaseToken shouldBeEqualTo "fake-token"
    }
}
