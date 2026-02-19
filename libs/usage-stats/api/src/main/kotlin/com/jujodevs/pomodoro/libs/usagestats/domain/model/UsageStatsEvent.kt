package com.jujodevs.pomodoro.libs.usagestats.domain.model

data class UsageStatsEvent(
    val type: UsageStatsEventType,
    val phase: UsageStatsPhase? = null,
    val occurredAtMillis: Long,
    val durationMillis: Long? = null,
    val metadata: Map<String, String> = emptyMap(),
)
