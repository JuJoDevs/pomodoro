package com.jujodevs.pomodoro.features.timer.presentation

sealed interface TimerEffect {
    data class ShowMessage(val message: String) : TimerEffect
    data object NavigateToSettings : TimerEffect
    data object RequestExactAlarmPermission : TimerEffect
}
