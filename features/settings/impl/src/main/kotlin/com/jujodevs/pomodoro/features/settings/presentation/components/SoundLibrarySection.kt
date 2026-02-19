package com.jujodevs.pomodoro.features.settings.presentation.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.jujodevs.pomodoro.core.designsystem.components.card.PomodoroCard
import com.jujodevs.pomodoro.core.designsystem.components.icons.PomodoroIcons
import com.jujodevs.pomodoro.core.designsystem.theme.LocalSpacing
import com.jujodevs.pomodoro.core.designsystem.theme.PomodoroTheme
import com.jujodevs.pomodoro.core.resources.R
import com.jujodevs.pomodoro.features.settings.presentation.SettingsAction

@Composable
fun soundLibrarySection(
    alarmSoundLabel: String,
    onAction: (SettingsAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        alarmSoundRow(
            label = stringResource(R.string.settings_label_alarm_sound),
            soundLabel = alarmSoundLabel,
            onClick = { onAction(SettingsAction.OpenNotificationChannelSettings) },
        )
    }
}

@Composable
private fun alarmSoundRow(
    label: String,
    soundLabel: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val spacing = LocalSpacing.current

    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(modifier = Modifier.height(spacing.spaceXS))
        PomodoroCard(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .clickable(onClick = onClick),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = soundLabel,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.weight(1f),
                )
                Spacer(modifier = Modifier.width(spacing.spaceXS))
                Icon(
                    imageVector = PomodoroIcons.ChevronDown,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            Spacer(modifier = Modifier.height(spacing.spaceXS))
            Text(
                text = stringResource(R.string.settings_action_open_channel_settings),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF1C2834)
@Composable
private fun soundLibrarySectionPreview() {
    PomodoroTheme(darkTheme = true) {
        soundLibrarySection(
            alarmSoundLabel = "Digital Beep (Default)",
            onAction = {},
            modifier = Modifier.padding(16.dp),
        )
    }
}
