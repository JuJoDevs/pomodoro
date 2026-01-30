package com.jujodevs.pomodoro.libs.crashlytics.impl

import com.jujodevs.pomodoro.libs.crashlytics.CrashReporter
import com.jujodevs.pomodoro.libs.logger.Logger

/**
 * Firebase Crashlytics implementation of [CrashReporter].
 *
 * Note: Firebase Crashlytics automatically captures uncaught exceptions and ANRs at the SDK level.
 * This implementation handles manual exception reporting for caught exceptions.
 */
internal class FirebaseCrashReporter(
    private val firebaseCrashlytics: FirebaseCrashlyticsWrapper,
    private val logger: Logger
) : CrashReporter {

    override fun recordException(throwable: Throwable) {
        try {
            firebaseCrashlytics.recordException(throwable)
            logger.d(TAG, "Exception recorded: ${throwable.message}")
        } catch (e: Exception) {
            logger.e(TAG, "Failed to record exception", e)
        }
    }

    override fun setUserId(userId: String) {
        try {
            firebaseCrashlytics.setUserId(userId)
            logger.d(TAG, "User ID set: $userId")
        } catch (e: Exception) {
            logger.e(TAG, "Failed to set user ID", e)
        }
    }

    override fun setCustomKey(key: String, value: String) {
        try {
            firebaseCrashlytics.setCustomKey(key, value)
            logger.d(TAG, "Custom key set: $key = $value")
        } catch (e: Exception) {
            logger.e(TAG, "Failed to set custom key: $key", e)
        }
    }

    override fun setCustomKey(key: String, value: Int) {
        try {
            firebaseCrashlytics.setCustomKey(key, value)
            logger.d(TAG, "Custom key set: $key = $value")
        } catch (e: Exception) {
            logger.e(TAG, "Failed to set custom key: $key", e)
        }
    }

    override fun setCustomKey(key: String, value: Boolean) {
        try {
            firebaseCrashlytics.setCustomKey(key, value)
            logger.d(TAG, "Custom key set: $key = $value")
        } catch (e: Exception) {
            logger.e(TAG, "Failed to set custom key: $key", e)
        }
    }

    override fun log(message: String) {
        try {
            firebaseCrashlytics.log(message)
            logger.d(TAG, "Log message: $message")
        } catch (e: Exception) {
            logger.e(TAG, "Failed to log message", e)
        }
    }

    private companion object {
        const val TAG = "Crashlytics"
    }
}
