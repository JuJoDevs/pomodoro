package com.jujodevs.pomodoro.features.statistics.presentation

import app.cash.turbine.testIn
import app.cash.turbine.turbineScope
import com.jujodevs.pomodoro.core.domain.util.DataError
import com.jujodevs.pomodoro.core.domain.util.EmptyResult
import com.jujodevs.pomodoro.core.domain.util.Result
import com.jujodevs.pomodoro.core.testing.extenion.CoroutineTestExtension
import com.jujodevs.pomodoro.features.statistics.domain.usecase.LoadStatisticsSummaryUseCase
import com.jujodevs.pomodoro.libs.usagestats.domain.model.UsageStatsEvent
import com.jujodevs.pomodoro.libs.usagestats.domain.model.UsageStatsPeriod
import com.jujodevs.pomodoro.libs.usagestats.domain.model.UsageStatsSummary
import com.jujodevs.pomodoro.libs.usagestats.domain.repository.UsageStatsRepository
import com.jujodevs.pomodoro.libs.usagestats.domain.usecase.GetDayStreakUseCase
import com.jujodevs.pomodoro.libs.usagestats.domain.usecase.GetUsageStatsSummaryUseCase
import com.jujodevs.pomodoro.libs.usagestats.domain.usecase.ObserveUsageStatsEventsCountUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.test.runTest
import org.amshove.kluent.shouldBeEqualTo
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension
import java.time.Instant
import java.time.LocalDate
import java.time.YearMonth
import java.time.ZoneId
import java.time.temporal.ChronoUnit

@OptIn(ExperimentalCoroutinesApi::class)
class StatisticsViewModelTest {
    @RegisterExtension
    val coroutineTestExtension = CoroutineTestExtension()

    private lateinit var repository: FakeUsageStatsRepository
    private lateinit var loadStatisticsSummary: LoadStatisticsSummaryUseCase
    private lateinit var observeUsageStatsEventsCount: ObserveUsageStatsEventsCountUseCase
    private lateinit var viewModel: StatisticsViewModel

    @BeforeEach
    fun setUp() {
        repository = FakeUsageStatsRepository()
        loadStatisticsSummary =
            LoadStatisticsSummaryUseCase(
                getUsageStatsSummary = GetUsageStatsSummaryUseCase(repository),
                getDayStreak = GetDayStreakUseCase(repository),
            )
        observeUsageStatsEventsCount = ObserveUsageStatsEventsCountUseCase(repository)
    }

