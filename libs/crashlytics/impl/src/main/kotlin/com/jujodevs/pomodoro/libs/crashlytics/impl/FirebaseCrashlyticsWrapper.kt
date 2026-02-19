package com.jujodevs.pomodoro.libs.crashlytics.impl

/**
 * Internal wrapper interface for FirebaseCrashlytics.
 * This allows proper unit testing without mocking final Firebase classes.
 */
internal interface FirebaseCrashlyticsWrapper {
    fun recordException(throwable: Throwable)

    fun setUserId(userId: String)

    fun setCustomKey(
        key: String,
        value: String,
    )

    fun setCustomKey(
        key: String,
        value: Int,
    )

    fun setCustomKey(
        key: String,
        value: Boolean,
    )

    fun log(message: String)
}
