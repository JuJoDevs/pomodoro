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

class UpdatePomodoroConfigUseCaseTest {
    private lateinit var repository: FakePomodoroRepository
    private lateinit var useCase: UpdatePomodoroConfigUseCase

    @BeforeEach
    fun setUp() {
        repository = FakePomodoroRepository()
        useCase = UpdatePomodoroConfigUseCase(repository)
    }

    @Test
    fun `GIVEN idle work phase WHEN updating work duration THEN selection and remaining should be updated`() =
        runTest {
            // GIVEN
            repository.updateSessionState(
                PomodoroSessionState(
                    status = PomodoroStatus.IDLE,
                    currentPhase = PomodoroPhase.WORK,
                    selectedWorkMinutes = 25,
                    remainingMillis = 25 * 60 * 1000L,
                ),
            )

            // WHEN
            useCase.updateWorkDuration(30)

            // THEN
            val state = repository.getSessionState().first()
            state.selectedWorkMinutes shouldBeEqualTo 30
            state.remainingMillis shouldBeEqualTo 30 * 60 * 1000L
        }

    @Test
    fun `GIVEN running work phase WHEN updating work duration THEN only selection should change`() =
        runTest {
            // GIVEN
            repository.updateSessionState(
                PomodoroSessionState(
                    status = PomodoroStatus.RUNNING,
                    currentPhase = PomodoroPhase.WORK,
                    selectedWorkMinutes = 25,
                    remainingMillis = 10 * 60 * 1000L,
                ),
            )

            // WHEN
            useCase.updateWorkDuration(20)

            // THEN
            val state = repository.getSessionState().first()
            state.selectedWorkMinutes shouldBeEqualTo 20
            state.remainingMillis shouldBeEqualTo 10 * 60 * 1000L
        }

    @Test
    fun `GIVEN idle short break WHEN updating duration THEN selection and remaining should update`() =
        runTest {
            // GIVEN
            repository.updateSessionState(
                PomodoroSessionState(
                    status = PomodoroStatus.IDLE,
                    currentPhase = PomodoroPhase.SHORT_BREAK,
                    selectedShortBreakMinutes = 5,
                    remainingMillis = 5 * 60 * 1000L,
                ),
            )

            // WHEN
            useCase.updateShortBreakDuration(10)

            // THEN
            val state = repository.getSessionState().first()
            state.selectedShortBreakMinutes shouldBeEqualTo 10
            state.remainingMillis shouldBeEqualTo 10 * 60 * 1000L
        }

    @Test
    fun `GIVEN any state WHEN toggling auto start flags THEN values should be stored`() =
        runTest {
            // GIVEN
            repository.updateSessionState(PomodoroSessionState(autoStartBreaks = false, autoStartWork = false))

            // WHEN
            useCase.toggleAutoStartBreaks(true)
            useCase.toggleAutoStartWork(true)

            // THEN
            val state = repository.getSessionState().first()
            state.autoStartBreaks shouldBeEqualTo true
            state.autoStartWork shouldBeEqualTo true
        }

    @Test
    fun `GIVEN warning snooze timestamp WHEN updating exact alarm warning snooze THEN value stored`() =
        runTest {
            // GIVEN
            val snoozedUntil = 123456789L

            // WHEN
            useCase.updateExactAlarmWarningSnoozedUntil(snoozedUntil)

            // THEN
            val state = repository.getSessionState().first()
            state.exactAlarmWarningSnoozedUntilMillis shouldBeEqualTo snoozedUntil
        }
}
