package com.jujodevs.pomodoro.features.statistics.presentation

import app.cash.turbine.test
import com.jujodevs.pomodoro.core.domain.util.DataError
import com.jujodevs.pomodoro.core.domain.util.Result
import com.jujodevs.pomodoro.features.statistics.domain.usecase.ActivityChartEntry
import com.jujodevs.pomodoro.features.statistics.domain.usecase.LoadStatisticsSummaryUseCase
import com.jujodevs.pomodoro.features.statistics.domain.usecase.StatisticsSummary
import com.jujodevs.pomodoro.libs.usagestats.domain.usecase.ObserveUsageStatsEventsCountUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.amshove.kluent.shouldBeEqualTo
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension

@OptIn(ExperimentalCoroutinesApi::class)
class StatisticsViewModelTest {
    @RegisterExtension
    val coroutineTestExtension =
        com.jujodevs.pomodoro.core.testing.extenion
            .CoroutineTestExtension()

    private lateinit var loadStatisticsSummary: LoadStatisticsSummaryUseCase
    private lateinit var observeUsageStatsEventsCount: ObserveUsageStatsEventsCountUseCase
    private lateinit var viewModel: StatisticsViewModel

    @BeforeEach
    fun setUp() {
        loadStatisticsSummary = mockk()
        observeUsageStatsEventsCount = mockk()
        every { observeUsageStatsEventsCount() } returns flowOf(0L)
    }

