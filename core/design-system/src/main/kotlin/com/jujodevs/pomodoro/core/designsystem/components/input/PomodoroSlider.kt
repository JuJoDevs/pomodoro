package com.jujodevs.pomodoro.core.designsystem.components.input

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.jujodevs.pomodoro.core.designsystem.theme.PomodoroTheme
import com.jujodevs.pomodoro.core.designsystem.theme.ProgressTrack

/**
 * Pomodoro Slider Component
 *
 * Slider for value selection with custom styling
 *
 * @param value Current value
 * @param onValueChange Callback when value changes
 * @param modifier Modifier for styling
 * @param valueRange Range of values (default 0f..1f)
 * @param steps Number of discrete steps (0 for continuous)
 * @param enabled Whether slider is enabled
 */
@Composable
fun PomodoroSlider(
    value: Float,
    onValueChange: (Float) -> Unit,
    modifier: Modifier = Modifier,
    valueRange: ClosedFloatingPointRange<Float> = 0f..1f,
    steps: Int = 0,
    enabled: Boolean = true
) {
    Slider(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        enabled = enabled,
        valueRange = valueRange,
        steps = steps,
        colors = SliderDefaults.colors(
            thumbColor = MaterialTheme.colorScheme.onPrimary,
            activeTrackColor = MaterialTheme.colorScheme.primary,
            inactiveTrackColor = ProgressTrack,
        )
    )
}

@Preview(showBackground = true, backgroundColor = 0xFF1C2834)
@Composable
private fun PomodoroSliderPreview() {
    PomodoroTheme {
        PomodoroSlider(
            value = 0.75f,
            onValueChange = {}
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF1C2834)
@Composable
private fun PomodoroSliderHalfPreview() {
    PomodoroTheme {
        PomodoroSlider(
            value = 0.5f,
            onValueChange = {}
        )
    }
}
