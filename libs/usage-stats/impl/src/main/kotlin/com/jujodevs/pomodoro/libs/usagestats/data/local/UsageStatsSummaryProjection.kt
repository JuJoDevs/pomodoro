package com.jujodevs.pomodoro.libs.usagestats.data.local

internal data class UsageStatsSummaryProjection(
    val totalWorkTimeMillis: Long,
    val totalShortBreakTimeMillis: Long,
    val totalLongBreakTimeMillis: Long,
    val workSessionsCompleted: Long,
    val shortBreaksCompleted: Long,
    val longBreaksCompleted: Long,
    val completedCycles: Long,
    val skippedPhases: Long,
    val stoppedSessions: Long,
    val resetCount: Long,
    val pauseCount: Long,
)
