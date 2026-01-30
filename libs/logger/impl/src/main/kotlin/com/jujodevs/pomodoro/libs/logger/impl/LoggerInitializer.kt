package com.jujodevs.pomodoro.libs.logger.impl

import timber.log.Timber

/**
 * Initializes Timber logging based on build type.
 *
 * In debug builds, plants a [Timber.DebugTree] that logs to Logcat.
 * In release builds, plants a no-op tree that discards all logs.
 *
 * This must be called in the Application class onCreate() before any logging occurs.
 *
 * @param isDebug true if running in debug mode, false otherwise
 */
object LoggerInitializer {
    fun initialize(isDebug: Boolean) {
        if (isDebug) {
            Timber.plant(Timber.DebugTree())
        } else {
            Timber.plant(object : Timber.Tree() {
                override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
                    // No-op: discard all logs in release builds
                }
            })
        }
    }
}
