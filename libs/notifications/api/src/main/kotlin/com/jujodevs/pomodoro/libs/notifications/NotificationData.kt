package com.jujodevs.pomodoro.libs.notifications

/**
 * Data class representing a notification to be scheduled.
 *
 * @param id Unique identifier for the notification
 * @param title Title text displayed in the notification
 * @param message Message text displayed in the notification
 * @param channelId ID of the notification channel
 * @param scheduledTimeMillis Timestamp in milliseconds when notification should be shown
 * @param type Type of notification for categorization
 */
data class NotificationData(
    val id: Int,
    val title: String,
    val message: String,
    val channelId: String,
    val scheduledTimeMillis: Long,
    val type: NotificationType,
)

/**
 * Enum representing different types of notifications.
 */
enum class NotificationType {
    WORK_SESSION_COMPLETE,
    SHORT_BREAK_COMPLETE,
    LONG_BREAK_COMPLETE,
    SESSION_REMINDER,
}

/**
 * Sealed class representing notification channels.
 *
 * Each channel has its own importance level and description.
 */
sealed class NotificationChannel(
    val id: String,
    val name: String,
    val description: String,
    val importance: Int,
) {
    /**
     * Channel for Pomodoro session completion notifications.
     * Uses high importance to ensure user is notified even when device is idle.
     */
    data object PomodoroSession : NotificationChannel(
        id = "pomodoro_session",
        name = "Pomodoro Sessions",
        description = "Notifications for pomodoro session completion",
        importance = 4, // IMPORTANCE_HIGH
    )

    /**
     * Channel for reminder notifications.
     * Uses default importance for less critical reminders.
     */
    data object Reminders : NotificationChannel(
        id = "reminders",
        name = "Reminders",
        description = "Reminder notifications",
        importance = 3, // IMPORTANCE_DEFAULT
    )
}
