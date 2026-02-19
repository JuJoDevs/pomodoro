@file:Suppress("MagicNumber")

package com.jujodevs.pomodoro.core.designsystem.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.ui.graphics.Color

// Background colors
val BackgroundDark = Color(0xFF1C2834)
val SurfaceDark = Color(0xFF263847)
val SurfaceVariantDark = Color(0xFF2C3E50)

// Primary colors
val PrimaryBlue = Color(0xFF2196F3)
val PrimaryBlueDark = Color(0xFF1976D2)

// Accent colors
val AccentOrange = Color(0xFFFF6B35)

// Text colors
val TextPrimary = Color(0xFFFFFFFF)
val TextSecondary = Color(0xFF8B97A5)
val TextTertiary = Color(0xFF5E6A78)

// Component colors
val ProgressTrack = Color(0xFF3A4A5A)
val DividerColor = Color(0xFF3A4A5A)

/**
 * Pomodoro Dark Color Scheme
 * Maps Material3 color tokens to the Pomodoro design system colors
 */
val PomodoroDarkColors =
    darkColorScheme(
        primary = PrimaryBlue,
        onPrimary = TextPrimary,
        primaryContainer = PrimaryBlueDark,
        onPrimaryContainer = TextPrimary,
        secondary = TextSecondary,
        onSecondary = TextPrimary,
        secondaryContainer = SurfaceVariantDark,
        onSecondaryContainer = TextPrimary,
        tertiary = AccentOrange,
        onTertiary = TextPrimary,
        background = BackgroundDark,
        onBackground = TextPrimary,
        surface = SurfaceDark,
        onSurface = TextPrimary,
        surfaceVariant = SurfaceVariantDark,
        onSurfaceVariant = TextSecondary,
        outline = DividerColor,
        outlineVariant = ProgressTrack,
        error = Color(0xFFCF6679),
        onError = Color(0xFF000000),
    )
