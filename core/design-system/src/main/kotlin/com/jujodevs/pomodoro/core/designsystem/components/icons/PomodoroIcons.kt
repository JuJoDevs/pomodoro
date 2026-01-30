package com.jujodevs.pomodoro.core.designsystem.components.icons

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.automirrored.outlined.HelpOutline
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.outlined.HelpOutline
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * Pomodoro Icons
 *
 * Centralized icon constants for the Pomodoro app
 * All icons are from Material Icons
 */
object PomodoroIcons {
    // Navigation
    val Settings: ImageVector = Icons.Default.Settings
    val Back: ImageVector = Icons.AutoMirrored.Filled.ArrowBack
    val Close: ImageVector = Icons.Default.Close
    val Help: ImageVector = Icons.AutoMirrored.Outlined.HelpOutline
    val More: ImageVector = Icons.Default.MoreVert

    // Timer
    val Timer: ImageVector = Icons.Default.Timer
    val Play: ImageVector = Icons.Default.PlayArrow
    val Pause: ImageVector = Icons.Default.Pause
    val Reset: ImageVector = Icons.Default.Refresh

    // Stats & History
    val Stats: ImageVector = Icons.Default.BarChart
    val History: ImageVector = Icons.Default.History
    val Check: ImageVector = Icons.Default.CheckCircle
    val Flame: ImageVector = Icons.Default.LocalFireDepartment

    // Settings
    val Sound: ImageVector = Icons.AutoMirrored.Filled.VolumeUp
    val Vibration: ImageVector = Icons.Default.Vibration

    // Actions
    val Share: ImageVector = Icons.Default.Share
    val ChevronDown: ImageVector = Icons.Default.KeyboardArrowDown
}
