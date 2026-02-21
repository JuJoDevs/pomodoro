package com.jujodevs.pomodoro.features.statistics.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jujodevs.pomodoro.core.domain.util.DataError
import com.jujodevs.pomodoro.core.domain.util.Result
import com.jujodevs.pomodoro.features.statistics.domain.usecase.LoadStatisticsSummaryUseCase
import com.jujodevs.pomodoro.libs.usagestats.domain.usecase.ObserveUsageStatsEventsCountUseCase
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Locale

class StatisticsViewModel(
    private val loadStatisticsSummary: LoadStatisticsSummaryUseCase,
    private val observeUsageStatsEventsCount: ObserveUsageStatsEventsCountUseCase,
) : ViewModel() {
    private val _state = MutableStateFlow(StatisticsState())
    val state = _state.asStateFlow()

    private val _effects = MutableSharedFlow<StatisticsEffect>()
    val effects = _effects.asSharedFlow()

    init {
        loadSummary(showLoading = true)
        observeDataChanges()
    }

    fun onAction(action: StatisticsAction) {
        when (action) {
            is StatisticsAction.SelectActivityFilter -> selectActivityFilter(action.filter)
            StatisticsAction.CycleWeekTimeMetric -> cycleWeekTimeMetric()
            StatisticsAction.Retry -> loadSummary(showLoading = true)
            StatisticsAction.ShareProgress -> emitShareEffect()
        }
    }

    private fun selectActivityFilter(filter: StatisticsActivityFilter) {
        _state.update { state ->
            if (state.selectedActivityFilter == filter) {
                state
            } else {
                state.copy(selectedActivityFilter = filter)
            }
        }
    }

    private fun observeDataChanges() {
        viewModelScope.launch {
            observeUsageStatsEventsCount()
                .drop(INITIAL_FLOW_EMISSION_COUNT)
                .collect {
                    loadSummary(showLoading = false)
                }
        }
    }

    private fun cycleWeekTimeMetric() {
        _state.update { state ->
            state.copy(
                selectedWeekTimeMetric =
                    when (state.selectedWeekTimeMetric) {
                        StatisticsWeekTimeMetric.FOCUS -> StatisticsWeekTimeMetric.BREAK
                        StatisticsWeekTimeMetric.BREAK -> StatisticsWeekTimeMetric.TOTAL
                        StatisticsWeekTimeMetric.TOTAL -> StatisticsWeekTimeMetric.FOCUS
                    },
            )
        }
    }

    private fun loadSummary(showLoading: Boolean) {
        viewModelScope.launch {
            _state.update { state ->
                if (showLoading) {
                    state.copy(isLoading = true, error = null)
                } else {
                    state.copy(error = null)
                }
            }

            val result =
                loadStatisticsSummary(
                    referenceTimeMillis = System.currentTimeMillis(),
                )

            when (result) {
                is Result.Success -> {
                    val summary = result.data
                    _state.update {
                        it.copy(
                            isLoading = false,
                            error = null,
                            totalFocusTimeFormatted = formatFocusTime(summary.totalFocusTimeMillis),
                            totalBreakTimeFormatted = formatFocusTime(summary.totalBreakTimeMillis),
                            totalWeekTimeFormatted = formatFocusTime(summary.totalWeekTimeMillis),
                            sessionsCompleted = summary.sessionsCompleted,
                            dayStreak = summary.dayStreak,
                            weeklyActivity = summary.weeklyActivity,
                            monthlyActivity = summary.monthlyActivity,
                            canShare = summary.sessionsCompleted > 0 || summary.totalWeekTimeMillis > 0,
                        )
                    }
                }
                is Result.Failure -> {
                    _state.update {
                        if (!showLoading && it.hasLoadedSummary) {
                            it.copy(isLoading = false)
                        } else {
                            it.copy(
                                isLoading = false,
                                error = mapError(result.error),
                            )
                        }
                    }
                }
            }
        }
    }

    private fun emitShareEffect() {
        viewModelScope.launch {
            _effects.emit(StatisticsEffect.ShareProgress)
        }
    }

    private fun formatFocusTime(millis: Long): String {
        val totalMinutes = (millis / MILLIS_IN_SECOND / SECONDS_IN_MINUTE).toInt()
        val hours = totalMinutes / MINUTES_IN_HOUR
        val minutes = totalMinutes % MINUTES_IN_HOUR
        return String.format(Locale.US, "%02d:%02d", hours, minutes)
    }

    private fun mapError(error: DataError): StatisticsError =
        when (error) {
            is DataError.Local -> StatisticsError.GENERIC
            is DataError.Network -> StatisticsError.NETWORK
        }

    private val StatisticsState.hasLoadedSummary: Boolean
        get() = sessionsCompleted > 0 || totalWeekTimeFormatted != "00:00"

    private companion object {
        const val MILLIS_IN_SECOND = 1_000L
        const val SECONDS_IN_MINUTE = 60L
        const val MINUTES_IN_HOUR = 60
        const val INITIAL_FLOW_EMISSION_COUNT = 1
    }
}
