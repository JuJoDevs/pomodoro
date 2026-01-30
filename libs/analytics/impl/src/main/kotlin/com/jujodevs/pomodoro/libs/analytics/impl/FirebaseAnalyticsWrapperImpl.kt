package com.jujodevs.pomodoro.libs.analytics.impl

import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics

/**
 * Default implementation of [FirebaseAnalyticsWrapper] that delegates to Firebase Analytics SDK.
 */
internal class FirebaseAnalyticsWrapperImpl(
    private val firebaseAnalytics: FirebaseAnalytics
) : FirebaseAnalyticsWrapper {

    override fun logEvent(name: String, params: Map<String, Any>) {
        val bundle = params.toBundle()
        firebaseAnalytics.logEvent(name, bundle)
    }

    override fun setUserProperty(name: String, value: String) {
        firebaseAnalytics.setUserProperty(name, value)
    }

    override fun setUserId(userId: String?) {
        firebaseAnalytics.setUserId(userId)
    }

    override fun resetAnalyticsData() {
        firebaseAnalytics.resetAnalyticsData()
    }

    private fun Map<String, Any>.toBundle(): Bundle {
        val bundle = Bundle()
        forEach { (key, value) ->
            when (value) {
                is String -> bundle.putString(key, value)
                is Int -> bundle.putInt(key, value)
                is Long -> bundle.putLong(key, value)
                is Double -> bundle.putDouble(key, value)
                is Boolean -> bundle.putBoolean(key, value)
            }
        }
        return bundle
    }
}
