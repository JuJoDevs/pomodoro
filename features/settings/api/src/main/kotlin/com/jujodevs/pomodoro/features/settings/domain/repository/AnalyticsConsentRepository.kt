package com.jujodevs.pomodoro.features.settings.domain.repository

import kotlinx.coroutines.flow.Flow

/**
 * Repository contract for analytics consent.
 *
 * Implementation will be provided by analytics module when available.
 */
interface AnalyticsConsentRepository {
    /**
     * Observe whether analytics collection is enabled by the user.
     */
    fun observeAnalyticsConsent(): Flow<Boolean>

    /**
     * Update analytics collection consent.
     */
    suspend fun setAnalyticsConsent(enabled: Boolean)
}
