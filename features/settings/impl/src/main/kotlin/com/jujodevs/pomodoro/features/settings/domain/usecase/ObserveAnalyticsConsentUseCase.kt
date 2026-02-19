package com.jujodevs.pomodoro.features.settings.domain.usecase

import com.jujodevs.pomodoro.libs.analytics.AnalyticsCollectionManager
import kotlinx.coroutines.flow.Flow

class ObserveAnalyticsConsentUseCase(
    private val analyticsCollectionManager: AnalyticsCollectionManager,
) {
    operator fun invoke(): Flow<Boolean> = analyticsCollectionManager.observeAnalyticsEnabled()
}
