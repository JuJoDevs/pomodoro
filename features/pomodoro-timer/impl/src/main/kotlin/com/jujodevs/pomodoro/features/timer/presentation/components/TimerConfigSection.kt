package com.jujodevs.pomodoro.features.timer.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.jujodevs.pomodoro.core.designsystem.components.card.PomodoroCard
import com.jujodevs.pomodoro.core.designsystem.components.input.PomodoroChip
import com.jujodevs.pomodoro.core.designsystem.components.input.PomodoroSwitch
import com.jujodevs.pomodoro.core.designsystem.theme.LocalSpacing
import com.jujodevs.pomodoro.core.designsystem.theme.PomodoroTheme
import com.jujodevs.pomodoro.core.resources.R
import com.jujodevs.pomodoro.features.timer.domain.model.PomodoroStatus
import com.jujodevs.pomodoro.features.timer.presentation.TimerAction
import com.jujodevs.pomodoro.features.timer.presentation.TimerState

@Composable
internal fun ConfigSection(
    state: TimerState,
    onAction: (TimerAction) -> Unit,
    modifier: Modifier = Modifier
) {
    val spacing = LocalSpacing.current
    val isIdle = state.status == PomodoroStatus.IDLE

    Column(modifier) {
        DurationSelectorCard(
            title = stringResource(R.string.label_work_duration),
            options = state.workDurationOptions,
            selectedOption = state.selectedWorkMinutes,
            onOptionSelected = { onAction(TimerAction.SelectWorkDuration(it)) },
            enabled = isIdle
        )

        Spacer(modifier = Modifier.height(spacing.spaceM))

        DurationSelectorCard(
            title = stringResource(R.string.label_break_duration),
            options = state.breakDurationOptions,
            selectedOption = state.selectedShortBreakMinutes,
            onOptionSelected = { onAction(TimerAction.SelectShortBreakDuration(it)) },
            enabled = isIdle
        )

        Spacer(modifier = Modifier.height(spacing.spaceM))

        AutoStartToggles(
            autoStartBreaks = state.autoStartBreaks,
            autoStartWork = state.autoStartWork,
            onToggleBreaks = { onAction(TimerAction.ToggleAutoStartBreaks(it)) },
            onToggleWork = { onAction(TimerAction.ToggleAutoStartWork(it)) }
        )
    }
}

@Composable
private fun DurationSelectorCard(
    title: String,
    options: List<Int>,
    selectedOption: Int,
    onOptionSelected: (Int) -> Unit,
    enabled: Boolean
) {
    val spacing = LocalSpacing.current

    PomodoroCard(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(spacing.spaceS)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = stringResource(R.string.label_min),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.height(spacing.spaceS))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                options.forEach { option ->
                    PomodoroChip(
                        text = option.toString(),
                        selected = option == selectedOption,
                        onClick = { if (enabled) onOptionSelected(option) }
                    )
                }
            }
        }
    }
}

@Composable
private fun AutoStartToggles(
    autoStartBreaks: Boolean,
    autoStartWork: Boolean,
    onToggleBreaks: (Boolean) -> Unit,
    onToggleWork: (Boolean) -> Unit
) {
    val spacing = LocalSpacing.current

    Row(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                PomodoroSwitch(
                    checked = autoStartBreaks,
                    onCheckedChange = onToggleBreaks
                )
                Spacer(modifier = Modifier.width(spacing.spaceXS))
                Text(
                    text = stringResource(R.string.label_auto_start_breaks),
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                PomodoroSwitch(
                    checked = autoStartWork,
                    onCheckedChange = onToggleWork
                )
                Spacer(modifier = Modifier.width(spacing.spaceXS))
                Text(
                    text = stringResource(R.string.label_auto_start_work),
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF1C2834)
@Composable
private fun ConfigSectionPreview() {
    PomodoroTheme(darkTheme = true) {
        Box(
            modifier = Modifier
                .padding(16.dp)
        ) {
            ConfigSection(
                state = TimerState(
                    status = PomodoroStatus.IDLE,
                    selectedWorkMinutes = 25,
                    selectedShortBreakMinutes = 5
                ),
                onAction = {}
            )
        }
    }
}
