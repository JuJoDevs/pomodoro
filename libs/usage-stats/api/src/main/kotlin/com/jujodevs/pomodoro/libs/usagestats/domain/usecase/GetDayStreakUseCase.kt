package com.jujodevs.pomodoro.libs.usagestats.domain.usecase

import com.jujodevs.pomodoro.core.domain.util.DataError
import com.jujodevs.pomodoro.core.domain.util.Result
import com.jujodevs.pomodoro.libs.usagestats.domain.repository.UsageStatsRepository
import java.time.Instant
import java.time.ZoneId

/**
 * Computes the current day streak: consecutive days (including today) with at least one work session.
 */
class GetDayStreakUseCase(
    private val repository: UsageStatsRepository,
) {
    suspend operator fun invoke(
        referenceTimeMillis: Long,
        zoneId: ZoneId = ZoneId.systemDefault(),
    ): Result<Int, DataError.Local> {
        val referenceDate = Instant.ofEpochMilli(referenceTimeMillis).atZone(zoneId).toLocalDate()
        var streak = 0
        var currentDate = referenceDate

        while (true) {
            val startMillis = currentDate.atStartOfDay(zoneId).toInstant().toEpochMilli()
            val endMillis =
                currentDate
                    .plusDays(1)
                    .atStartOfDay(zoneId)
                    .toInstant()
                    .toEpochMilli()

            val result = repository.getSummary(periodStartMillis = startMillis, periodEndMillis = endMillis)
            val summary =
                when (result) {
                    is Result.Success -> result.data
                    is Result.Failure -> return result
                }

            val hasActivity = summary.totalWorkTimeMillis > 0 || summary.workSessionsCompleted > 0
            if (!hasActivity) break

            streak++
            currentDate = currentDate.minusDays(1)
        }

        return Result.Success(streak)
    }
}
