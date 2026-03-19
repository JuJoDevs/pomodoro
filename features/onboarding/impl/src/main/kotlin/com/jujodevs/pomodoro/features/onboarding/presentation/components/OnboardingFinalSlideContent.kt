package com.jujodevs.pomodoro.features.onboarding.presentation.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.jujodevs.pomodoro.core.designsystem.components.button.ButtonVariant
import com.jujodevs.pomodoro.core.designsystem.components.button.PomodoroButton
import com.jujodevs.pomodoro.core.designsystem.theme.LocalSpacing
import com.jujodevs.pomodoro.core.resources.R
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

        Spacer(modifier = Modifier.height(spacing.spaceXS))

        PomodoroButton(
            text = stringResource(R.string.action_privacy_policy),
            onClick = { onAction(OnboardingAction.OpenPrivacyPolicy) },
            variant = ButtonVariant.Text,
            modifier = Modifier.align(Alignment.CenterHorizontally),
        )

        Spacer(modifier = Modifier.height(spacing.spaceXL))
    }
}
