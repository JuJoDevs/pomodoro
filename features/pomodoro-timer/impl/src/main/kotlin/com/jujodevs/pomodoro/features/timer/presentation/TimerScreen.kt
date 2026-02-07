package com.jujodevs.pomodoro.features.timer.presentation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun TimerScreen(
    state: TimerState,
    onAction: (TimerAction) -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Pomodoro Timer Feature (Loading: ${state.isLoading})",
            modifier = Modifier.clickable { onAction(TimerAction.Init) }
        )
    }
}
