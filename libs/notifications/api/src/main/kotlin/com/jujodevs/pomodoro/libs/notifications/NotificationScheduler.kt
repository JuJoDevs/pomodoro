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

    /**
     * Show or update a persistent notification (foreground service style, but without service if not needed).
     *
     * @param notification The notification data
     * @return Result indicating success or failure
     */
    suspend fun showPersistentNotification(notification: NotificationData): Result<Unit>

    /**
     * Dismiss the persistent notification.
     *
     * @param notificationId The ID of the notification to dismiss
     * @return Result indicating success or failure
     */
    suspend fun dismissPersistentNotification(notificationId: Int): Result<Unit>

    /**
     * Start or update the running timer foreground service notification.
     *
     * @param notification The running timer notification data
     * @return Result indicating success or failure
     */
    suspend fun startRunningForegroundTimer(notification: RunningTimerNotificationData): Result<Unit>

    /**
     * Stop the running timer foreground service notification.
     *
     * @return Result indicating success or failure
     */
    suspend fun stopRunningForegroundTimer(): Result<Unit>
}
