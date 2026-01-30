package com.jujodevs.pomodoro.core.designsystem.components.surface

import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.jujodevs.pomodoro.core.designsystem.theme.DividerColor
import com.jujodevs.pomodoro.core.designsystem.theme.PomodoroTheme

/**
 * Pomodoro Divider Component
 *
 * Horizontal divider with custom styling
 *
 * @param modifier Modifier for styling
 */
@Composable
fun PomodoroDivider(
    modifier: Modifier = Modifier
) {
    HorizontalDivider(
        modifier = modifier,
        thickness = 1.dp,
        color = DividerColor
    )
}

@Preview(showBackground = true, backgroundColor = 0xFF1C2834)
@Composable
private fun PomodoroDividerPreview() {
    PomodoroTheme {
        PomodoroDivider()
    }
}
