package com.jujodevs.pomodoro.features.timer.presentation

import com.jujodevs.pomodoro.core.ui.UiText

sealed interface TimerEffect {
    data class ShowMessage(val message: UiText) : TimerEffect
    data object NavigateToSettings : TimerEffect
    data object RequestExactAlarmPermission : TimerEffect
}
