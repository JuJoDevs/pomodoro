package com.jujodevs.pomodoro.libs.notifications.impl

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.jujodevs.pomodoro.libs.notifications.impl.NotificationSchedulerImpl.Companion.ACTION_NOTIFICATION
import com.jujodevs.pomodoro.libs.notifications.impl.NotificationSchedulerImpl.Companion.EXTRA_NOTIFICATION_CHANNEL_ID
import com.jujodevs.pomodoro.libs.notifications.impl.NotificationSchedulerImpl.Companion.EXTRA_NOTIFICATION_ID
import com.jujodevs.pomodoro.libs.notifications.impl.NotificationSchedulerImpl.Companion.EXTRA_NOTIFICATION_MESSAGE
import com.jujodevs.pomodoro.libs.notifications.impl.NotificationSchedulerImpl.Companion.EXTRA_NOTIFICATION_TITLE
/**
 * BroadcastReceiver that handles alarm triggers and displays notifications.
 *
 * Registered in AndroidManifest.xml to receive alarm intents from AlarmManager.
 */
class AlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != ACTION_NOTIFICATION) return

        val notificationId = intent.getIntExtra(EXTRA_NOTIFICATION_ID, -1)
        val title = intent.getStringExtra(EXTRA_NOTIFICATION_TITLE)
        val message = intent.getStringExtra(EXTRA_NOTIFICATION_MESSAGE)
        val channelId = intent.getStringExtra(EXTRA_NOTIFICATION_CHANNEL_ID)

        if (title != null && message != null && channelId != null) {
            NotificationHelper.showNotification(
                context = context,
                notificationId = notificationId,
                title = title,
                message = message,
                channelId = channelId
            )
        }
    }
}
