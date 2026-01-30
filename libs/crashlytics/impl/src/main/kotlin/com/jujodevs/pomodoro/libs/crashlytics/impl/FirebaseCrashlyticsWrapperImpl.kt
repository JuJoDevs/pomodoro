package com.jujodevs.pomodoro.libs.crashlytics.impl

import com.google.firebase.crashlytics.FirebaseCrashlytics

/**
 * Default implementation of [FirebaseCrashlyticsWrapper] that delegates to Firebase Crashlytics SDK.
 */
internal class FirebaseCrashlyticsWrapperImpl(
    private val firebaseCrashlytics: FirebaseCrashlytics
) : FirebaseCrashlyticsWrapper {

    override fun recordException(throwable: Throwable) {
        firebaseCrashlytics.recordException(throwable)
    }

    override fun setUserId(userId: String) {
        firebaseCrashlytics.setUserId(userId)
    }

    override fun setCustomKey(key: String, value: String) {
        firebaseCrashlytics.setCustomKey(key, value)
    }

    override fun setCustomKey(key: String, value: Int) {
        firebaseCrashlytics.setCustomKey(key, value)
    }

    override fun setCustomKey(key: String, value: Boolean) {
        firebaseCrashlytics.setCustomKey(key, value)
    }

    override fun log(message: String) {
        firebaseCrashlytics.log(message)
    }
}
