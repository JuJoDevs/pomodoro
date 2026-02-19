package com.jujodevs.pomodoro.features.settings.presentation

sealed interface SettingsEffect {
    data object OpenNotificationChannelSettings : SettingsEffect

    data object GrantExactAlarmPermission : SettingsEffect

    data object RequestNotificationPermission : SettingsEffect
}
