package com.jujodevs.pomodoro.core.appconfig.impl

import com.jujodevs.pomodoro.core.appconfig.AppConfig

/**
 * Implementation of [AppConfig] that accesses BuildConfig.
 *
 * This is the ONLY module in the project that has direct access to BuildConfig.
 */
internal class AppConfigImpl : AppConfig {
    override val isDebug: Boolean
        get() = BuildConfig.DEBUG
}
