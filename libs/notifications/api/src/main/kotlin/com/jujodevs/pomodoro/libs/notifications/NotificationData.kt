package com.jujodevs.pomodoro.libs.notifications

/**
 * Data class representing a notification to be scheduled.
 *
 * @param id Unique identifier for the notification
 * @param titleResId String resource id displayed as notification title
 * @param messageResId String resource id displayed as notification message
 * @param channelId ID of the notification channel
 * @param scheduledTimeMillis Timestamp in milliseconds when notification should be shown
 * @param type Type of notification for categorization
 */
data class NotificationData(
    val id: Int,
    val titleResId: Int,
    val messageResId: Int,
    val channelId: String,
    val scheduledTimeMillis: Long,
    val type: NotificationType,
    val token: String = "",
)

/**
 * Enum representing different types of notifications.
 */
enum class NotificationType {
    WORK_SESSION_COMPLETE,
    SHORT_BREAK_COMPLETE,
    LONG_BREAK_COMPLETE,
}

/**
 * Sealed class representing notification channels.
 *
 * Each channel has its own id and importance level.
 */
sealed class NotificationChannel(
    val id: String,
    val importance: Int,
) {
    /**
     * Channel for Pomodoro session completion notifications.
     * Uses high importance to ensure user is notified even when device is idle.
     */
    data object PomodoroSession : NotificationChannel(
        id = "pomodoro_session",
        importance = 4, // IMPORTANCE_HIGH
    )

    /**
     * Channel for reminder notifications.
     * Uses default importance for less critical reminders.
     */
    data object Reminders : NotificationChannel(
        id = "reminders",
        importance = 3, // IMPORTANCE_DEFAULT
    )

    /**
     * Channel for the running timer foreground service notification.
     * Uses default importance with silent behavior to remain visible on lock screen
     * without playing sounds when updating/resuming.
     */
    data object RunningTimer : NotificationChannel(
        id = "running_timer_v2",
        importance = 3, // IMPORTANCE_DEFAULT
    )
}
