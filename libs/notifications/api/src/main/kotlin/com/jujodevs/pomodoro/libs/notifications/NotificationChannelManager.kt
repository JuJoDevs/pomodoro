package com.jujodevs.pomodoro.libs.notifications

/**
 * Interface for managing notification channels.
 *
 * Notification channels are required on Android 8.0 (API 26) and above.
 * This interface provides functionality to create and manage channels.
 */
interface NotificationChannelManager {
    /**
     * Create all required notification channels.
     *
     * Should be called during app initialization.
     */
    fun createNotificationChannels()

    /**
     * Delete a notification channel by its ID.
     *
     * @param channelId The ID of the channel to delete
     */
    fun deleteNotificationChannel(channelId: String)
}
