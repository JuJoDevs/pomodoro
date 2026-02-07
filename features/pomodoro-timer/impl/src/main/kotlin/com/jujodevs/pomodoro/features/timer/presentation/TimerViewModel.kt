package com.jujodevs.pomodoro.features.timer.presentation

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow

class TimerViewModel : ViewModel() {
    private val _state = MutableStateFlow(TimerState())
    val state = _state.asStateFlow()

    private val _effects = MutableSharedFlow<TimerEffect>()
    val effects = _effects.asSharedFlow()

    fun onAction(action: TimerAction) {
        when (action) {
            TimerAction.Init -> { /* Do nothing for now */ }
        }
    }
}
