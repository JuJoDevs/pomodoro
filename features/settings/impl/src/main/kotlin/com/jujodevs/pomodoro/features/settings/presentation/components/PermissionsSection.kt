package com.jujodevs.pomodoro.features.settings.presentation.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import com.jujodevs.pomodoro.core.designsystem.components.surface.PomodoroDivider
import com.jujodevs.pomodoro.core.designsystem.theme.LocalSpacing
import com.jujodevs.pomodoro.core.designsystem.theme.PomodoroTheme
import com.jujodevs.pomodoro.core.resources.R
import com.jujodevs.pomodoro.features.settings.presentation.SettingsAction

@Composable
fun PermissionsSection(
    canScheduleExactAlarms: Boolean,
    hasNotificationPermission: Boolean,
    onAction: (SettingsAction) -> Unit,
    modifier: Modifier = Modifier
) {
    val spacing = LocalSpacing.current

    Column(modifier = modifier) {
        Text(
            text = stringResource(R.string.settings_section_permissions),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = FontWeight.Medium
        )
        Spacer(modifier = Modifier.height(spacing.spaceS))
        PomodoroCard(modifier = Modifier.fillMaxWidth()) {
            PermissionRow(
                label = stringResource(R.string.settings_label_exact_alarm),
                isGranted = canScheduleExactAlarms,
                grantButtonText = stringResource(R.string.settings_action_grant),
                onGrantClick = { onAction(SettingsAction.GrantExactAlarmPermission) }
            )
            Spacer(modifier = Modifier.height(spacing.spaceS))
            PomodoroDivider()
            Spacer(modifier = Modifier.height(spacing.spaceS))
            PermissionRow(
                label = stringResource(R.string.settings_label_notification),
                isGranted = hasNotificationPermission,
                grantButtonText = stringResource(R.string.settings_action_grant),
                onGrantClick = { onAction(SettingsAction.RequestNotificationPermission) }
            )
        }
    }
}

@Composable
private fun PermissionRow(
    label: String,
    isGranted: Boolean,
    grantButtonText: String,
    onGrantClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = LocalSpacing.current.spaceXS),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(1f)
        )
        if (isGranted) {
            Text(
                text = stringResource(R.string.settings_status_granted),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary
            )
        } else {
            Text(
                text = grantButtonText,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.clickable(onClick = onGrantClick)
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF1C2834)
@Composable
private fun PermissionsSectionPreview() {
    PomodoroTheme(darkTheme = true) {
        PermissionsSection(
            canScheduleExactAlarms = false,
            hasNotificationPermission = false,
            onAction = {},
            modifier = Modifier.padding(16.dp)
        )
    }
}
