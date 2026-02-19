package com.jujodevs.pomodoro.features.settings.presentation

data class SettingsState(
    val alarmSoundLabel: String = "Default",
    val analyticsCollectionEnabled: Boolean = false,
    val canScheduleExactAlarms: Boolean = true,
    val hasNotificationPermission: Boolean = true,
    val isLoading: Boolean = true,
)
