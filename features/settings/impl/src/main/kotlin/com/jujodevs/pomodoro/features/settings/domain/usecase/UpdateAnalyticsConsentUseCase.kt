package com.jujodevs.pomodoro.features.settings.domain.usecase

import com.jujodevs.pomodoro.features.settings.domain.repository.AnalyticsConsentRepository

class UpdateAnalyticsConsentUseCase(
    private val repository: AnalyticsConsentRepository
) {
    suspend operator fun invoke(enabled: Boolean) {
        repository.setAnalyticsConsent(enabled)
    }
}
