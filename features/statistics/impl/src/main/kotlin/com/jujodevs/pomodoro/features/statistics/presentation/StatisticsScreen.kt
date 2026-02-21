package com.jujodevs.pomodoro.features.statistics.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.jujodevs.pomodoro.core.designsystem.components.button.ButtonVariant
import com.jujodevs.pomodoro.core.designsystem.components.button.PomodoroButton
import com.jujodevs.pomodoro.core.designsystem.theme.AccentOrange
import com.jujodevs.pomodoro.core.designsystem.theme.LocalSpacing
import com.jujodevs.pomodoro.core.designsystem.theme.PomodoroTheme
import com.jujodevs.pomodoro.core.resources.R
import com.jujodevs.pomodoro.features.statistics.domain.usecase.ActivityChartEntry
import com.jujodevs.pomodoro.features.statistics.presentation.components.StatisticsActivityCard
import com.jujodevs.pomodoro.features.statistics.presentation.components.StatisticsActivityFilterSelector
import com.jujodevs.pomodoro.features.statistics.presentation.components.StatisticsHeaderSummary
import com.jujodevs.pomodoro.features.statistics.presentation.components.StatisticsKpiCard
import com.jujodevs.pomodoro.features.statistics.presentation.components.StatisticsShareButton

@Composable
fun StatisticsScreen(
    state: StatisticsState,
    onAction: (StatisticsAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    val spacing = LocalSpacing.current
    val hasData =
        state.sessionsCompleted > 0 ||
            state.totalWeekTimeFormatted != "00:00" ||
            state.weeklyActivity.any { it.focusTimeMillis > 0L } ||
            state.monthlyActivity.any { it.focusTimeMillis > 0L }

    Column(
        modifier =
            modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        when {
            state.isLoading -> LoadingContent()
            state.error != null -> ErrorContent(state.error, spacing) { onAction(StatisticsAction.Retry) }
            !hasData -> EmptyContent(spacing)
            else -> SuccessContent(state = state, onAction = onAction, spacing = spacing)
        }
    }
}

@Composable
private fun LoadingContent() {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        CircularProgressIndicator()
    }
}

@Composable
private fun ErrorContent(
    error: StatisticsError,
    spacing: com.jujodevs.pomodoro.core.designsystem.theme.Spacing,
    onRetry: () -> Unit,
) {
    val errorMessage =
        when (error) {
            StatisticsError.GENERIC -> stringResource(R.string.error_generic)
            StatisticsError.NETWORK -> stringResource(R.string.error_network)
        }

    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .padding(spacing.spaceXL),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = errorMessage,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center,
        )
        Spacer(modifier = Modifier.height(spacing.spaceL))
        PomodoroButton(
            text = stringResource(R.string.statistics_action_retry),
            onClick = onRetry,
            variant = ButtonVariant.Primary,
        )
    }
}

@Composable
private fun EmptyContent(spacing: com.jujodevs.pomodoro.core.designsystem.theme.Spacing) {
    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .padding(spacing.spaceXL),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = stringResource(R.string.statistics_empty_title),
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onBackground,
        )
        Spacer(modifier = Modifier.height(spacing.spaceM))
        Text(
            text = stringResource(R.string.statistics_empty_message),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun SuccessContent(
    state: StatisticsState,
    onAction: (StatisticsAction) -> Unit,
    spacing: com.jujodevs.pomodoro.core.designsystem.theme.Spacing,
) {
    val (activityTitle, activitySubtitle, activityData) =
        resolveActivityContent(
            selectedFilter = state.selectedActivityFilter,
            selectedWeekTimeMetric = state.selectedWeekTimeMetric,
            weeklyActivity = state.weeklyActivity,
            monthlyActivity = state.monthlyActivity,
        )
    val weekTimeMetric = resolveWeekTimeMetricContent(state)

    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .widthIn(max = SCREEN_MAX_WIDTH.dp)
                    .padding(horizontal = spacing.spaceXL),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(modifier = Modifier.height(spacing.spaceL))
            StatisticsHeaderSummary(
                labelRes = weekTimeMetric.labelRes,
                totalTimeFormatted = weekTimeMetric.formattedTime,
                onClick = { onAction(StatisticsAction.CycleWeekTimeMetric) },
            )

            Spacer(modifier = Modifier.height(spacing.spaceL))
            KpiSection(state = state, spacing = spacing)

            Spacer(modifier = Modifier.height(spacing.spaceL))
            StatisticsActivityFilterSelector(
                selectedFilter = state.selectedActivityFilter,
                onFilterChange = { onAction(StatisticsAction.SelectActivityFilter(it)) },
            )

            Spacer(modifier = Modifier.height(spacing.spaceL))
            StatisticsActivityCard(
                title = stringResource(activityTitle),
                subtitle = stringResource(activitySubtitle),
                activity = activityData,
            )

            Spacer(modifier = Modifier.height(spacing.spaceXL))
            StatisticsShareButton(
                onClick = { onAction(StatisticsAction.ShareProgress) },
                enabled = state.canShare,
            )
            Spacer(modifier = Modifier.height(spacing.spaceXXL))
        }
    }
}

