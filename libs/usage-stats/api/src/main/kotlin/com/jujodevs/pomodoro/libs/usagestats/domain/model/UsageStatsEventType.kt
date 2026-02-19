package com.jujodevs.pomodoro.libs.usagestats.domain.model

enum class UsageStatsEventType {
    PHASE_STARTED,
    PHASE_PAUSED,
    PHASE_RESUMED,
    PHASE_COMPLETED,
    PHASE_SKIPPED,
    SESSION_STOPPED,
    SESSION_RESET,
    CYCLE_COMPLETED,
    PHASE_TIME_RECORDED,
}
