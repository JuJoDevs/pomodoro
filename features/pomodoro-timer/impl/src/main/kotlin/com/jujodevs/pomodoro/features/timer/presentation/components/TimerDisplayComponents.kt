package com.jujodevs.pomodoro.features.timer.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.unit.sp
import com.jujodevs.pomodoro.core.designsystem.components.button.ButtonVariant
import com.jujodevs.pomodoro.core.designsystem.components.button.PomodoroButton
import com.jujodevs.pomodoro.core.designsystem.components.card.PomodoroCard
import com.jujodevs.pomodoro.core.designsystem.theme.LocalSpacing
import com.jujodevs.pomodoro.core.designsystem.theme.PomodoroTheme
import com.jujodevs.pomodoro.core.resources.R
import com.jujodevs.pomodoro.features.timer.domain.model.PomodoroPhase
import com.jujodevs.pomodoro.features.timer.domain.model.PomodoroStatus

@Composable
internal fun ExactAlarmWarningBanner(
    onDismiss: () -> Unit,
    onRequestPermission: () -> Unit
) {
    val spacing = LocalSpacing.current

    PomodoroCard(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(spacing.spaceS),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                modifier = Modifier.weight(1f),
                text = stringResource(R.string.warning_exact_alarm_missing),
                style = MaterialTheme.typography.bodySmall
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = spacing.spaceS),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.End
        ) {
            PomodoroButton(
                text = stringResource(R.string.action_grant),
                onClick = onRequestPermission,
                variant = ButtonVariant.Text
            )
            Spacer(modifier = Modifier.width(spacing.spaceS))
            PomodoroButton(
                text = stringResource(R.string.action_close),
                onClick = onDismiss,
                variant = ButtonVariant.Text
            )
        }
    }
}

@Composable
internal fun TimerDisplay(
    remainingTimeText: String,
    status: PomodoroStatus,
    phase: PomodoroPhase
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = remainingTimeText,
            style = MaterialTheme.typography.displayLarge.copy(
                fontSize = TIMER_FONT_SIZE.sp,
                fontWeight = FontWeight.Bold
            ),
            color = MaterialTheme.colorScheme.onBackground
        )

        Text(
            text = when (status) {
                PomodoroStatus.IDLE -> stringResource(R.string.label_ready_to_work)
                PomodoroStatus.RUNNING -> when (phase) {
                    PomodoroPhase.WORK -> stringResource(R.string.status_focusing)
                    PomodoroPhase.SHORT_BREAK -> stringResource(R.string.status_time_to_rest)
                    PomodoroPhase.LONG_BREAK -> stringResource(R.string.status_long_rest)
                }
                PomodoroStatus.PAUSED -> stringResource(R.string.status_paused)
            },
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = STATUS_ALPHA)
        )
    }
}

private const val TIMER_FONT_SIZE = 96
private const val STATUS_ALPHA = 0.7f

@Preview(showBackground = true, backgroundColor = 0xFF1C2834)
@Composable
private fun TimerDisplayPreview() {
    PomodoroTheme(darkTheme = true) {
        TimerDisplay(
            remainingTimeText = "12:34",
            status = PomodoroStatus.RUNNING,
            phase = PomodoroPhase.WORK
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF1C2834)
@Composable
private fun ExactAlarmWarningBannerPreview() {
    PomodoroTheme(darkTheme = true) {
        Box(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp)
        ) {
            ExactAlarmWarningBanner(
                onDismiss = {},
                onRequestPermission = {}
            )
        }
    }
}
