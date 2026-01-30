package com.jujodevs.pomodoro.libs.notifications

/**
 * Fake implementation of NotificationScheduler for testing purposes.
 *
 * Uses in-memory storage to track scheduled notifications.
 */
class FakeNotificationScheduler : NotificationScheduler {
    private val scheduledNotifications = mutableMapOf<Int, NotificationData>()

    override suspend fun scheduleNotification(notification: NotificationData): Result<Unit> {
        scheduledNotifications[notification.id] = notification
        return Result.success(Unit)
    }

    override suspend fun cancelNotification(notificationId: Int): Result<Unit> {
        scheduledNotifications.remove(notificationId)
        return Result.success(Unit)
    }

    override suspend fun cancelAllNotifications(): Result<Unit> {
        scheduledNotifications.clear()
        return Result.success(Unit)
    }

    override fun isNotificationScheduled(notificationId: Int): Boolean =
        scheduledNotifications.containsKey(notificationId)

    /**
     * Get all currently scheduled notifications.
     * Useful for testing purposes.
     */
    fun getScheduledNotifications(): List<NotificationData> = scheduledNotifications.values.toList()
}
