package com.jujodevs.pomodoro.features.statistics.presentation.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.jujodevs.pomodoro.core.designsystem.components.button.PomodoroButton
import com.jujodevs.pomodoro.core.designsystem.components.icons.PomodoroIcons
import com.jujodevs.pomodoro.core.designsystem.theme.PomodoroTheme
import com.jujodevs.pomodoro.core.resources.R

@Composable
fun StatisticsShareButton(
    onClick: () -> Unit,
    enabled: Boolean,
    modifier: Modifier = Modifier,
) {
    PomodoroButton(
        text = stringResource(R.string.statistics_action_share_progress),
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        variant = com.jujodevs.pomodoro.core.designsystem.components.button.ButtonVariant.Primary,
        enabled = enabled,
        icon = PomodoroIcons.Share,
    )
}

@Preview(showBackground = true, backgroundColor = 0xFF1C2834)
@Composable
private fun StatisticsShareButtonEnabledPreview() {
    PomodoroTheme(darkTheme = true) {
        StatisticsShareButton(
            onClick = {},
            enabled = true,
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF1C2834)
@Composable
private fun StatisticsShareButtonDisabledPreview() {
    PomodoroTheme(darkTheme = true) {
        StatisticsShareButton(
            onClick = {},
            enabled = false,
        )
    }
}
