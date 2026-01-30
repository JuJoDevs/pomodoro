package com.jujodevs.pomodoro.libs.analytics.impl

import com.jujodevs.pomodoro.libs.analytics.AnalyticsEvent
import com.jujodevs.pomodoro.libs.analytics.AnalyticsTracker
import com.jujodevs.pomodoro.libs.analytics.UserProperty
import com.jujodevs.pomodoro.libs.logger.Logger

/**
 * Firebase Analytics implementation of [AnalyticsTracker].
 */
internal class FirebaseAnalyticsTracker(
    private val firebaseAnalytics: FirebaseAnalyticsWrapper,
    private val logger: Logger
) : AnalyticsTracker {

    override fun track(event: AnalyticsEvent) {
        try {
            val filteredParams = event.parameters.filterSupportedTypes()
            firebaseAnalytics.logEvent(event.name, filteredParams)
            logger.d(TAG, "Event tracked: ${event.name}")
        } catch (e: Exception) {
            logger.e(TAG, "Failed to track event: ${event.name}", e)
        }
    }

    override fun setUserProperty(property: UserProperty) {
        try {
            val value = when (val propValue = property.value) {
                is String -> propValue
                is Int -> propValue.toString()
                is Long -> propValue.toString()
                is Double -> propValue.toString()
                is Boolean -> propValue.toString()
                else -> {
                    logger.w(TAG, "Unsupported user property type: ${propValue::class.simpleName}")
                    return
                }
            }
            firebaseAnalytics.setUserProperty(property.key, value)
            logger.d(TAG, "User property set: ${property.key} = $value")
        } catch (e: Exception) {
            logger.e(TAG, "Failed to set user property: ${property.key}", e)
        }
    }

    override fun setUserId(userId: String?) {
        try {
            firebaseAnalytics.setUserId(userId)
            logger.d(TAG, "User ID set: ${userId ?: "null"}")
        } catch (e: Exception) {
            logger.e(TAG, "Failed to set user ID", e)
        }
    }

    override fun resetAnalyticsData() {
        try {
            firebaseAnalytics.resetAnalyticsData()
            logger.d(TAG, "Analytics data reset")
        } catch (e: Exception) {
            logger.e(TAG, "Failed to reset analytics data", e)
        }
    }

    private fun Map<String, Any>.filterSupportedTypes(): Map<String, Any> {
        return filter { (key, value) ->
            val isSupported = value is String || value is Int || value is Long || value is Double || value is Boolean
            if (!isSupported) {
                logger.w(TAG, "Unsupported parameter type for key '$key': ${value::class.simpleName}", null)
            }
            isSupported
        }
    }

    private companion object {
        const val TAG = "Analytics"
    }
}
