package com.jujodevs.pomodoro.libs.notifications.impl

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import com.jujodevs.pomodoro.libs.notifications.NotificationChannel.PomodoroSession
import com.jujodevs.pomodoro.libs.notifications.NotificationChannel.Reminders
import com.jujodevs.pomodoro.libs.notifications.NotificationChannelManager

/**
 * Android implementation of NotificationChannelManager.
 *
 * Creates and manages notification channels required for Android 8.0+.
 */
class NotificationChannelManagerImpl(
    private val context: Context
) : NotificationChannelManager {

    override fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            val channels = listOf(
                PomodoroSession,
                Reminders
            )

            channels.forEach { channel ->
                val androidChannel = NotificationChannel(
                    channel.id,
                    channel.name,
                    channel.importance
                ).apply {
                    description = channel.description
                    enableVibration(true)
                    enableLights(true)
                }

                notificationManager.createNotificationChannel(androidChannel)
            }
        }
    }

    override fun deleteNotificationChannel(channelId: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.deleteNotificationChannel(channelId)
        }
    }
}
