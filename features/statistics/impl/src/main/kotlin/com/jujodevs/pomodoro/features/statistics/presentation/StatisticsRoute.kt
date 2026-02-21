package com.jujodevs.pomodoro.features.statistics.presentation

import android.content.Context
import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
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
            StatisticsWeekTimeMetric.FOCUS -> "Focus time"
            StatisticsWeekTimeMetric.BREAK -> "Break time"
            StatisticsWeekTimeMetric.TOTAL -> "Total week time"
        }
    val selectedMetricValue =
        when (state.selectedWeekTimeMetric) {
            StatisticsWeekTimeMetric.FOCUS -> state.totalFocusTimeFormatted
            StatisticsWeekTimeMetric.BREAK -> state.totalBreakTimeFormatted
            StatisticsWeekTimeMetric.TOTAL -> state.totalWeekTimeFormatted
        }

    val shareText =
        buildString {
            append("This week $selectedMetricText: $selectedMetricValue (Hours:Minutes).")
            append(" Completed ${state.sessionsCompleted} sessions.")
            append(" Day streak: ${state.dayStreak}.")
        }
    val intent =
        Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, shareText)
        }
    context.startActivity(Intent.createChooser(intent, null))
}
