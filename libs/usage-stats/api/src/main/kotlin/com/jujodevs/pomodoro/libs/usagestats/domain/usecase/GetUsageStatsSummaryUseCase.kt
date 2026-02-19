package com.jujodevs.pomodoro.libs.usagestats.domain.usecase

import com.jujodevs.pomodoro.core.domain.util.DataError
import com.jujodevs.pomodoro.core.domain.util.Result
import com.jujodevs.pomodoro.core.domain.util.map
import com.jujodevs.pomodoro.libs.usagestats.domain.model.UsageStatsPeriod
import com.jujodevs.pomodoro.libs.usagestats.domain.model.UsageStatsSummary
import com.jujodevs.pomodoro.libs.usagestats.domain.repository.UsageStatsRepository
import java.time.DayOfWeek
import java.time.Instant
import java.time.ZoneId
import java.time.temporal.TemporalAdjusters

class GetUsageStatsSummaryUseCase(
    private val repository: UsageStatsRepository,
) {
    suspend operator fun invoke(
        period: UsageStatsPeriod,
        referenceTimeMillis: Long,
        zoneId: ZoneId = ZoneId.systemDefault(),
    ): Result<UsageStatsSummary, DataError.Local> {
        val (startMillis, endMillis) = resolvePeriodRange(period, referenceTimeMillis, zoneId)
        return repository
            .getSummary(
                periodStartMillis = startMillis,
                periodEndMillis = endMillis,
            ).map { summary ->
                summary.copy(
                    period = period,
                    periodStartMillis = startMillis,
                    periodEndMillis = endMillis,
                )
            }
    }

    private fun resolvePeriodRange(
        period: UsageStatsPeriod,
        referenceTimeMillis: Long,
        zoneId: ZoneId,
    ): Pair<Long, Long> {
        val referenceDate = Instant.ofEpochMilli(referenceTimeMillis).atZone(zoneId).toLocalDate()

        val periodStart =
            when (period) {
                UsageStatsPeriod.DAILY -> referenceDate
                UsageStatsPeriod.WEEKLY -> referenceDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
                UsageStatsPeriod.MONTHLY -> referenceDate.withDayOfMonth(1)
            }

        val periodEndExclusive =
            when (period) {
                UsageStatsPeriod.DAILY -> periodStart.plusDays(1)
                UsageStatsPeriod.WEEKLY -> periodStart.plusWeeks(1)
                UsageStatsPeriod.MONTHLY -> periodStart.plusMonths(1)
            }

        val startMillis = periodStart.atStartOfDay(zoneId).toInstant().toEpochMilli()
        val endMillis = periodEndExclusive.atStartOfDay(zoneId).toInstant().toEpochMilli()
        return startMillis to endMillis
    }
}
