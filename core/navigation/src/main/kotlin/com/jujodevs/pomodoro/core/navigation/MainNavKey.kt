package com.jujodevs.pomodoro.core.navigation

import kotlinx.serialization.Serializable

/**
 * Main navigation keys for the app.
 *
 * These are the top-level destinations in the application.
 */
@Serializable
sealed interface MainNavKey : AppNavKey {
    /**
     * Onboarding screen - first-run flow with slides and consent
     */
    @Serializable
    data object Onboarding : MainNavKey

    /**
     * Home screen - main Pomodoro timer screen
     */
    @Serializable
    data object Home : MainNavKey

    /**
     * Settings screen - app configuration
     */
    @Serializable
    data object Settings : MainNavKey

    /**
     * Statistics screen - session history and statistics
     */
    @Serializable
    data object Statistics : MainNavKey
}
