package com.jujodevs.pomodoro.features.settings.domain.usecase

import com.jujodevs.pomodoro.libs.analytics.AnalyticsCollectionManager

class UpdateAnalyticsConsentUseCase(
    private val analyticsCollectionManager: AnalyticsCollectionManager,
) {
    suspend operator fun invoke(enabled: Boolean) {
        analyticsCollectionManager.setAnalyticsEnabled(enabled)
    }
}
