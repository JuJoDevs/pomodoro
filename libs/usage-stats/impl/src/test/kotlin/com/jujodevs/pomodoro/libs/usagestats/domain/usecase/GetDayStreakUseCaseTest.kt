package com.jujodevs.pomodoro.libs.usagestats.domain.usecase

import com.jujodevs.pomodoro.core.domain.util.DataError
import com.jujodevs.pomodoro.core.domain.util.Result
import com.jujodevs.pomodoro.libs.usagestats.domain.model.UsageStatsPeriod
import com.jujodevs.pomodoro.libs.usagestats.domain.model.UsageStatsSummary
import com.jujodevs.pomodoro.libs.usagestats.domain.repository.UsageStatsRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.amshove.kluent.shouldBeEqualTo
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class GetDayStreakUseCaseTest {
    private lateinit var repository: UsageStatsRepository
    private lateinit var useCase: GetDayStreakUseCase

    @BeforeEach
    fun setUp() {
        repository = mockk()
        useCase = GetDayStreakUseCase(repository)
    }

    @Test
    fun `GIVEN today has activity and yesterday has none WHEN invoke THEN returns streak of 1`() =
        runTest {
            val summaryWithActivity =
                UsageStatsSummary(
                    period = UsageStatsPeriod.DAILY,
                    periodStartMillis = 0L,
                    periodEndMillis = 0L,
                    totalWorkTimeMillis = 1000L,
                    totalShortBreakTimeMillis = 0L,
                    totalLongBreakTimeMillis = 0L,
                    workSessionsCompleted = 1,
                    shortBreaksCompleted = 0,
                    longBreaksCompleted = 0,
                    completedCycles = 0,
                    skippedPhases = 0,
                    stoppedSessions = 0,
                    resetCount = 0,
                    pauseCount = 0,
                )
            val summaryWithoutActivity =
                UsageStatsSummary(
                    period = UsageStatsPeriod.DAILY,
                    periodStartMillis = 0L,
                    periodEndMillis = 0L,
                    totalWorkTimeMillis = 0L,
                    totalShortBreakTimeMillis = 0L,
                    totalLongBreakTimeMillis = 0L,
                    workSessionsCompleted = 0,
                    shortBreaksCompleted = 0,
                    longBreaksCompleted = 0,
                    completedCycles = 0,
                    skippedPhases = 0,
                    stoppedSessions = 0,
                    resetCount = 0,
                    pauseCount = 0,
                )

            var callCount = 0
            coEvery { repository.getSummary(any(), any()) } answers {
                callCount++
                if (callCount == 1) {
                    Result.Success(summaryWithActivity)
                } else {
                    Result.Success(summaryWithoutActivity)
                }
            }

            val result = useCase(System.currentTimeMillis())

            (result as Result.Success).data shouldBeEqualTo 1
        }

    @Test
    fun `GIVEN repository failure WHEN invoke THEN failure is propagated`() =
        runTest {
            coEvery { repository.getSummary(any(), any()) } returns
                Result.Failure(DataError.Local.UNKNOWN)

            val result = useCase(System.currentTimeMillis())

            (result as Result.Failure).error shouldBeEqualTo DataError.Local.UNKNOWN
        }
}