    @Test
    fun `GIVEN initialized ViewModel WHEN initial load completes THEN success state is emitted`() =
        runTest {
            repository.enqueueWeeklyResult(Result.Success(successWeeklySummary()))
            repository.setConsecutiveDailyActivity(days = 5)
            viewModel = createViewModel()

            turbineScope {
                val state = viewModel.state.testIn(this)

                var loadedState = state.awaitItem()
                while (loadedState.isLoading) {
                    loadedState = state.awaitItem()
                }

                loadedState.error shouldBeEqualTo null
                loadedState.totalFocusTimeFormatted shouldBeEqualTo "04:30"
                loadedState.totalBreakTimeFormatted shouldBeEqualTo "01:30"
                loadedState.totalWeekTimeFormatted shouldBeEqualTo "06:00"
                loadedState.sessionsCompleted shouldBeEqualTo 12
                loadedState.dayStreak shouldBeEqualTo 5
                loadedState.weeklyActivity.size shouldBeEqualTo 7
                loadedState.monthlyActivity.size shouldBeEqualTo 12
                loadedState.selectedActivityFilter shouldBeEqualTo StatisticsActivityFilter.CURRENT_WEEK
                state.cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `GIVEN summary already loaded WHEN activity filter changes THEN ViewModel updates filter without reloading`() =
        runTest {
            repository.enqueueWeeklyResult(Result.Success(successWeeklySummary()))
            repository.setConsecutiveDailyActivity(days = 5)
            viewModel = createViewModel()

            turbineScope {
                val state = viewModel.state.testIn(this)

                var loadedState = state.awaitItem()
                while (loadedState.isLoading) {
                    loadedState = state.awaitItem()
                }

                viewModel.onAction(StatisticsAction.SelectActivityFilter(StatisticsActivityFilter.MONTHS))

                val updatedState = state.awaitItem()
                updatedState.selectedActivityFilter shouldBeEqualTo StatisticsActivityFilter.MONTHS
                repository.weeklySummaryRequestCount shouldBeEqualTo 1
                state.cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `GIVEN summary already loaded WHEN week time metric is cycled THEN metric changes in expected order`() =
        runTest {
            repository.enqueueWeeklyResult(Result.Success(successWeeklySummary()))
            repository.setConsecutiveDailyActivity(days = 5)
            viewModel = createViewModel()

            turbineScope {
                val state = viewModel.state.testIn(this)

                var loadedState = state.awaitItem()
                while (loadedState.isLoading) {
                    loadedState = state.awaitItem()
                }

                viewModel.onAction(StatisticsAction.CycleWeekTimeMetric)
                state.awaitItem().selectedWeekTimeMetric shouldBeEqualTo StatisticsWeekTimeMetric.BREAK

                viewModel.onAction(StatisticsAction.CycleWeekTimeMetric)
                state.awaitItem().selectedWeekTimeMetric shouldBeEqualTo StatisticsWeekTimeMetric.TOTAL

                viewModel.onAction(StatisticsAction.CycleWeekTimeMetric)
                state.awaitItem().selectedWeekTimeMetric shouldBeEqualTo StatisticsWeekTimeMetric.FOCUS
                state.cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `GIVEN initial load failure WHEN Retry action is dispatched THEN ViewModel retries and emits success`() =
        runTest {
            repository.enqueueWeeklyResult(Result.Failure(DataError.Local.UNKNOWN))
            repository.enqueueWeeklyResult(Result.Success(successWeeklySummary()))
            repository.setConsecutiveDailyActivity(days = 5)
            viewModel = createViewModel()

            turbineScope {
                val state = viewModel.state.testIn(this)

                var failedState = state.awaitItem()
                while (failedState.isLoading) {
                    failedState = state.awaitItem()
                }
                failedState.error shouldBeEqualTo StatisticsError.GENERIC

                viewModel.onAction(StatisticsAction.Retry)

                var retriedState = state.awaitItem()
                while (retriedState.isLoading) {
                    retriedState = state.awaitItem()
                }
                retriedState.error shouldBeEqualTo null
                retriedState.totalFocusTimeFormatted shouldBeEqualTo "04:30"
                repository.weeklySummaryRequestCount shouldBeEqualTo 2
                state.cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `GIVEN share action WHEN ShareProgress is dispatched THEN share effect is emitted`() =
        runTest {
            repository.enqueueWeeklyResult(Result.Success(successWeeklySummary()))
            repository.setConsecutiveDailyActivity(days = 5)
            viewModel = createViewModel()

            turbineScope {
                val state = viewModel.state.testIn(this)
                val effects = viewModel.effects.testIn(this)

                var loadedState = state.awaitItem()
                while (loadedState.isLoading) {
                    loadedState = state.awaitItem()
                }
                viewModel.onAction(StatisticsAction.ShareProgress)

                effects.awaitItem() shouldBeEqualTo StatisticsEffect.ShareProgress
                effects.cancelAndIgnoreRemainingEvents()
                state.cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `GIVEN usage stats change flow emits new value WHEN observed THEN summary is reloaded`() =
        runTest {
            repository.enqueueWeeklyResult(Result.Success(successWeeklySummary()))
            repository.enqueueWeeklyResult(
                Result.Success(
                    successWeeklySummary(
                        totalWorkTimeMillis = 18_000_000L,
                        totalShortBreakTimeMillis = 2_700_000L,
                        totalLongBreakTimeMillis = 900_000L,
                    ),
                ),
            )
            repository.setConsecutiveDailyActivity(days = 5)
            viewModel = createViewModel()

            turbineScope {
                val state = viewModel.state.testIn(this)

                var initialState = state.awaitItem()
                while (initialState.isLoading) {
                    initialState = state.awaitItem()
                }
                initialState.totalFocusTimeFormatted shouldBeEqualTo "04:30"

                repository.emitEventsCount(value = 1L)

                var refreshedState = state.awaitItem()
                while (refreshedState.isLoading) {
                    refreshedState = state.awaitItem()
                }
                refreshedState.totalFocusTimeFormatted shouldBeEqualTo "05:00"
                repository.weeklySummaryRequestCount shouldBeEqualTo 2
                state.cancelAndIgnoreRemainingEvents()
            }
        }

    private fun createViewModel(): StatisticsViewModel =
        StatisticsViewModel(
            loadStatisticsSummary = loadStatisticsSummary,
            observeUsageStatsEventsCount = observeUsageStatsEventsCount,
        )

    private fun successWeeklySummary(
        totalWorkTimeMillis: Long = 16_200_000L,
        totalShortBreakTimeMillis: Long = 3_600_000L,
        totalLongBreakTimeMillis: Long = 1_800_000L,
    ): UsageStatsSummary =
        UsageStatsSummary(
            period = UsageStatsPeriod.WEEKLY,
            periodStartMillis = 0L,
            periodEndMillis = 0L,
            totalWorkTimeMillis = totalWorkTimeMillis,
            totalShortBreakTimeMillis = totalShortBreakTimeMillis,
            totalLongBreakTimeMillis = totalLongBreakTimeMillis,
            workSessionsCompleted = 12,
            shortBreaksCompleted = 0,
            longBreaksCompleted = 0,
            completedCycles = 0,
            skippedPhases = 0,
            stoppedSessions = 0,
            resetCount = 0,
            pauseCount = 0,
        )
}

private class FakeUsageStatsRepository : UsageStatsRepository {
    private val eventsCountFlow = MutableStateFlow(0L)
    private val weeklyResultsQueue = ArrayDeque<Result<UsageStatsSummary, DataError.Local>>()
    private val dailyResultsByDate = mutableMapOf<LocalDate, Result<UsageStatsSummary, DataError.Local>>()
    private val monthlyResultsByMonth = mutableMapOf<YearMonth, Result<UsageStatsSummary, DataError.Local>>()
    private val zoneId = ZoneId.systemDefault()

    var weeklySummaryRequestCount: Int = 0
        private set

    fun enqueueWeeklyResult(result: Result<UsageStatsSummary, DataError.Local>) {
        weeklyResultsQueue.addLast(result)
    }

    fun setConsecutiveDailyActivity(
        days: Int,
        referenceDate: LocalDate = LocalDate.now(zoneId),
    ) {
        repeat(days) { dayOffset ->
            val date = referenceDate.minusDays(dayOffset.toLong())
            dailyResultsByDate[date] = Result.Success(summaryWithActivity(period = UsageStatsPeriod.DAILY))
        }
    }

    fun emitEventsCount(value: Long) {
        eventsCountFlow.value = value
    }

    override suspend fun recordEvent(event: UsageStatsEvent): EmptyResult<DataError.Local> = Result.Success(Unit)

    override suspend fun getSummary(
        periodStartMillis: Long,
        periodEndMillis: Long,
    ): Result<UsageStatsSummary, DataError.Local> {
        val startDate = Instant.ofEpochMilli(periodStartMillis).atZone(zoneId).toLocalDate()
        val endDate = Instant.ofEpochMilli(periodEndMillis).atZone(zoneId).toLocalDate()
        val daysBetween = ChronoUnit.DAYS.between(startDate, endDate).toInt()

        return when (daysBetween) {
            1 ->
                adaptPeriodRange(
                    result =
                        dailyResultsByDate[startDate]
                            ?: Result.Success(emptySummary(period = UsageStatsPeriod.DAILY)),
                    period = UsageStatsPeriod.DAILY,
                    periodStartMillis = periodStartMillis,
                    periodEndMillis = periodEndMillis,
                )
            7 -> {
                weeklySummaryRequestCount += 1
                val weeklyResult =
                    if (weeklyResultsQueue.isNotEmpty()) {
                        weeklyResultsQueue.removeFirst()
                    } else {
                        Result.Success(emptySummary(period = UsageStatsPeriod.WEEKLY))
                    }

                adaptPeriodRange(
                    result = weeklyResult,
                    period = UsageStatsPeriod.WEEKLY,
                    periodStartMillis = periodStartMillis,
                    periodEndMillis = periodEndMillis,
                )
            }
            else ->
                adaptPeriodRange(
                    result =
                        monthlyResultsByMonth[YearMonth.from(startDate)]
                            ?: Result.Success(emptySummary(period = UsageStatsPeriod.MONTHLY)),
                    period = UsageStatsPeriod.MONTHLY,
                    periodStartMillis = periodStartMillis,
                    periodEndMillis = periodEndMillis,
                )
        }
    }

    override fun observeEventsCount(): Flow<Long> = eventsCountFlow.asStateFlow()

    private fun adaptPeriodRange(
        result: Result<UsageStatsSummary, DataError.Local>,
        period: UsageStatsPeriod,
        periodStartMillis: Long,
        periodEndMillis: Long,
    ): Result<UsageStatsSummary, DataError.Local> =
        when (result) {
            is Result.Success ->
                Result.Success(
                    result.data.copy(
                        period = period,
                        periodStartMillis = periodStartMillis,
                        periodEndMillis = periodEndMillis,
                    ),
                )
            is Result.Failure -> result
        }

    private fun emptySummary(period: UsageStatsPeriod): UsageStatsSummary =
        UsageStatsSummary(
            period = period,
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

    private fun summaryWithActivity(period: UsageStatsPeriod): UsageStatsSummary =
        emptySummary(period = period).copy(
            totalWorkTimeMillis = 1L,
            workSessionsCompleted = 1,
        )
}
