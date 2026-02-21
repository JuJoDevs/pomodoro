package com.jujodevs.pomodoro.features.statistics.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.jujodevs.pomodoro.core.designsystem.components.card.PomodoroCard
import com.jujodevs.pomodoro.core.designsystem.theme.LocalSpacing
import com.jujodevs.pomodoro.core.designsystem.theme.PomodoroTheme
import com.jujodevs.pomodoro.core.designsystem.theme.ProgressTrack
import com.jujodevs.pomodoro.core.designsystem.theme.TextSecondary
import com.jujodevs.pomodoro.features.statistics.domain.usecase.ActivityChartEntry

@Composable
fun StatisticsActivityCard(
    title: String,
    subtitle: String,
    activity: List<ActivityChartEntry>,
    modifier: Modifier = Modifier,
) {
    PomodoroCard(modifier = modifier.fillMaxWidth()) {
        ActivityCardContent(
            title = title,
            subtitle = subtitle,
            activity = activity,
        )
    }
}

@Composable
private fun ActivityCardContent(
    title: String,
    subtitle: String,
    activity: List<ActivityChartEntry>,
) {
    val spacing = LocalSpacing.current
    val maxFocus = activity.maxOfOrNull { it.focusTimeMillis }?.coerceAtLeast(MIN_FOCUS_TIME) ?: MIN_FOCUS_TIME

    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary,
            )
        }

        Spacer(modifier = Modifier.height(spacing.spaceL))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.Bottom,
        ) {
            activity.forEach { item ->
                ActivityBar(
                    item = item,
                    maxFocus = maxFocus,
                )
            }
        }
    }
}

@Composable
private fun RowScope.ActivityBar(
    item: ActivityChartEntry,
    maxFocus: Long,
) {
    val spacing = LocalSpacing.current
    val barHeight = (item.focusTimeMillis.toFloat() / maxFocus * CHART_MAX_BAR_HEIGHT).coerceAtLeast(MIN_BAR_HEIGHT)

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.weight(1f),
    ) {
        Box(
            modifier =
                Modifier
                    .height(CHART_MAX_BAR_HEIGHT.dp)
                    .fillMaxWidth()
                    .padding(horizontal = OUTER_BAR_HORIZONTAL_PADDING.dp),
            contentAlignment = Alignment.BottomCenter,
        ) {
            Box(
                modifier =
                    Modifier
                        .height(barHeight.dp)
                        .fillMaxWidth()
                        .padding(horizontal = INNER_BAR_HORIZONTAL_PADDING.dp)
                        .padding(bottom = INNER_BAR_BOTTOM_PADDING.dp)
                        .background(
                            color = resolveBarColor(item.focusTimeMillis),
                            shape = MaterialTheme.shapes.small,
                        ),
            )
        }
        Spacer(modifier = Modifier.height(spacing.spaceXS))
        Text(
            text = item.label,
            style = MaterialTheme.typography.labelSmall,
            color = TextSecondary,
        )
    }
}

@Composable
private fun resolveBarColor(focusTimeMillis: Long) =
    if (focusTimeMillis > 0) {
        MaterialTheme.colorScheme.primary
    } else {
        ProgressTrack
    }

@Preview(showBackground = true, backgroundColor = 0xFF1C2834)
@Composable
private fun StatisticsWeeklyActivityCardPreview() {
    PomodoroTheme(darkTheme = true) {
        StatisticsActivityCard(
            title = "Weekly Activity",
            subtitle = "Current Week",
            activity =
                listOf(
                    ActivityChartEntry("M", SAMPLE_MONDAY_FOCUS_TIME),
                    ActivityChartEntry("T", SAMPLE_TUESDAY_FOCUS_TIME),
                    ActivityChartEntry("W", SAMPLE_WEDNESDAY_FOCUS_TIME),
                    ActivityChartEntry("T", 0L),
                    ActivityChartEntry("F", SAMPLE_FRIDAY_FOCUS_TIME),
                    ActivityChartEntry("S", SAMPLE_SATURDAY_FOCUS_TIME),
                    ActivityChartEntry("S", SAMPLE_SUNDAY_FOCUS_TIME),
                ),
        )
    }
}

private const val CHART_MAX_BAR_HEIGHT = 80f
private const val MIN_BAR_HEIGHT = 4f
private const val MIN_FOCUS_TIME = 1L
private const val OUTER_BAR_HORIZONTAL_PADDING = 4
private const val INNER_BAR_HORIZONTAL_PADDING = 2
private const val INNER_BAR_BOTTOM_PADDING = 2
private const val MILLIS_IN_MINUTE = 60_000L
private const val SAMPLE_MONDAY_FOCUS_TIME = 45L * MILLIS_IN_MINUTE
private const val SAMPLE_TUESDAY_FOCUS_TIME = 30L * MILLIS_IN_MINUTE
private const val SAMPLE_WEDNESDAY_FOCUS_TIME = 60L * MILLIS_IN_MINUTE
private const val SAMPLE_FRIDAY_FOCUS_TIME = 90L * MILLIS_IN_MINUTE
private const val SAMPLE_SATURDAY_FOCUS_TIME = 20L * MILLIS_IN_MINUTE
private const val SAMPLE_SUNDAY_FOCUS_TIME = 15L * MILLIS_IN_MINUTE