@Composable
private fun KpiSection(
    state: StatisticsState,
    spacing: com.jujodevs.pomodoro.core.designsystem.theme.Spacing,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(spacing.spaceM),
    ) {
        StatisticsKpiCard(
            value = state.sessionsCompleted.toString(),
            label = stringResource(R.string.statistics_label_sessions),
            icon = com.jujodevs.pomodoro.core.designsystem.components.icons.PomodoroIcons.Check,
            modifier = Modifier.weight(1f),
        )
        StatisticsKpiCard(
            value = state.dayStreak.toString(),
            label = stringResource(R.string.statistics_label_day_streak),
            icon = com.jujodevs.pomodoro.core.designsystem.components.icons.PomodoroIcons.Flame,
            modifier = Modifier.weight(1f),
            iconTint = AccentOrange,
        )
    }
}

private fun resolveActivityContent(
    selectedFilter: StatisticsActivityFilter,
    selectedWeekTimeMetric: StatisticsWeekTimeMetric,
    weeklyActivity: List<ActivityChartEntry>,
    monthlyActivity: List<ActivityChartEntry>,
): Triple<Int, Int, List<ActivityChartEntry>> =
    when (selectedFilter) {
        StatisticsActivityFilter.CURRENT_WEEK ->
            Triple(
                R.string.statistics_label_weekly_activity,
                R.string.statistics_label_current_week,
                weeklyActivity.mapToMetric(selectedWeekTimeMetric),
            )
        StatisticsActivityFilter.MONTHS ->
            Triple(
                R.string.statistics_label_monthly_activity,
                R.string.statistics_label_last_12_months,
                monthlyActivity.mapToMetric(selectedWeekTimeMetric),
            )
    }

private fun List<ActivityChartEntry>.mapToMetric(metric: StatisticsWeekTimeMetric): List<ActivityChartEntry> =
    map { entry ->
        val selectedMillis =
            when (metric) {
                StatisticsWeekTimeMetric.FOCUS -> entry.focusTimeMillis
                StatisticsWeekTimeMetric.BREAK -> entry.breakTimeMillis
                StatisticsWeekTimeMetric.TOTAL -> entry.focusTimeMillis + entry.breakTimeMillis
            }
        entry.copy(focusTimeMillis = selectedMillis)
    }

private data class WeekTimeMetricContent(
    val labelRes: Int,
    val formattedTime: String,
)

private fun resolveWeekTimeMetricContent(state: StatisticsState): WeekTimeMetricContent =
    when (state.selectedWeekTimeMetric) {
        StatisticsWeekTimeMetric.FOCUS ->
            WeekTimeMetricContent(
                labelRes = R.string.statistics_label_total_focus_time,
                formattedTime = state.totalFocusTimeFormatted,
            )
        StatisticsWeekTimeMetric.BREAK ->
            WeekTimeMetricContent(
                labelRes = R.string.statistics_label_total_break_time,
                formattedTime = state.totalBreakTimeFormatted,
            )
        StatisticsWeekTimeMetric.TOTAL ->
            WeekTimeMetricContent(
                labelRes = R.string.statistics_label_total_week_time,
                formattedTime = state.totalWeekTimeFormatted,
            )
    }

@Preview(showBackground = true, backgroundColor = 0xFF1C2834)
@Composable
private fun StatisticsScreenSuccessPreview() {
    PomodoroTheme(darkTheme = true) {
        StatisticsScreen(
            state =
                StatisticsState(
                    isLoading = false,
                    error = null,
                    selectedActivityFilter = StatisticsActivityFilter.CURRENT_WEEK,
                    totalFocusTimeFormatted = "04:30",
                    totalBreakTimeFormatted = "01:20",
                    totalWeekTimeFormatted = "05:50",
                    sessionsCompleted = PREVIEW_SESSIONS_COMPLETED,
                    dayStreak = PREVIEW_DAY_STREAK,
                    weeklyActivity = previewWeeklyActivity(),
                    monthlyActivity = previewMonthlyActivity(),
                    canShare = true,
                ),
            onAction = {},
        )
    }
}

