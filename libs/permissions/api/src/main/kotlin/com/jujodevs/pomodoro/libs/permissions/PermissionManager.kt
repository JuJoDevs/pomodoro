package com.jujodevs.pomodoro.libs.permissions

/**
 * Interface for managing Android runtime permissions.
 */
interface PermissionManager {
    /**
     * Checks if notification permission is granted.
     */
    fun hasNotificationPermission(): Boolean

    /**
     * Checks if the app can schedule exact alarms.
     */
    fun canScheduleExactAlarms(): Boolean

    /**
     * Returns the required notification permission string if applicable.
     */
    fun getNotificationPermissionString(): String?
}
