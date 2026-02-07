package com.jujodevs.pomodoro.libs.notifications.impl

import android.app.Notification
import android.app.NotificationManager
import android.content.Context
import com.jujodevs.pomodoro.core.resources.R
import com.jujodevs.pomodoro.libs.notifications.NotificationChannel
import com.jujodevs.pomodoro.libs.notifications.NotificationChannel.PomodoroSession
import com.jujodevs.pomodoro.libs.notifications.NotificationChannel.Reminders
import com.jujodevs.pomodoro.libs.notifications.NotificationChannel.RunningTimer
import com.jujodevs.pomodoro.libs.notifications.NotificationChannelManager
import android.app.NotificationChannel as AndroidNotificationChannel

/**
 * Android implementation of NotificationChannelManager.
 *
 * Creates and manages notification channels required for Android 8.0+.
 */
class NotificationChannelManagerImpl(
    private val context: Context
) : NotificationChannelManager {

    override fun createNotificationChannels() {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        pomodoroChannels()
            .map(::toAndroidChannel)
            .forEach(notificationManager::createNotificationChannel)
    }

    override fun deleteNotificationChannel(channelId: String) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.deleteNotificationChannel(channelId)
    }

    private fun pomodoroChannels(): List<NotificationChannel> = listOf(
        PomodoroSession,
        Reminders,
        RunningTimer
    )

    private fun toAndroidChannel(channel: NotificationChannel): AndroidNotificationChannel {
        return AndroidNotificationChannel(
            channel.id,
            channelName(channel),
            channel.importance
        ).apply {
            description = channelDescription(channel)
            if (channel == RunningTimer) {
                enableVibration(false)
                enableLights(false)
                setSound(null, null)
                setShowBadge(false)
                lockscreenVisibility = Notification.VISIBILITY_PUBLIC
            } else {
                enableVibration(true)
                enableLights(true)
            }
        }
    }

    private fun channelName(channel: NotificationChannel): String = context.getString(
        when (channel) {
            PomodoroSession -> R.string.notification_channel_sessions_name
            Reminders -> R.string.notification_channel_reminders_name
            RunningTimer -> R.string.notification_channel_sessions_name
        }
    )

    private fun channelDescription(channel: NotificationChannel): String = context.getString(
        when (channel) {
            PomodoroSession -> R.string.notification_channel_sessions_description
            Reminders -> R.string.notification_channel_reminders_description
            RunningTimer -> R.string.notification_channel_sessions_description
        }
    )
}
