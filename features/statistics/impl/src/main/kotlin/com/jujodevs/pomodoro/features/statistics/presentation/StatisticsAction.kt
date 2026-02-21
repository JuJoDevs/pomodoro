package com.jujodevs.pomodoro.features.statistics.presentation

sealed interface StatisticsAction {
    data object Retry : StatisticsAction

    data class SelectActivityFilter(
        val filter: StatisticsActivityFilter,
    ) : StatisticsAction

    data object CycleWeekTimeMetric : StatisticsAction

    data object ShareProgress : StatisticsAction
}
