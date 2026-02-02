package com.jujodevs.pomodoro.libs.datastore

/**
 * Centralized definition of preference keys used throughout the app.
 *
 * All keys should be defined here to avoid typos and ensure consistency.
 * These keys are typically for user-configurable settings.
 */
object PreferencesKeys {
    // Pomodoro configuration
    const val WORK_DURATION_MINUTES = "work_duration_minutes"
    const val SHORT_BREAK_DURATION_MINUTES = "short_break_duration_minutes"
    const val LONG_BREAK_DURATION_MINUTES = "long_break_duration_minutes"
    const val SESSIONS_UNTIL_LONG_BREAK = "sessions_until_long_break"

    // App settings
    const val SOUND_ENABLED = "sound_enabled"
    const val VIBRATION_ENABLED = "vibration_enabled"
    const val AUTO_START_BREAKS = "auto_start_breaks"
    const val AUTO_START_POMODOROS = "auto_start_pomodoros"

    // User preferences
    const val THEME_MODE = "theme_mode"
    const val NOTIFICATIONS_ENABLED = "notifications_enabled"
}
