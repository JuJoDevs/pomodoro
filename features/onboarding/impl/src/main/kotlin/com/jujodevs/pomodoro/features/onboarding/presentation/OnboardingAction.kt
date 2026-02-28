package com.jujodevs.pomodoro.features.onboarding.presentation

sealed interface OnboardingAction {
    data object NextSlide : OnboardingAction

    data class UpdateCurrentSlide(
        val index: Int,
    ) : OnboardingAction

    data class ToggleAnalyticsConsent(
        val enabled: Boolean,
    ) : OnboardingAction

    data object CompleteOnboarding : OnboardingAction
}
