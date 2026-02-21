package com.jujodevs.pomodoro.features.statistics.domain.usecase

import com.jujodevs.pomodoro.core.domain.util.DataError
import com.jujodevs.pomodoro.core.domain.util.Result
import com.jujodevs.pomodoro.libs.usagestats.domain.model.UsageStatsPeriod
import com.jujodevs.pomodoro.libs.usagestats.domain.usecase.GetDayStreakUseCase
import com.jujodevs.pomodoro.libs.usagestats.domain.usecase.GetUsageStatsSummaryUseCase
import java.time.DayOfWeek
import java.time.Instant
import java.time.Month
import java.time.ZoneId
import java.time.temporal.TemporalAdjusters
import java.util.Locale

data class StatisticsSummary(
    val totalFocusTimeMillis: Long,
    val totalBreakTimeMillis: Long,
    val totalWeekTimeMillis: Long,
    val sessionsCompleted: Int,
    val dayStreak: Int,
    val weeklyActivity: List<ActivityChartEntry>,
    val monthlyActivity: List<ActivityChartEntry>,
)

data class ActivityChartEntry(
    val label: String,
    val focusTimeMillis: Long,
    val breakTimeMillis: Long = 0L,
)

class LoadStatisticsSummaryUseCase(
    private val getUsageStatsSummary: GetUsageStatsSummaryUseCase,
    private val getDayStreak: GetDayStreakUseCase,
) {
    suspend operator fun invoke(
        referenceTimeMillis: Long,
        zoneId: ZoneId = ZoneId.systemDefault(),
    ): Result<StatisticsSummary, DataError.Local> =
        when (val weeklySummaryResult = loadWeeklySummary(referenceTimeMillis, zoneId)) {
            is Result.Failure -> weeklySummaryResult
            is Result.Success -> {
                when (val streakResult = getDayStreak(referenceTimeMillis, zoneId)) {
                    is Result.Failure -> streakResult
                    is Result.Success ->
                        Result.Success(
                            StatisticsSummary(
                                totalFocusTimeMillis = weeklySummaryResult.data.totalWorkTimeMillis,
                                totalBreakTimeMillis =
                                    weeklySummaryResult.data.totalShortBreakTimeMillis +
                                        weeklySummaryResult.data.totalLongBreakTimeMillis,
                                totalWeekTimeMillis =
                                    weeklySummaryResult.data.totalWorkTimeMillis +
                                        weeklySummaryResult.data.totalShortBreakTimeMillis +
                                        weeklySummaryResult.data.totalLongBreakTimeMillis,
                                sessionsCompleted = weeklySummaryResult.data.workSessionsCompleted,
                                dayStreak = streakResult.data,
                                weeklyActivity = loadWeeklyActivity(referenceTimeMillis, zoneId),
                                monthlyActivity = loadMonthlyActivity(referenceTimeMillis, zoneId),
                            ),
                        )
                }
            }
        }

    private suspend fun loadWeeklySummary(
        referenceTimeMillis: Long,
        zoneId: ZoneId,
    ) = getUsageStatsSummary(
        period = UsageStatsPeriod.WEEKLY,
        referenceTimeMillis = referenceTimeMillis,
        zoneId = zoneId,
    )

    private suspend fun loadMonthlySummary(
        referenceTimeMillis: Long,
        zoneId: ZoneId,
    ) = getUsageStatsSummary(
        period = UsageStatsPeriod.MONTHLY,
        referenceTimeMillis = referenceTimeMillis,
        zoneId = zoneId,
    )

    private suspend fun loadDailySummary(
        referenceTimeMillis: Long,
        zoneId: ZoneId,
    ) = getUsageStatsSummary(
        period = UsageStatsPeriod.DAILY,
        referenceTimeMillis = referenceTimeMillis,
        zoneId = zoneId,
    )

    private suspend fun toActivityEntry(
        label: String,
        result: Result<com.jujodevs.pomodoro.libs.usagestats.domain.model.UsageStatsSummary, DataError.Local>,
    ): ActivityChartEntry =
        when (result) {
            is Result.Success ->
                ActivityChartEntry(
                    label = label,
                    focusTimeMillis = result.data.totalWorkTimeMillis,
                    breakTimeMillis = result.data.totalShortBreakTimeMillis + result.data.totalLongBreakTimeMillis,
                )
            is Result.Failure -> ActivityChartEntry(label = label, focusTimeMillis = 0L, breakTimeMillis = 0L)
        }

    private suspend fun loadMonthlyActivity(
        referenceTimeMillis: Long,
        zoneId: ZoneId,
    ): List<ActivityChartEntry> {
        val referenceDate = Instant.ofEpochMilli(referenceTimeMillis).atZone(zoneId).toLocalDate()
        val currentMonth = referenceDate.withDayOfMonth(FIRST_DAY_OF_MONTH)
        val firstMonth = currentMonth.minusMonths((MONTHS_IN_YEAR - 1).toLong())
        val locale = Locale.getDefault()

        return (0 until MONTHS_IN_YEAR).map { monthOffset ->
            val monthDate = firstMonth.plusMonths(monthOffset.toLong())
            val monthStartMillis = monthDate.atStartOfDay(zoneId).toInstant().toEpochMilli()
            val summaryResult = loadMonthlySummary(monthStartMillis, zoneId)
            toActivityEntry(
                label = monthDate.month.toLabel(locale),
                result = summaryResult,
            )
        }
    }

    private suspend fun loadWeeklyActivity(
        referenceTimeMillis: Long,
        zoneId: ZoneId,
    ): List<ActivityChartEntry> {
        val referenceDate = Instant.ofEpochMilli(referenceTimeMillis).atZone(zoneId).toLocalDate()
        val weekStart = referenceDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
        val dayLabels = listOf("M", "T", "W", "T", "F", "S", "S")

        return (0 until DAYS_IN_WEEK).map { dayOffset ->
            val dayDate = weekStart.plusDays(dayOffset.toLong())
            val dayStartMillis = dayDate.atStartOfDay(zoneId).toInstant().toEpochMilli()
            val summaryResult = loadDailySummary(dayStartMillis, zoneId)
            toActivityEntry(
                label = dayLabels[dayOffset],
                result = summaryResult,
            )
        }
    }

    private fun Month.toLabel(locale: Locale): String =
        name.take(MONTH_LABEL_LENGTH).lowercase(locale).replaceFirstChar { first ->
            first.titlecase(locale)
        }

    private companion object {
        const val DAYS_IN_WEEK = 7
        const val MONTHS_IN_YEAR = 12
        const val MONTH_LABEL_LENGTH = 3
        const val FIRST_DAY_OF_MONTH = 1
    }
}