private fun previewWeeklyActivity(): List<ActivityChartEntry> =
    listOf(
        ActivityChartEntry("M", PREVIEW_MONDAY_FOCUS_TIME),
        ActivityChartEntry("T", PREVIEW_TUESDAY_FOCUS_TIME),
        ActivityChartEntry("W", PREVIEW_WEDNESDAY_FOCUS_TIME),
        ActivityChartEntry("T", PREVIEW_THURSDAY_FOCUS_TIME),
        ActivityChartEntry("F", PREVIEW_FRIDAY_FOCUS_TIME),
        ActivityChartEntry("S", PREVIEW_SATURDAY_FOCUS_TIME),
        ActivityChartEntry("S", PREVIEW_SUNDAY_FOCUS_TIME),
    )

private fun previewMonthlyActivity(): List<ActivityChartEntry> =
    listOf(
        ActivityChartEntry("Jan", PREVIEW_JAN_FOCUS_TIME),
        ActivityChartEntry("Feb", PREVIEW_FEB_FOCUS_TIME),
        ActivityChartEntry("Mar", PREVIEW_MAR_FOCUS_TIME),
        ActivityChartEntry("Apr", PREVIEW_APR_FOCUS_TIME),
        ActivityChartEntry("May", PREVIEW_MAY_FOCUS_TIME),
        ActivityChartEntry("Jun", PREVIEW_JUN_FOCUS_TIME),
        ActivityChartEntry("Jul", PREVIEW_JUL_FOCUS_TIME),
        ActivityChartEntry("Aug", PREVIEW_AUG_FOCUS_TIME),
        ActivityChartEntry("Sep", PREVIEW_SEP_FOCUS_TIME),
        ActivityChartEntry("Oct", PREVIEW_OCT_FOCUS_TIME),
        ActivityChartEntry("Nov", PREVIEW_NOV_FOCUS_TIME),
        ActivityChartEntry("Dec", PREVIEW_DEC_FOCUS_TIME),
    )

private const val SCREEN_MAX_WIDTH = 560
private const val PREVIEW_SESSIONS_COMPLETED = 12
private const val PREVIEW_DAY_STREAK = 5
private const val MILLIS_IN_MINUTE = 60_000L
private const val MILLIS_IN_HOUR = 3_600_000L
private const val PREVIEW_MONDAY_FOCUS_TIME = 45L * MILLIS_IN_MINUTE
private const val PREVIEW_TUESDAY_FOCUS_TIME = 30L * MILLIS_IN_MINUTE
private const val PREVIEW_WEDNESDAY_FOCUS_TIME = 60L * MILLIS_IN_MINUTE
private const val PREVIEW_THURSDAY_FOCUS_TIME = 0L
private const val PREVIEW_FRIDAY_FOCUS_TIME = 90L * MILLIS_IN_MINUTE
private const val PREVIEW_SATURDAY_FOCUS_TIME = 20L * MILLIS_IN_MINUTE
private const val PREVIEW_SUNDAY_FOCUS_TIME = 15L * MILLIS_IN_MINUTE
private const val PREVIEW_JAN_FOCUS_TIME = 20L * MILLIS_IN_HOUR
private const val PREVIEW_FEB_FOCUS_TIME = 25L * MILLIS_IN_HOUR
private const val PREVIEW_MAR_FOCUS_TIME = 22L * MILLIS_IN_HOUR
private const val PREVIEW_APR_FOCUS_TIME = 18L * MILLIS_IN_HOUR
private const val PREVIEW_MAY_FOCUS_TIME = 26L * MILLIS_IN_HOUR
private const val PREVIEW_JUN_FOCUS_TIME = 24L * MILLIS_IN_HOUR
private const val PREVIEW_JUL_FOCUS_TIME = 28L * MILLIS_IN_HOUR
private const val PREVIEW_AUG_FOCUS_TIME = 21L * MILLIS_IN_HOUR
private const val PREVIEW_SEP_FOCUS_TIME = 19L * MILLIS_IN_HOUR
private const val PREVIEW_OCT_FOCUS_TIME = 30L * MILLIS_IN_HOUR
private const val PREVIEW_NOV_FOCUS_TIME = 27L * MILLIS_IN_HOUR
private const val PREVIEW_DEC_FOCUS_TIME = 23L * MILLIS_IN_HOUR
