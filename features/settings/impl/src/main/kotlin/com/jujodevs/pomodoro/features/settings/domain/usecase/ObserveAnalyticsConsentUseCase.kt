package com.jujodevs.pomodoro.features.settings.domain.usecase

import com.jujodevs.pomodoro.features.settings.domain.repository.AnalyticsConsentRepository
import kotlinx.coroutines.flow.Flow

class ObserveAnalyticsConsentUseCase(
    private val repository: AnalyticsConsentRepository
) {
    operator fun invoke(): Flow<Boolean> {
        return repository.observeAnalyticsConsent()
    }
}
