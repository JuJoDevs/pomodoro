package com.jujodevs.pomodoro.core.designsystem.components.progress

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import com.jujodevs.pomodoro.core.designsystem.theme.PomodoroTheme
import com.jujodevs.pomodoro.core.designsystem.theme.ProgressTrack

/**
 * Pomodoro Progress Bar Component
 *
 * Linear progress indicator with custom styling
 *
 * @param progress Current progress (0.0 to 1.0)
 * @param modifier Modifier for styling
 */
@Composable
fun PomodoroProgressBar(
    progress: Float,
    modifier: Modifier = Modifier,
) {
    LinearProgressIndicator(
        progress = { progress },
        modifier =
            modifier
                .fillMaxWidth()
                .clip(MaterialTheme.shapes.extraSmall),
        color = MaterialTheme.colorScheme.primary,
        trackColor = ProgressTrack,
        strokeCap = androidx.compose.ui.graphics.StrokeCap.Round,
    )
}

@Preview(showBackground = true, backgroundColor = 0xFF1C2834)
@Composable
private fun PomodoroProgressBarPreview() {
    PomodoroTheme {
        PomodoroProgressBar(progress = 0.75f)
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF1C2834)
@Composable
private fun PomodoroProgressBarHalfPreview() {
    PomodoroTheme {
        PomodoroProgressBar(progress = 0.5f)
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF1C2834)
@Composable
private fun PomodoroProgressBarStartPreview() {
    PomodoroTheme {
        PomodoroProgressBar(progress = 0.1f)
    }
}
