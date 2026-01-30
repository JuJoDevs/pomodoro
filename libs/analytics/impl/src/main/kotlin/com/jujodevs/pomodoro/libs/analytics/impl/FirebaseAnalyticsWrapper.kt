package com.jujodevs.pomodoro.libs.analytics.impl

/**
 * Internal wrapper interface for FirebaseAnalytics.
 * This allows proper unit testing without mocking final Firebase classes.
 */
internal interface FirebaseAnalyticsWrapper {
    fun logEvent(name: String, params: Map<String, Any>)
    fun setUserProperty(name: String, value: String)
    fun setUserId(userId: String?)
    fun resetAnalyticsData()
}
