package com.jujodevs.pomodoro.features.timer.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jujodevs.pomodoro.core.resources.R
import com.jujodevs.pomodoro.core.ui.ObserveAsEvents
import com.jujodevs.pomodoro.core.ui.UiText
import com.jujodevs.pomodoro.features.timer.domain.model.PomodoroPhase
import org.koin.androidx.compose.koinViewModel

@Composable
fun TimerRoute(
    onNavigateToSettings: () -> Unit,
    onPhaseChanged: (Int) -> Unit,
    onShowMessage: (UiText) -> Unit,
    onRequestExactAlarmPermission: () -> Unit,
    exactAlarmPermissionGranted: Boolean? = null,
    viewModel: TimerViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(exactAlarmPermissionGranted) {
        exactAlarmPermissionGranted?.let { isGranted ->
            viewModel.onAction(TimerAction.UpdateExactAlarmPermission(isGranted))
        }
    }

    LaunchedEffect(state.phase) {
        val titleResId =
            when (state.phase) {
                PomodoroPhase.WORK -> R.string.phase_title_focus
                PomodoroPhase.SHORT_BREAK -> R.string.phase_title_short_break
                PomodoroPhase.LONG_BREAK -> R.string.phase_title_long_break
            }
        onPhaseChanged(titleResId)
    }

    ObserveAsEvents(viewModel.effects) { effect ->
        when (effect) {
            is TimerEffect.ShowMessage -> {
                onShowMessage(effect.message)
            }
            TimerEffect.NavigateToSettings -> onNavigateToSettings()
            TimerEffect.RequestExactAlarmPermission -> onRequestExactAlarmPermission()
        }
    }

    TimerScreen(
        state = state,
        onAction = viewModel::onAction,
    )
}
