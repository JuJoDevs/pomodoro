package com.jujodevs.pomodoro.features.timer.presentation

sealed interface TimerAction {
    data class SelectWorkDuration(
        val minutes: Int,
    ) : TimerAction

    data class SelectShortBreakDuration(
        val minutes: Int,
    ) : TimerAction

    data class ToggleAutoStartBreaks(
        val enabled: Boolean,
    ) : TimerAction

    data class ToggleAutoStartWork(
        val enabled: Boolean,
    ) : TimerAction

    data object Start : TimerAction

    data object Pause : TimerAction

    data object Resume : TimerAction

    data object Skip : TimerAction

    data object Stop : TimerAction

    data object Reset : TimerAction

    data object ConfirmStop : TimerAction

    data object ConfirmReset : TimerAction

    data object DismissDialog : TimerAction

    data object DismissExactAlarmWarning : TimerAction

    data object RequestExactAlarmPermission : TimerAction

    data class UpdateExactAlarmPermission(
        val isGranted: Boolean,
    ) : TimerAction
}
