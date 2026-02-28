package com.jujodevs.pomodoro.features.onboarding.di

import com.jujodevs.pomodoro.features.onboarding.domain.usecase.GetHasCompletedOnboardingUseCase
import com.jujodevs.pomodoro.features.onboarding.domain.usecase.ObserveHasCompletedOnboardingUseCase
import com.jujodevs.pomodoro.features.onboarding.domain.usecase.SetOnboardingCompletedUseCase
import com.jujodevs.pomodoro.features.onboarding.domain.usecase.UpdateAnalyticsConsentUseCase
import com.jujodevs.pomodoro.features.onboarding.presentation.OnboardingViewModel
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val onboardingModule =
    module {
        factoryOf(::GetHasCompletedOnboardingUseCase)
        factoryOf(::ObserveHasCompletedOnboardingUseCase)
        factoryOf(::SetOnboardingCompletedUseCase)
        factoryOf(::UpdateAnalyticsConsentUseCase)

        viewModel {
            OnboardingViewModel(
                setOnboardingCompleted = get(),
                updateAnalyticsConsent = get(),
            )
        }
    }
