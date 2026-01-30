package com.jujodevs.pomodoro.libs.notifications

/**
 * Interface for scheduling and managing notifications.
 *
 * Provides functionality to:
 * - Schedule notifications at specific times
 * - Cancel individual or all notifications
 * - Check if a notification is scheduled
 */
interface NotificationScheduler {
    /**
     * Schedule a notification to be shown at the specified time.
     *
     * @param notification The notification data to schedule
     * @return Result indicating success or failure
     */
    suspend fun scheduleNotification(notification: NotificationData): Result<Unit>

    /**
     * Cancel a scheduled notification by its ID.
     *
     * @param notificationId The ID of the notification to cancel
     * @return Result indicating success or failure
     */
    suspend fun cancelNotification(notificationId: Int): Result<Unit>

    /**
     * Cancel all scheduled notifications.
     *
     * @return Result indicating success or failure
     */
    suspend fun cancelAllNotifications(): Result<Unit>

    /**
     * Check if a notification with the given ID is currently scheduled.
     *
     * @param notificationId The ID to check
     * @return true if the notification is scheduled, false otherwise
     */
    fun isNotificationScheduled(notificationId: Int): Boolean
}
