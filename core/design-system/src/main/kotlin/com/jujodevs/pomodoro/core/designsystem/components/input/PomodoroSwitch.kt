package com.jujodevs.pomodoro.core.designsystem.components.input

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.jujodevs.pomodoro.core.designsystem.theme.PomodoroTheme
import com.jujodevs.pomodoro.core.designsystem.theme.ProgressTrack

/**
 * Pomodoro Switch Component
 *
 * Toggle switch with custom styling
 *
 * @param checked Whether switch is checked
 * @param onCheckedChange Callback when switch is toggled
 * @param modifier Modifier for styling
 * @param enabled Whether switch is enabled
 */
@Composable
fun PomodoroSwitch(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    Switch(
        checked = checked,
        onCheckedChange = onCheckedChange,
        modifier = modifier,
        enabled = enabled,
        colors =
            SwitchDefaults.colors(
                checkedThumbColor = MaterialTheme.colorScheme.onPrimary,
                checkedTrackColor = MaterialTheme.colorScheme.primary,
                uncheckedThumbColor = MaterialTheme.colorScheme.onPrimary,
                uncheckedTrackColor = ProgressTrack,
            ),
    )
}

@Preview(showBackground = true, backgroundColor = 0xFF1C2834)
@Composable
private fun PomodoroSwitchCheckedPreview() {
    PomodoroTheme {
        PomodoroSwitch(
            checked = true,
            onCheckedChange = {},
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF1C2834)
@Composable
private fun PomodoroSwitchUncheckedPreview() {
    PomodoroTheme {
        PomodoroSwitch(
            checked = false,
            onCheckedChange = {},
        )
    }
}
