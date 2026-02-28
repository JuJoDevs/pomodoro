package com.jujodevs.pomodoro.features.onboarding.presentation

sealed interface OnboardingEffect {
    data object RequestPermissionsAndNavigateToHome : OnboardingEffect
}
