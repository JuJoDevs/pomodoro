package com.jujodevs.pomodoro.features.timer.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.jujodevs.pomodoro.core.designsystem.components.button.PomodoroButton
import com.jujodevs.pomodoro.core.designsystem.components.button.PomodoroIconButton
import com.jujodevs.pomodoro.core.designsystem.components.icons.PomodoroIcons
import com.jujodevs.pomodoro.core.designsystem.theme.LocalSpacing
import com.jujodevs.pomodoro.core.designsystem.theme.PomodoroTheme
import com.jujodevs.pomodoro.core.resources.R
import com.jujodevs.pomodoro.features.timer.domain.model.PomodoroStatus
import com.jujodevs.pomodoro.features.timer.presentation.TimerAction
import com.jujodevs.pomodoro.features.timer.presentation.TimerState

@Composable
internal fun BottomSection(
    state: TimerState,
    onAction: (TimerAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    val spacing = LocalSpacing.current
    Column(
        modifier =
            modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.background)
                .padding(spacing.spaceXL),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        ControlButtons(
            status = state.status,
            onStart = { onAction(TimerAction.Start) },
            onPause = { onAction(TimerAction.Pause) },
            onResume = { onAction(TimerAction.Resume) },
            onReset = { onAction(TimerAction.Reset) },
        )

        Spacer(modifier = Modifier.height(spacing.spaceM))

        SessionProgress(
            completedSessions = state.completedSessions,
            totalSessions = state.totalSessions,
        )
    }
}

@Composable
private fun ControlButtons(
    status: PomodoroStatus,
    onStart: () -> Unit,
    onPause: () -> Unit,
    onResume: () -> Unit,
    onReset: () -> Unit,
) {
    val spacing = LocalSpacing.current

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
    ) {
        when (status) {
            PomodoroStatus.IDLE -> {
                PomodoroButton(
                    modifier = Modifier.fillMaxWidth(CONTROL_BUTTON_WIDTH_FRACTION),
                    text = stringResource(R.string.action_start),
                    onClick = onStart,
                    icon = PomodoroIcons.Play,
                )
            }
            PomodoroStatus.RUNNING -> {
                PomodoroButton(
                    modifier = Modifier.fillMaxWidth(CONTROL_BUTTON_WIDTH_FRACTION),
                    text = stringResource(R.string.action_pause),
                    onClick = onPause,
                    icon = PomodoroIcons.Pause,
                )
            }
            PomodoroStatus.PAUSED -> {
                PomodoroButton(
                    modifier = Modifier.fillMaxWidth(CONTROL_BUTTON_WIDTH_FRACTION),
                    text = stringResource(R.string.action_resume),
                    onClick = onResume,
                    icon = PomodoroIcons.Play,
                )
            }
        }

        Spacer(modifier = Modifier.width(spacing.spaceM))

        PomodoroIconButton(
            icon = PomodoroIcons.Reset,
            onClick = onReset,
            contentDescription = stringResource(R.string.action_reset),
        )
    }
}

@Composable
private fun SessionProgress(
    completedSessions: Int,
    totalSessions: Int,
) {
    val spacing = LocalSpacing.current

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column {
            Text(
                text = stringResource(R.string.label_session_progress),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = PROGRESS_LABEL_ALPHA),
            )
            Text(
                text = stringResource(R.string.label_sessions_completed, completedSessions, totalSessions),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
            )
        }

        Row {
            repeat(totalSessions) { index ->
                Box(
                    modifier =
                        Modifier
                            .padding(horizontal = spacing.spaceXXS)
                            .size(spacing.spaceS)
                            .clip(CircleShape)
                            .background(
                                if (index < completedSessions) {
                                    MaterialTheme.colorScheme.primary
                                } else {
                                    MaterialTheme.colorScheme.onBackground.copy(alpha = INACTIVE_DOT_ALPHA)
                                },
                            ),
                )
            }
        }
    }
}

private const val CONTROL_BUTTON_WIDTH_FRACTION = 0.7f
private const val PROGRESS_LABEL_ALPHA = 0.5f
private const val INACTIVE_DOT_ALPHA = 0.2f

@Preview(showBackground = true, backgroundColor = 0xFF1C2834)
@Composable
private fun BottomSectionPreview() {
    PomodoroTheme(darkTheme = true) {
        Box(modifier = Modifier.padding(16.dp)) {
            BottomSection(
                state =
                    TimerState(
                        status = PomodoroStatus.RUNNING,
                        completedSessions = 2,
                        totalSessions = 4,
                    ),
                onAction = {},
            )
        }
    }
}
