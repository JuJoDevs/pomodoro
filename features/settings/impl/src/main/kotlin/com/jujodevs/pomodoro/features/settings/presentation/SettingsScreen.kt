package com.jujodevs.pomodoro.features.settings.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.jujodevs.pomodoro.core.designsystem.theme.LocalSpacing
import com.jujodevs.pomodoro.core.designsystem.theme.PomodoroTheme
import com.jujodevs.pomodoro.features.settings.presentation.components.analyticsSection
import com.jujodevs.pomodoro.features.settings.presentation.components.permissionsSection
import com.jujodevs.pomodoro.features.settings.presentation.components.soundLibrarySection
import com.jujodevs.pomodoro.features.settings.presentation.components.versionRow

@Composable
fun settingsScreen(
    state: SettingsState,
    versionText: String,
    onAction: (SettingsAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    val spacing = LocalSpacing.current

    if (state.isLoading) {
        Column(
            modifier = modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(modifier = Modifier.weight(1f))
            CircularProgressIndicator()
            Spacer(modifier = Modifier.weight(1f))
        }
        return
    }

    Column(
        modifier =
            modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = spacing.spaceXL),
    ) {
        Spacer(modifier = Modifier.height(spacing.spaceM))

        soundLibrarySection(
            alarmSoundLabel = state.alarmSoundLabel,
            onAction = onAction,
        )

        Spacer(modifier = Modifier.height(spacing.spaceXL))

        analyticsSection(
            analyticsEnabled = state.analyticsCollectionEnabled,
            onAction = onAction,
        )

        Spacer(modifier = Modifier.height(spacing.spaceXL))

        permissionsSection(
            canScheduleExactAlarms = state.canScheduleExactAlarms,
            hasNotificationPermission = state.hasNotificationPermission,
            onAction = onAction,
        )

        Spacer(modifier = Modifier.height(spacing.spaceXXL))

        versionRow(versionText = versionText)
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF1C2834)
@Composable
private fun settingsScreenPreview() {
    PomodoroTheme(darkTheme = true) {
        settingsScreen(
            state =
                SettingsState(
                    alarmSoundLabel = "Digital Beep (Default)",
                    analyticsCollectionEnabled = true,
                    canScheduleExactAlarms = true,
                    hasNotificationPermission = true,
                    isLoading = false,
                ),
            versionText = "Version 1.0.0 (1)",
            onAction = {},
            modifier = Modifier.padding(16.dp),
        )
    }
}
