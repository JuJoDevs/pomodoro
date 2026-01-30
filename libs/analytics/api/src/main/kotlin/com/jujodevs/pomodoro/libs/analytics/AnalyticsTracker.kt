package com.jujodevs.pomodoro.libs.analytics

/**
 * Provider-agnostic analytics tracking interface.
 *
 * All modules should use this interface instead of direct analytics SDK dependencies.
 * This allows easy swapping of analytics providers and ensures no direct dependencies leak into features.
 */
interface AnalyticsTracker {
    /**
     * Track an analytics event.
     *
     * @param event The event to track
     */
    fun track(event: AnalyticsEvent)

    /**
     * Set a user property.
     *
     * @param property The user property to set
     */
    fun setUserProperty(property: UserProperty)

    /**
     * Set the user ID for analytics.
     *
     * @param userId The user ID, or null to clear it
     */
    fun setUserId(userId: String?)

    /**
     * Reset analytics data (clears user properties and user ID).
     * Useful for logout or privacy compliance.
     */
    fun resetAnalyticsData()
}
