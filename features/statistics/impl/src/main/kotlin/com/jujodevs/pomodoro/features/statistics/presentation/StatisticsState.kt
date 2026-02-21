package com.jujodevs.pomodoro.features.statistics.presentation

import com.jujodevs.pomodoro.features.statistics.domain.usecase.ActivityChartEntry

enum class StatisticsError {
    GENERIC,
    NETWORK,
}

enum class StatisticsWeekTimeMetric {
    FOCUS,
    BREAK,
    TOTAL,
}

data class StatisticsState(
    val isLoading: Boolean = true,
    val error: StatisticsError? = null,
    val selectedActivityFilter: StatisticsActivityFilter = StatisticsActivityFilter.CURRENT_WEEK,
    val selectedWeekTimeMetric: StatisticsWeekTimeMetric = StatisticsWeekTimeMetric.FOCUS,
    val totalFocusTimeFormatted: String = "00:00",
    val totalBreakTimeFormatted: String = "00:00",
    val totalWeekTimeFormatted: String = "00:00",
    val sessionsCompleted: Int = 0,
    val dayStreak: Int = 0,
    val weeklyActivity: List<ActivityChartEntry> = emptyList(),
    val monthlyActivity: List<ActivityChartEntry> = emptyList(),
    val canShare: Boolean = false,
)
