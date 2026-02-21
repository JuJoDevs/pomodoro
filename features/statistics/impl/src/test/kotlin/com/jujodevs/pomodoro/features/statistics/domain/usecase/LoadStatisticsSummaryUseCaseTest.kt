package com.jujodevs.pomodoro.features.statistics.domain.usecase

import com.jujodevs.pomodoro.core.domain.util.DataError
import com.jujodevs.pomodoro.core.domain.util.Result
import com.jujodevs.pomodoro.libs.usagestats.domain.model.UsageStatsPeriod
import com.jujodevs.pomodoro.libs.usagestats.domain.model.UsageStatsSummary
import com.jujodevs.pomodoro.libs.usagestats.domain.usecase.GetDayStreakUseCase
import com.jujodevs.pomodoro.libs.usagestats.domain.usecase.GetUsageStatsSummaryUseCase
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.amshove.kluent.shouldBeEqualTo
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class LoadStatisticsSummaryUseCaseTest {
    private lateinit var getUsageStatsSummary: GetUsageStatsSummaryUseCase
    private lateinit var getDayStreak: GetDayStreakUseCase
    private lateinit var useCase: LoadStatisticsSummaryUseCase

    @BeforeEach
    fun setUp() {
        getUsageStatsSummary = mockk()
        getDayStreak = mockk()
        useCase = LoadStatisticsSummaryUseCase(getUsageStatsSummary, getDayStreak)
    }

    @Test
    fun `GIVEN successful summary and streak WHEN load is invoked THEN mapped statistics are returned`() =
        runTest {
            val referenceTime = System.currentTimeMillis()
            val summary =
                UsageStatsSummary(
                    period = UsageStatsPeriod.WEEKLY,
                    periodStartMillis = referenceTime,
                    periodEndMillis = referenceTime + 86400000,
                    totalWorkTimeMillis = 270000L,
                    totalShortBreakTimeMillis = 30000L,
                    totalLongBreakTimeMillis = 60000L,
                    workSessionsCompleted = 12,
                    shortBreaksCompleted = 0,
                    longBreaksCompleted = 0,
                    completedCycles = 0,
                    skippedPhases = 0,
                    stoppedSessions = 0,
                    resetCount = 0,
                    pauseCount = 0,
                )

            coEvery { getUsageStatsSummary(any(), any(), any()) } returns Result.Success(summary)
            coEvery { getDayStreak(any(), any()) } returns Result.Success(5)

            val result = useCase(referenceTime)

            val successResult = result as Result.Success
            successResult.data.totalFocusTimeMillis shouldBeEqualTo 270000L
            successResult.data.totalBreakTimeMillis shouldBeEqualTo 90000L
            successResult.data.totalWeekTimeMillis shouldBeEqualTo 360000L
            successResult.data.sessionsCompleted shouldBeEqualTo 12
            successResult.data.dayStreak shouldBeEqualTo 5
            successResult.data.weeklyActivity.size shouldBeEqualTo 7
            successResult.data.weeklyActivity.map { it.label } shouldBeEqualTo
                listOf("M", "T", "W", "T", "F", "S", "S")
            successResult.data.monthlyActivity.size shouldBeEqualTo 12
        }

    @Test
    fun `GIVEN usage summary failure WHEN load is invoked THEN failure is propagated`() =
        runTest {
            coEvery { getUsageStatsSummary(any(), any(), any()) } returns Result.Failure(DataError.Local.UNKNOWN)
            coEvery { getDayStreak(any(), any()) } returns Result.Success(0)

            val result = useCase(System.currentTimeMillis())

            (result as Result.Failure).error shouldBeEqualTo DataError.Local.UNKNOWN
        }

    @Test
    fun `GIVEN day streak failure WHEN load is invoked THEN failure is propagated`() =
        runTest {
            val summary =
                UsageStatsSummary(
                    period = UsageStatsPeriod.WEEKLY,
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
            coEvery { getUsageStatsSummary(any(), any(), any()) } returns Result.Success(summary)
            coEvery { getDayStreak(any(), any()) } returns Result.Failure(DataError.Local.UNKNOWN)

            val result = useCase(System.currentTimeMillis())

            (result as Result.Failure).error shouldBeEqualTo DataError.Local.UNKNOWN
        }
}