    @Test
    fun `GIVEN initialized ViewModel WHEN initial load completes THEN success state is emitted`() =
        runTest {
            coEvery { loadStatisticsSummary(any(), any()) } returns Result.Success(successSummary())

            viewModel =
                StatisticsViewModel(
                    loadStatisticsSummary = loadStatisticsSummary,
                    observeUsageStatsEventsCount = observeUsageStatsEventsCount,
                )

            viewModel.state.test {
                var state = awaitItem()
                while (state.isLoading) {
                    state = awaitItem()
                }

                state.error shouldBeEqualTo null
                state.totalFocusTimeFormatted shouldBeEqualTo "04:30"
                state.totalBreakTimeFormatted shouldBeEqualTo "01:30"
                state.totalWeekTimeFormatted shouldBeEqualTo "06:00"
                state.sessionsCompleted shouldBeEqualTo 12
                state.dayStreak shouldBeEqualTo 5
                state.weeklyActivity.size shouldBeEqualTo 7
                state.monthlyActivity.size shouldBeEqualTo 12
                state.selectedActivityFilter shouldBeEqualTo StatisticsActivityFilter.CURRENT_WEEK
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `GIVEN summary already loaded WHEN activity filter changes THEN ViewModel updates filter without reloading`() =
        runTest {
            coEvery { loadStatisticsSummary(any(), any()) } returns Result.Success(successSummary())
            viewModel =
                StatisticsViewModel(
                    loadStatisticsSummary = loadStatisticsSummary,
                    observeUsageStatsEventsCount = observeUsageStatsEventsCount,
                )

            advanceUntilIdle()
            viewModel.onAction(StatisticsAction.SelectActivityFilter(StatisticsActivityFilter.MONTHS))
            advanceUntilIdle()

            viewModel.state.value.selectedActivityFilter shouldBeEqualTo StatisticsActivityFilter.MONTHS
            coVerify(exactly = 1) { loadStatisticsSummary(any(), any()) }
        }

    @Test
    fun `GIVEN summary already loaded WHEN week time metric is cycled THEN metric changes in expected order`() =
        runTest {
            coEvery { loadStatisticsSummary(any(), any()) } returns Result.Success(successSummary())
            viewModel =
                StatisticsViewModel(
                    loadStatisticsSummary = loadStatisticsSummary,
                    observeUsageStatsEventsCount = observeUsageStatsEventsCount,
                )
            advanceUntilIdle()

            viewModel.onAction(StatisticsAction.CycleWeekTimeMetric)
            viewModel.state.value.selectedWeekTimeMetric shouldBeEqualTo StatisticsWeekTimeMetric.BREAK

            viewModel.onAction(StatisticsAction.CycleWeekTimeMetric)
            viewModel.state.value.selectedWeekTimeMetric shouldBeEqualTo StatisticsWeekTimeMetric.TOTAL

            viewModel.onAction(StatisticsAction.CycleWeekTimeMetric)
            viewModel.state.value.selectedWeekTimeMetric shouldBeEqualTo StatisticsWeekTimeMetric.FOCUS
        }

    @Test
    fun `GIVEN initial load failure WHEN Retry action is dispatched THEN ViewModel retries and emits success`() =
        runTest {
            coEvery { loadStatisticsSummary(any(), any()) } returns
                Result.Failure(DataError.Local.UNKNOWN) andThen
                Result.Success(successSummary())
            viewModel =
                StatisticsViewModel(
                    loadStatisticsSummary = loadStatisticsSummary,
                    observeUsageStatsEventsCount = observeUsageStatsEventsCount,
                )

            advanceUntilIdle()
            viewModel.onAction(StatisticsAction.Retry)
            advanceUntilIdle()

            viewModel.state.value.error shouldBeEqualTo null
            viewModel.state.value.totalFocusTimeFormatted shouldBeEqualTo "04:30"
            coVerify(exactly = 2) { loadStatisticsSummary(any(), any()) }
        }

    @Test
    fun `GIVEN share action WHEN ShareProgress is dispatched THEN share effect is emitted`() =
        runTest {
            coEvery { loadStatisticsSummary(any(), any()) } returns Result.Success(successSummary())
            viewModel =
                StatisticsViewModel(
                    loadStatisticsSummary = loadStatisticsSummary,
                    observeUsageStatsEventsCount = observeUsageStatsEventsCount,
                )
            advanceUntilIdle()

            viewModel.effects.test {
                viewModel.onAction(StatisticsAction.ShareProgress)
                awaitItem() shouldBeEqualTo StatisticsEffect.ShareProgress
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `GIVEN usage stats change flow emits new value WHEN observed THEN summary is reloaded`() =
        runTest {
            val changes = MutableSharedFlow<Long>(replay = 1)
            changes.tryEmit(0L)
            every { observeUsageStatsEventsCount() } returns changes.asSharedFlow()
            coEvery { loadStatisticsSummary(any(), any()) } returns Result.Success(successSummary())

            viewModel =
                StatisticsViewModel(
                    loadStatisticsSummary = loadStatisticsSummary,
                    observeUsageStatsEventsCount = observeUsageStatsEventsCount,
                )

            advanceUntilIdle()
            changes.emit(1L)
            advanceUntilIdle()

            coVerify(exactly = 2) { loadStatisticsSummary(any(), any()) }
        }

    private fun successSummary(): StatisticsSummary =
        StatisticsSummary(
            totalFocusTimeMillis = 16_200_000L,
            totalBreakTimeMillis = 5_400_000L,
            totalWeekTimeMillis = 21_600_000L,
            sessionsCompleted = 12,
            dayStreak = 5,
            weeklyActivity =
                listOf(
                    ActivityChartEntry("M", 0L),
                    ActivityChartEntry("T", 0L),
                    ActivityChartEntry("W", 0L),
                    ActivityChartEntry("T", 0L),
                    ActivityChartEntry("F", 0L),
                    ActivityChartEntry("S", 0L),
                    ActivityChartEntry("S", 0L),
                ),
            monthlyActivity =
                listOf(
                    ActivityChartEntry("Jan", 0L),
                    ActivityChartEntry("Feb", 0L),
                    ActivityChartEntry("Mar", 0L),
                    ActivityChartEntry("Apr", 0L),
                    ActivityChartEntry("May", 0L),
                    ActivityChartEntry("Jun", 0L),
                    ActivityChartEntry("Jul", 0L),
                    ActivityChartEntry("Aug", 0L),
                    ActivityChartEntry("Sep", 0L),
                    ActivityChartEntry("Oct", 0L),
                    ActivityChartEntry("Nov", 0L),
                    ActivityChartEntry("Dec", 0L),
                ),
        )
}
