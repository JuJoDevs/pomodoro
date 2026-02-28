package com.jujodevs.pomodoro.features.onboarding.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jujodevs.pomodoro.features.onboarding.domain.usecase.SetOnboardingCompletedUseCase
import com.jujodevs.pomodoro.features.onboarding.domain.usecase.UpdateAnalyticsConsentUseCase
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class OnboardingViewModel(
    private val setOnboardingCompleted: SetOnboardingCompletedUseCase,
    private val updateAnalyticsConsent: UpdateAnalyticsConsentUseCase,
) : ViewModel() {
    private val _state = MutableStateFlow(OnboardingState())
    val state = _state.asStateFlow()

    private val _effects = MutableSharedFlow<OnboardingEffect>()
    val effects = _effects.asSharedFlow()

    init {
        _state.update { it.copy(isLoading = false) }
    }

    fun onAction(action: OnboardingAction) {
        viewModelScope.launch {
            when (action) {
                OnboardingAction.NextSlide -> {
                    val currentIndex = _state.value.currentSlideIndex
                    if (currentIndex < TOTAL_SLIDES - 1) {
                        _state.update { it.copy(currentSlideIndex = currentIndex + 1) }
                    }
                }
                is OnboardingAction.UpdateCurrentSlide -> {
                    if (
                        action.index in 0 until TOTAL_SLIDES &&
                        action.index != _state.value.currentSlideIndex
                    ) {
                        _state.update { it.copy(currentSlideIndex = action.index) }
                    }
                }
                is OnboardingAction.ToggleAnalyticsConsent -> {
                    _state.update { it.copy(analyticsConsentChecked = action.enabled) }
                }
                OnboardingAction.CompleteOnboarding -> {
                    updateAnalyticsConsent(_state.value.analyticsConsentChecked)
                    setOnboardingCompleted()
                    _effects.emit(OnboardingEffect.RequestPermissionsAndNavigateToHome)
                }
            }
        }
    }

    private companion object {
        const val TOTAL_SLIDES = 3
    }
}
