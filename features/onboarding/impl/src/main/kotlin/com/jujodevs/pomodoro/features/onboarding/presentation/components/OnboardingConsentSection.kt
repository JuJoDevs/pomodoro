package com.jujodevs.pomodoro.features.onboarding.presentation.components

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

@Composable
fun OnboardingConsentSection(
    analyticsConsentChecked: Boolean,
    onConsentChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    val spacing = LocalSpacing.current

    PomodoroCard(modifier = modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(spacing.spaceM)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = stringResource(R.string.onboarding_consent_label),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Medium,
                    )
                    Spacer(modifier = Modifier.height(spacing.spaceXXS))
                    Text(
                        text = stringResource(R.string.onboarding_consent_description),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                Spacer(modifier = Modifier.width(spacing.spaceM))
                PomodoroSwitch(
                    checked = analyticsConsentChecked,
                    onCheckedChange = onConsentChange,
                )
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF1C2834)
@Composable
private fun OnboardingConsentSectionUncheckedPreview() {
    PomodoroTheme(darkTheme = true) {
        OnboardingConsentSection(
            analyticsConsentChecked = false,
            onConsentChange = {},
            modifier = Modifier.padding(16.dp),
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF1C2834)
@Composable
private fun OnboardingConsentSectionCheckedPreview() {
    PomodoroTheme(darkTheme = true) {
        OnboardingConsentSection(
            analyticsConsentChecked = true,
            onConsentChange = {},
            modifier = Modifier.padding(16.dp),
        )
    }
}
