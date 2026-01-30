package com.jujodevs.pomodoro.libs.analytics

/**
 * No-op implementation of [AnalyticsTracker] for testing.
 *
 * Use this in tests to avoid actual analytics tracking and to verify tracking calls.
 */
class FakeAnalyticsTracker : AnalyticsTracker {
    private val trackedEvents = mutableListOf<AnalyticsEvent>()
    private val userProperties = mutableMapOf<String, Any>()
    private var userId: String? = null

    override fun track(event: AnalyticsEvent) {
        trackedEvents.add(event)
    }

    override fun setUserProperty(property: UserProperty) {
        userProperties[property.key] = property.value
    }

    override fun setUserId(userId: String?) {
        this.userId = userId
    }

    override fun resetAnalyticsData() {
        userProperties.clear()
        userId = null
    }

    /**
     * Get all tracked events (for test verification).
     */
    fun getTrackedEvents(): List<AnalyticsEvent> = trackedEvents.toList()

    /**
     * Get user properties (for test verification).
     */
    fun getUserProperties(): Map<String, Any> = userProperties.toMap()

    /**
     * Get current user ID (for test verification).
     */
    fun getUserId(): String? = userId

    /**
     * Clear all tracked data (useful for test cleanup).
     */
    fun clear() {
        trackedEvents.clear()
        userProperties.clear()
        userId = null
    }
}
