package com.jujodevs.pomodoro.features.timer.presentation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.jujodevs.pomodoro.core.designsystem.theme.LocalSpacing
import com.jujodevs.pomodoro.core.designsystem.theme.PomodoroTheme
import com.jujodevs.pomodoro.features.timer.domain.model.PomodoroPhase
import com.jujodevs.pomodoro.features.timer.domain.model.PomodoroStatus
import com.jujodevs.pomodoro.features.timer.presentation.components.BottomSection
import com.jujodevs.pomodoro.features.timer.presentation.components.ConfigSection
import com.jujodevs.pomodoro.features.timer.presentation.components.ExactAlarmWarningBanner
import com.jujodevs.pomodoro.features.timer.presentation.components.HandleModals
import com.jujodevs.pomodoro.features.timer.presentation.components.TimerDisplay

@Composable
fun TimerScreen(
    state: TimerState,
    onAction: (TimerAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    val spacing = LocalSpacing.current

    Column(modifier = modifier.fillMaxSize()) {
        Column(
            modifier =
                Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = spacing.spaceXL),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(modifier = Modifier.height(spacing.spaceM))

            if (state.isExactAlarmPermissionMissing) {
                ExactAlarmWarningBanner(
                    onDismiss = { onAction(TimerAction.DismissExactAlarmWarning) },
                    onRequestPermission = { onAction(TimerAction.RequestExactAlarmPermission) },
                )
                Spacer(modifier = Modifier.height(spacing.spaceM))
            }

            TimerDisplay(
                remainingTimeText = state.remainingTimeText,
                status = state.status,
                phase = state.phase,
            )

            Spacer(modifier = Modifier.height(spacing.spaceXXL))

            ConfigSection(
                state = state,
                onAction = onAction,
            )

            Spacer(modifier = Modifier.height(spacing.spaceXL))
        }

        BottomSection(
            state = state,
            onAction = onAction,
            modifier = Modifier.fillMaxWidth(),
        )
    }

    HandleModals(
        state = state,
        onAction = onAction,
    )
}

@Preview(showBackground = true, backgroundColor = 0xFF1C2834)
@Composable
private fun TimerScreenPreview() {
    PomodoroTheme(darkTheme = true) {
        TimerScreen(
            state =
                TimerState(
                    phase = PomodoroPhase.WORK,
                    status = PomodoroStatus.IDLE,
                    remainingTimeText = "25:00",
                    progress = 0f,
                    completedSessions = 0,
                ),
            onAction = {},
        )
    }
}
