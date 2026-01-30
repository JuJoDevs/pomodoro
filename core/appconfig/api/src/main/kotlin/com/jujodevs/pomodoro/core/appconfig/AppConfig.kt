package com.jujodevs.pomodoro.core.appconfig

/**
 * Provider-agnostic application configuration interface.
 *
 * This interface abstracts access to build-time configuration values.
 * Only the implementation module has access to BuildConfig.
 */
interface AppConfig {
    /**
     * Returns true if the app is running in debug mode, false otherwise.
     */
    val isDebug: Boolean
}
