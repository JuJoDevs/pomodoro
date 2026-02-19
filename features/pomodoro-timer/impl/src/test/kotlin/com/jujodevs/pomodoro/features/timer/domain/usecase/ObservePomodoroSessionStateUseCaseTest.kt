package com.jujodevs.pomodoro.features.timer.domain.usecase

import app.cash.turbine.test
import com.jujodevs.pomodoro.features.timer.domain.model.PomodoroSessionState
import com.jujodevs.pomodoro.features.timer.domain.model.PomodoroStatus
import com.jujodevs.pomodoro.features.timer.fakes.FakePomodoroRepository
import kotlinx.coroutines.test.runTest
import org.amshove.kluent.shouldBeEqualTo
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class ObservePomodoroSessionStateUseCaseTest {
    private lateinit var repository: FakePomodoroRepository
    private lateinit var useCase: ObservePomodoroSessionStateUseCase

    @BeforeEach
    fun setUp() {
        repository = FakePomodoroRepository()
        useCase = ObservePomodoroSessionStateUseCase(repository)
    }

    @Test
    fun `GIVEN repository emits states WHEN observing THEN use case should emit same states`() =
        runTest {
            // GIVEN
            val initialState = PomodoroSessionState(status = PomodoroStatus.IDLE)
            val secondState = PomodoroSessionState(status = PomodoroStatus.RUNNING)
            repository.updateSessionState(initialState)

            // WHEN & THEN
            useCase().test {
                awaitItem() shouldBeEqualTo initialState

                repository.updateSessionState(secondState)
                awaitItem() shouldBeEqualTo secondState
            }
        }
}
