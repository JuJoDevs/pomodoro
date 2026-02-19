package com.jujodevs.pomodoro.core.designsystem.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider

/**
 * Pomodoro Theme
 * Main theme composable that wraps all theme providers
 *
 * @param darkTheme Currently always true as the app uses dark theme only
 * @param content The composable content to be themed
 */
@Composable
fun PomodoroTheme(
    darkTheme: Boolean = true,
    content: @Composable () -> Unit,
) {
    val colorScheme = if (darkTheme) PomodoroDarkColors else PomodoroDarkColors

    CompositionLocalProvider(LocalSpacing provides Spacing()) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = PomodoroTypography,
            shapes = PomodoroShapes,
            content = content,
        )
    }
}
