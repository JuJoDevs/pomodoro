package com.jujodevs.pomodoro.core.designsystem.components.progress

import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.jujodevs.pomodoro.core.designsystem.theme.PomodoroTheme
import com.jujodevs.pomodoro.core.designsystem.theme.ProgressTrack

/**
 * Pomodoro Progress Indicator Component
 *
 * Circular progress indicator with custom styling
 *
 * @param modifier Modifier for styling
 * @param progress Current progress (0.0 to 1.0), null for indeterminate
 */
@Composable
fun PomodoroProgressIndicator(
    modifier: Modifier = Modifier,
    progress: Float? = null,
) {
    if (progress != null) {
        CircularProgressIndicator(
            progress = { progress },
            modifier = modifier,
            color = MaterialTheme.colorScheme.primary,
            strokeWidth = 4.dp,
            trackColor = ProgressTrack,
        )
    } else {
        CircularProgressIndicator(
            modifier = modifier,
            color = MaterialTheme.colorScheme.primary,
            strokeWidth = 4.dp,
            trackColor = ProgressTrack,
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF1C2834)
@Composable
private fun PomodoroProgressIndicatorPreview() {
    PomodoroTheme {
        PomodoroProgressIndicator(progress = 0.75f)
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF1C2834)
@Composable
private fun PomodoroProgressIndicatorIndeterminatePreview() {
    PomodoroTheme {
        PomodoroProgressIndicator()
    }
}
