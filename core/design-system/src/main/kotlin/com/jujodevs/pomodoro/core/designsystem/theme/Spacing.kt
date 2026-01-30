package com.jujodevs.pomodoro.core.designsystem.theme

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Pomodoro Spacing System
 * Provides consistent spacing values throughout the app
 */
data class Spacing(
    val spaceXXS: Dp = 4.dp,
    val spaceXS: Dp = 8.dp,
    val spaceS: Dp = 12.dp,
    val spaceM: Dp = 16.dp,
    val spaceL: Dp = 20.dp,
    val spaceXL: Dp = 24.dp,
    val spaceXXL: Dp = 32.dp,
    val spaceXXXL: Dp = 48.dp,
)

/**
 * CompositionLocal for accessing spacing values throughout the app
 * Usage: val spacing = LocalSpacing.current
 */
val LocalSpacing = staticCompositionLocalOf { Spacing() }
