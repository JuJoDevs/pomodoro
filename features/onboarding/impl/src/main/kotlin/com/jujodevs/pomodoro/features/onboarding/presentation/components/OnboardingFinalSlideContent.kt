package com.jujodevs.pomodoro.features.onboarding.presentation.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.jujodevs.pomodoro.core.designsystem.theme.LocalSpacing
import com.jujodevs.pomodoro.features.onboarding.presentation.OnboardingAction

@Composable
fun OnboardingFinalSlideContent(
    analyticsConsentChecked: Boolean,
    onAction: (OnboardingAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    val spacing = LocalSpacing.current

    Column(modifier = modifier) {
        OnboardingConsentSection(
            analyticsConsentChecked = analyticsConsentChecked,
            onConsentChange = { onAction(OnboardingAction.ToggleAnalyticsConsent(it)) },
        )

        Spacer(modifier = Modifier.height(spacing.spaceXL))
    }
}
