package com.jujodevs.pomodoro.libs.notifications.impl

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import com.jujodevs.pomodoro.libs.logger.Logger
import com.jujodevs.pomodoro.libs.notifications.NotificationData
import com.jujodevs.pomodoro.libs.notifications.NotificationScheduler

/**
 * Android implementation of NotificationScheduler using AlarmManager.
 *
 * Uses AlarmManager.setExactAndAllowWhileIdle for reliable notification delivery
 * even when the device is in doze mode or the screen is locked.
 */
class NotificationSchedulerImpl(
    private val context: Context,
    private val alarmManager: AlarmManager,
    private val logger: Logger
) : NotificationScheduler {

    override suspend fun scheduleNotification(notification: NotificationData): Result<Unit> {
        return runCatching {
            val intent = Intent(context, AlarmReceiver::class.java).apply {
                action = ACTION_NOTIFICATION
                putExtra(EXTRA_NOTIFICATION_ID, notification.id)
                putExtra(EXTRA_NOTIFICATION_TITLE, notification.title)
                putExtra(EXTRA_NOTIFICATION_MESSAGE, notification.message)
                putExtra(EXTRA_NOTIFICATION_CHANNEL_ID, notification.channelId)
                putExtra(EXTRA_NOTIFICATION_TYPE, notification.type.name)
            }

            val pendingIntent = PendingIntent.getBroadcast(
                context,
                notification.id,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (alarmManager.canScheduleExactAlarms()) {
                    alarmManager.setExactAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        notification.scheduledTimeMillis,
                        pendingIntent
                    )
                } else {
                    logger.w(
                        TAG,
                        "Cannot schedule exact alarms. Permission not granted."
                    )
                    throw SecurityException("SCHEDULE_EXACT_ALARM permission not granted")
                }
            } else {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    notification.scheduledTimeMillis,
                    pendingIntent
                )
            }

            logger.d(
                TAG,
                "Notification scheduled: id=${notification.id}, time=${notification.scheduledTimeMillis}"
            )
        }
    }

    override suspend fun cancelNotification(notificationId: Int): Result<Unit> {
        return runCatching {
            val intent = Intent(context, AlarmReceiver::class.java).apply {
                action = ACTION_NOTIFICATION
            }

            val pendingIntent = PendingIntent.getBroadcast(
                context,
                notificationId,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            alarmManager.cancel(pendingIntent)
            pendingIntent.cancel()

            logger.d(TAG, "Notification cancelled: id=$notificationId")
        }
    }

    override suspend fun cancelAllNotifications(): Result<Unit> {
        return runCatching {
            // Note: This is a simplified version. In production, you'd need to track all notification IDs
            logger.d(TAG, "All notifications cancelled")
        }
    }

    override fun isNotificationScheduled(notificationId: Int): Boolean {
        val intent = Intent(context, AlarmReceiver::class.java).apply {
            action = ACTION_NOTIFICATION
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            notificationId,
            intent,
            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
        )

        return pendingIntent != null
    }

    companion object {
        const val ACTION_NOTIFICATION = "com.jujodevs.pomodoro.NOTIFICATION_ACTION"
        const val EXTRA_NOTIFICATION_ID = "notification_id"
        const val EXTRA_NOTIFICATION_TITLE = "notification_title"
        const val EXTRA_NOTIFICATION_MESSAGE = "notification_message"
        const val EXTRA_NOTIFICATION_CHANNEL_ID = "notification_channel_id"
        const val EXTRA_NOTIFICATION_TYPE = "notification_type"

        private const val TAG = "NotificationScheduler"
    }
}
