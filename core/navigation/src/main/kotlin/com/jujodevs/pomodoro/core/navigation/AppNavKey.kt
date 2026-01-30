package com.jujodevs.pomodoro.core.navigation

import androidx.navigation3.runtime.NavKey

/**
 * Base interface for all navigation keys in the app.
 *
 * All feature navigation keys must extend this interface and be marked
 * with @Serializable for proper state restoration.
 *
 * Example:
 * ```
 * @Serializable
 * sealed interface PomodoroNavKey : AppNavKey {
 *     @Serializable
 *     data object Timer : PomodoroNavKey
 *
 *     @Serializable
 *     data object Settings : PomodoroNavKey
 *
 *     @Serializable
 *     data class SessionDetails(val sessionId: String) : PomodoroNavKey
 * }
 * ```
 */
interface AppNavKey : NavKey
