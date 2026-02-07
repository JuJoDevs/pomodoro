package com.jujodevs.pomodoro.features.timer.presentation

sealed interface TimerAction {
    data object Init : TimerAction
}
