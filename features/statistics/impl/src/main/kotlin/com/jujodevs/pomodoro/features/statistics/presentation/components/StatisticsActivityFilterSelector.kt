package com.jujodevs.pomodoro.features.statistics.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.jujodevs.pomodoro.core.designsystem.components.button.ButtonVariant
import com.jujodevs.pomodoro.core.designsystem.components.button.PomodoroButton
import com.jujodevs.pomodoro.core.designsystem.theme.LocalSpacing
import com.jujodevs.pomodoro.core.resources.R
import com.jujodevs.pomodoro.features.statistics.presentation.StatisticsActivityFilter

@Composable
fun StatisticsActivityFilterSelector(
    selectedFilter: StatisticsActivityFilter,
    onFilterChange: (StatisticsActivityFilter) -> Unit,
    modifier: Modifier = Modifier,
) {
    val spacing = LocalSpacing.current

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(spacing.spaceS),
    ) {
        PomodoroButton(
            text = stringResource(R.string.statistics_filter_current_week),
            onClick = { onFilterChange(StatisticsActivityFilter.CURRENT_WEEK) },
            modifier = Modifier.weight(1f),
            variant =
                if (selectedFilter == StatisticsActivityFilter.CURRENT_WEEK) {
                    ButtonVariant.Primary
                } else {
                    ButtonVariant.Secondary
                },
        )
        PomodoroButton(
            text = stringResource(R.string.statistics_filter_months),
            onClick = { onFilterChange(StatisticsActivityFilter.MONTHS) },
            modifier = Modifier.weight(1f),
            variant =
                if (selectedFilter == StatisticsActivityFilter.MONTHS) {
                    ButtonVariant.Primary
                } else {
                    ButtonVariant.Secondary
                },
        )
    }
}
