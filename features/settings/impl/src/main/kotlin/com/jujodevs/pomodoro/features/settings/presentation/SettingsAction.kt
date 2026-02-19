package com.jujodevs.pomodoro.features.settings.presentation

sealed interface SettingsAction {
    data class ToggleAnalyticsCollection(
        val enabled: Boolean,
    ) : SettingsAction

    data object OpenNotificationChannelSettings : SettingsAction

    data object GrantExactAlarmPermission : SettingsAction

    data object RequestNotificationPermission : SettingsAction
}
