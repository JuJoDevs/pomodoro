package com.jujodevs.pomodoro.libs.logger

/**
 * Provider-agnostic logging interface.
 *
 * All modules should use this interface instead of direct logging library dependencies.
 * This allows easy swapping of logging implementations and ensures logs only appear in debug builds.
 */
interface Logger {
    /**
     * Log a debug message.
     *
     * @param tag Tag to identify the source of the log message
     * @param message The message to log
     * @param throwable Optional throwable to log
     */
    fun d(
        tag: String,
        message: String,
        throwable: Throwable? = null,
    )

    /**
     * Log an info message.
     *
     * @param tag Tag to identify the source of the log message
     * @param message The message to log
     * @param throwable Optional throwable to log
     */
    fun i(
        tag: String,
        message: String,
        throwable: Throwable? = null,
    )

    /**
     * Log a warning message.
     *
     * @param tag Tag to identify the source of the log message
     * @param message The message to log
     * @param throwable Optional throwable to log
     */
    fun w(
        tag: String,
        message: String,
        throwable: Throwable? = null,
    )

    /**
     * Log an error message.
     *
     * @param tag Tag to identify the source of the log message
     * @param message The message to log
     * @param throwable Optional throwable to log
     */
    fun e(
        tag: String,
        message: String,
        throwable: Throwable? = null,
    )
}
