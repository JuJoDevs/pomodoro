package com.jujodevs.pomodoro.libs.crashlytics

/**
 * Provider-agnostic crash reporting interface.
 *
 * All modules should use this interface instead of direct crash reporting SDK dependencies.
 * This allows easy swapping of crash reporting providers.
 *
 * Note: Firebase Crashlytics automatically captures uncaught exceptions and ANRs at the SDK level.
 * This interface is primarily for manual exception reporting (caught exceptions we want to track).
 */
interface CrashReporter {
    /**
     * Record an exception for crash reporting.
     *
     * @param throwable The exception to record
     */
    fun recordException(throwable: Throwable)

    /**
     * Set the user ID for crash reports.
     * Use an empty string to clear the user ID.
     *
     * @param userId The user ID
     */
    fun setUserId(userId: String)

    /**
     * Set a custom key-value pair for crash reports.
     *
     * @param key The key
     * @param value The string value
     */
    fun setCustomKey(
        key: String,
        value: String,
    )

    /**
     * Set a custom key-value pair for crash reports.
     *
     * @param key The key
     * @param value The integer value
     */
    fun setCustomKey(
        key: String,
        value: Int,
    )

    /**
     * Set a custom key-value pair for crash reports.
     *
     * @param key The key
     * @param value The boolean value
     */
    fun setCustomKey(
        key: String,
        value: Boolean,
    )

    /**
     * Log a message for crash reports (useful for breadcrumbs/debugging context).
     *
     * @param message The message to log
     */
    fun log(message: String)
}
