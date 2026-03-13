package com.jujodevs.pomodoro.features.statistics.presentation

import android.content.Context
import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jujodevs.pomodoro.core.resources.R
import com.jujodevs.pomodoro.core.ui.ObserveAsEvents
import org.koin.androidx.compose.koinViewModel

@Composable
fun StatisticsRoute(viewModel: StatisticsViewModel = koinViewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current

    ObserveAsEvents(viewModel.effects) { effect ->
        when (effect) {
            StatisticsEffect.ShareProgress -> handleShareProgress(context, state)
        }
    }

    StatisticsScreen(
        state = state,
        onAction = viewModel::onAction,
    )
}

private fun handleShareProgress(
    context: Context,
    state: StatisticsState,
) {
    val selectedMetricText =
        when (state.selectedWeekTimeMetric) {
            StatisticsWeekTimeMetric.FOCUS -> context.getString(R.string.statistics_share_metric_focus_time)
            StatisticsWeekTimeMetric.BREAK -> context.getString(R.string.statistics_share_metric_break_time)
            StatisticsWeekTimeMetric.TOTAL -> context.getString(R.string.statistics_share_metric_total_week_time)
        }
    val selectedMetricValue =
        when (state.selectedWeekTimeMetric) {
            StatisticsWeekTimeMetric.FOCUS -> state.totalFocusTimeFormatted
            StatisticsWeekTimeMetric.BREAK -> state.totalBreakTimeFormatted
            StatisticsWeekTimeMetric.TOTAL -> state.totalWeekTimeFormatted
        }

    val shareText =
        context.getString(
            R.string.statistics_share_message,
            selectedMetricText,
            selectedMetricValue,
            state.sessionsCompleted,
            state.dayStreak,
        )
    val intent =
        Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, shareText)
        }
    context.startActivity(Intent.createChooser(intent, null))
}
