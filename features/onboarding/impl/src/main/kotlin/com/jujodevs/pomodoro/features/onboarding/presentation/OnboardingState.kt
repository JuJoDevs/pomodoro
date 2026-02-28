package com.jujodevs.pomodoro.features.onboarding.presentation

data class OnboardingState(
    val currentSlideIndex: Int = 0,
    val analyticsConsentChecked: Boolean = true,
    val isLoading: Boolean = true,
)
