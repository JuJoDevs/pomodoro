package com.jujodevs.pomodoro.features.settings.presentation.components

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
import com.jujodevs.pomodoro.core.designsystem.components.input.PomodoroSwitch
import com.jujodevs.pomodoro.core.designsystem.theme.LocalSpacing
import com.jujodevs.pomodoro.core.designsystem.theme.PomodoroTheme
import com.jujodevs.pomodoro.core.resources.R
import com.jujodevs.pomodoro.features.settings.presentation.SettingsAction

@Composable
fun analyticsSection(
    analyticsEnabled: Boolean,
    onAction: (SettingsAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    val spacing = LocalSpacing.current

    Column(modifier = modifier) {
        Text(
            text = stringResource(R.string.settings_section_analytics),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = FontWeight.Medium,
        )
        Spacer(modifier = Modifier.height(spacing.spaceS))
        PomodoroCard(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = stringResource(R.string.settings_label_analytics_collection),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                    Spacer(modifier = Modifier.height(spacing.spaceXXS))
                    Text(
                        text = stringResource(R.string.settings_description_analytics_collection),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                Spacer(modifier = Modifier.width(spacing.spaceM))
                PomodoroSwitch(
                    checked = analyticsEnabled,
                    onCheckedChange = {
                        onAction(SettingsAction.ToggleAnalyticsCollection(it))
                    },
                )
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF1C2834)
@Composable
private fun analyticsSectionPreview() {
    PomodoroTheme(darkTheme = true) {
        analyticsSection(
            analyticsEnabled = true,
            onAction = {},
            modifier = Modifier.padding(16.dp),
        )
    }
}
