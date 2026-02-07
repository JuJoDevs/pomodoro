package com.jujodevs.pomodoro.features.timer.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jujodevs.pomodoro.core.ui.ObserveAsEvents
import org.koin.androidx.compose.koinViewModel

@Composable
fun TimerRoute(
    viewModel: TimerViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    ObserveAsEvents(viewModel.effects) { effect ->
        when (effect) {
            is TimerEffect.ShowMessage -> { /* Handle */ }
        }
    }

    TimerScreen(
        state = state,
        onAction = viewModel::onAction
    )
}
