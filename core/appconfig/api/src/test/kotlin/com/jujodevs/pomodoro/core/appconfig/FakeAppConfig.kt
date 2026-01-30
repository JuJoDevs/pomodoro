package com.jujodevs.pomodoro.core.appconfig

/**
 * Fake implementation of [AppConfig] for testing.
 */
class FakeAppConfig(
    override val isDebug: Boolean = false,
) : AppConfig
