package com.jujodevs.pomodoro.libs.usagestats.domain.model

data class UsageStatsSummary(
    val period: UsageStatsPeriod,
    val periodStartMillis: Long,
    val periodEndMillis: Long,
    val totalWorkTimeMillis: Long,
    val totalShortBreakTimeMillis: Long,
    val totalLongBreakTimeMillis: Long,
    val workSessionsCompleted: Int,
    val shortBreaksCompleted: Int,
    val longBreaksCompleted: Int,
    val completedCycles: Int,
    val skippedPhases: Int,
    val stoppedSessions: Int,
    val resetCount: Int,
    val pauseCount: Int,
)
