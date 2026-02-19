package com.jujodevs.pomodoro.libs.usagestats.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
internal interface UsageStatsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEvent(event: UsageStatsEventEntity)

    @Query(
        """
        DELETE FROM usage_stats_events
        WHERE occurredAtMillis < :cutoffMillis
        """,
    )
    suspend fun deleteEventsOlderThan(cutoffMillis: Long)

    @Query(
        """
        SELECT
            COALESCE(SUM(CASE
                WHEN eventType = 'PHASE_TIME_RECORDED' AND phase = 'WORK' THEN durationMillis
                ELSE 0
            END), 0) AS totalWorkTimeMillis,
            COALESCE(SUM(CASE
                WHEN eventType = 'PHASE_TIME_RECORDED' AND phase = 'SHORT_BREAK' THEN durationMillis
                ELSE 0
            END), 0) AS totalShortBreakTimeMillis,
            COALESCE(SUM(CASE
                WHEN eventType = 'PHASE_TIME_RECORDED' AND phase = 'LONG_BREAK' THEN durationMillis
                ELSE 0
            END), 0) AS totalLongBreakTimeMillis,
            COALESCE(SUM(CASE
                WHEN eventType = 'PHASE_COMPLETED' AND phase = 'WORK' THEN 1
                ELSE 0
            END), 0) AS workSessionsCompleted,
            COALESCE(SUM(CASE
                WHEN eventType = 'PHASE_COMPLETED' AND phase = 'SHORT_BREAK' THEN 1
                ELSE 0
            END), 0) AS shortBreaksCompleted,
            COALESCE(SUM(CASE
                WHEN eventType = 'PHASE_COMPLETED' AND phase = 'LONG_BREAK' THEN 1
                ELSE 0
            END), 0) AS longBreaksCompleted,
            COALESCE(SUM(CASE
                WHEN eventType = 'CYCLE_COMPLETED' THEN 1
                ELSE 0
            END), 0) AS completedCycles,
            COALESCE(SUM(CASE
                WHEN eventType = 'PHASE_SKIPPED' THEN 1
                ELSE 0
            END), 0) AS skippedPhases,
            COALESCE(SUM(CASE
                WHEN eventType = 'SESSION_STOPPED' THEN 1
                ELSE 0
            END), 0) AS stoppedSessions,
            COALESCE(SUM(CASE
                WHEN eventType = 'SESSION_RESET' THEN 1
                ELSE 0
            END), 0) AS resetCount,
            COALESCE(SUM(CASE
                WHEN eventType = 'PHASE_PAUSED' THEN 1
                ELSE 0
            END), 0) AS pauseCount
        FROM usage_stats_events
        WHERE occurredAtMillis >= :periodStartMillis
            AND occurredAtMillis < :periodEndMillis
        """,
    )
    suspend fun getSummary(
        periodStartMillis: Long,
        periodEndMillis: Long,
    ): UsageStatsSummaryProjection
}
