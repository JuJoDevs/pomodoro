package com.jujodevs.pomodoro.libs.usagestats.data.repository

import com.jujodevs.pomodoro.core.domain.util.DataError
import com.jujodevs.pomodoro.core.domain.util.EmptyResult
import com.jujodevs.pomodoro.core.domain.util.Result
import com.jujodevs.pomodoro.libs.analytics.AnalyticsTracker
import com.jujodevs.pomodoro.libs.logger.Logger
import com.jujodevs.pomodoro.libs.usagestats.data.local.UsageStatsDao
import com.jujodevs.pomodoro.libs.usagestats.data.local.UsageStatsEventEntity
import com.jujodevs.pomodoro.libs.usagestats.data.mapper.UsageStatsAnalyticsEventMapper
import com.jujodevs.pomodoro.libs.usagestats.domain.model.UsageStatsEvent
import com.jujodevs.pomodoro.libs.usagestats.domain.model.UsageStatsPeriod
import com.jujodevs.pomodoro.libs.usagestats.domain.model.UsageStatsSummary
import com.jujodevs.pomodoro.libs.usagestats.domain.repository.UsageStatsRepository
import kotlinx.coroutines.flow.Flow

internal class UsageStatsRepositoryImpl(
    private val dao: UsageStatsDao,
    private val analyticsTracker: AnalyticsTracker,
    private val analyticsEventMapper: UsageStatsAnalyticsEventMapper,
    private val logger: Logger,
) : UsageStatsRepository {
    private var hasNormalizedNullDurations = false

    override suspend fun recordEvent(event: UsageStatsEvent): EmptyResult<DataError.Local> {
        val result =
            runCatching {
                normalizeNullDurationsIfNeeded()
                dao.insertEvent(event.toEntity())
                dao.deleteEventsOlderThan(event.occurredAtMillis - RETENTION_WINDOW_MILLIS)

                analyticsEventMapper
                    .toAnalyticsEvent(event)
                    ?.let(analyticsTracker::track)
            }

        return result.fold(
            onSuccess = { Result.Success(Unit) },
            onFailure = { throwable ->
                logger.e(TAG, "Failed to record usage event: ${event.type.name}", throwable)
                Result.Failure(DataError.Local.UNKNOWN)
            },
        )
    }

    override suspend fun getSummary(
        periodStartMillis: Long,
        periodEndMillis: Long,
    ): Result<UsageStatsSummary, DataError.Local> {
        val result =
            runCatching {
                val projection = dao.getSummary(periodStartMillis, periodEndMillis)

                UsageStatsSummary(
                    period = UsageStatsPeriod.DAILY,
                    periodStartMillis = periodStartMillis,
                    periodEndMillis = periodEndMillis,
                    totalWorkTimeMillis = projection.totalWorkTimeMillis,
                    totalShortBreakTimeMillis = projection.totalShortBreakTimeMillis,
                    totalLongBreakTimeMillis = projection.totalLongBreakTimeMillis,
                    workSessionsCompleted = projection.workSessionsCompleted.toInt(),
                    shortBreaksCompleted = projection.shortBreaksCompleted.toInt(),
                    longBreaksCompleted = projection.longBreaksCompleted.toInt(),
                    completedCycles = projection.completedCycles.toInt(),
                    skippedPhases = projection.skippedPhases.toInt(),
                    stoppedSessions = projection.stoppedSessions.toInt(),
                    resetCount = projection.resetCount.toInt(),
                    pauseCount = projection.pauseCount.toInt(),
                )
            }

        return result.fold(
            onSuccess = { summary -> Result.Success(summary) },
            onFailure = { throwable ->
                logger.e(TAG, "Failed to get usage stats summary", throwable)
                Result.Failure(DataError.Local.UNKNOWN)
            },
        )
    }

    override fun observeEventsCount(): Flow<Long> = dao.observeEventsCount()

    private suspend fun normalizeNullDurationsIfNeeded() {
        if (hasNormalizedNullDurations) return
        dao.normalizeNullDurations()
        hasNormalizedNullDurations = true
    }

    private fun UsageStatsEvent.toEntity(): UsageStatsEventEntity =
        UsageStatsEventEntity(
            eventType = type.name,
            phase = phase?.name,
            occurredAtMillis = occurredAtMillis,
            durationMillis = durationMillis ?: ZERO_DURATION_MILLIS,
            metadata = metadata.toStorageValue(),
        )

    private fun Map<String, String>.toStorageValue(): String {
        if (isEmpty()) return ""
        return entries.joinToString(separator = "&") { (key, value) -> "$key=$value" }
    }

    private companion object {
        const val TAG = "UsageStats"
        const val RETENTION_WINDOW_MILLIS = 365L * 24L * 60L * 60L * 1000L
        const val ZERO_DURATION_MILLIS = 0L
    }
}
