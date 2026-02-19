package com.jujodevs.pomodoro.libs.usagestats.data.mapper

import com.jujodevs.pomodoro.libs.analytics.AnalyticsEvent
import com.jujodevs.pomodoro.libs.analytics.CustomAnalyticsEvent
import com.jujodevs.pomodoro.libs.usagestats.domain.model.UsageStatsEvent
import com.jujodevs.pomodoro.libs.usagestats.domain.model.UsageStatsEventType

internal class UsageStatsAnalyticsEventMapper {
    fun toAnalyticsEvent(event: UsageStatsEvent): AnalyticsEvent? {
        val eventName =
            when (event.type) {
                UsageStatsEventType.PHASE_STARTED -> "timer_phase_started"
                UsageStatsEventType.PHASE_COMPLETED -> "timer_phase_completed"
                UsageStatsEventType.CYCLE_COMPLETED -> "timer_cycle_completed"
                UsageStatsEventType.PHASE_SKIPPED -> "timer_phase_skipped"
                UsageStatsEventType.SESSION_STOPPED -> "timer_session_stopped"
                UsageStatsEventType.SESSION_RESET -> "timer_session_reset"
                UsageStatsEventType.PHASE_PAUSED,
                UsageStatsEventType.PHASE_RESUMED,
                UsageStatsEventType.PHASE_TIME_RECORDED,
                -> return null
            }

        val params =
            buildMap<String, Any> {
                event.phase?.let { put("phase", it.name.lowercase()) }
                event.durationMillis?.let { put("duration_ms", it) }
                event.metadata.forEach { (key, value) -> put(key, value) }
            }

        return CustomAnalyticsEvent(
            name = eventName,
            parameters = params,
        )
    }
}
