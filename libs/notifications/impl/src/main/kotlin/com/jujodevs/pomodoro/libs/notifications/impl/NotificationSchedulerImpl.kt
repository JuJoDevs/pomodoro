package com.jujodevs.pomodoro.libs.notifications.impl

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import com.jujodevs.pomodoro.libs.datastore.DataStoreManager
import com.jujodevs.pomodoro.libs.datastore.InternalStateKeys
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
    private val dataStoreManager: DataStoreManager,
    private val logger: Logger
) : NotificationScheduler {

    override suspend fun scheduleNotification(notification: NotificationData): Result<Unit> {
        return runCatching {
            val intent = createIntent(notification.id).apply {
                putExtra(EXTRA_NOTIFICATION_TITLE, notification.title)
                putExtra(EXTRA_NOTIFICATION_MESSAGE, notification.message)
                putExtra(EXTRA_NOTIFICATION_CHANNEL_ID, notification.channelId)
                putExtra(EXTRA_NOTIFICATION_TYPE, notification.type.name)
            }

            val pendingIntent = createPendingIntent(notification.id, intent)

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
                    return Result.failure(SecurityException("SCHEDULE_EXACT_ALARM permission not granted"))
                }
            } else {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    notification.scheduledTimeMillis,
                    pendingIntent
                )
            }

            saveNotificationId(notification.id)

            logger.d(
                TAG,
                "Notification scheduled: id=${notification.id}, time=${notification.scheduledTimeMillis}"
            )
        }
    }

    override suspend fun cancelNotification(notificationId: Int): Result<Unit> {
        return runCatching {
            val pendingIntent = createPendingIntent(notificationId, createIntent(notificationId))

            alarmManager.cancel(pendingIntent)
            pendingIntent.cancel()

            removeNotificationId(notificationId)

            logger.d(TAG, "Notification cancelled: id=$notificationId")
        }
    }

    override suspend fun cancelAllNotifications(): Result<Unit> {
        return runCatching {
            val ids = getScheduledIds()
            if (ids.isEmpty()) {
                logger.d(TAG, "No notifications to cancel")
                return@runCatching
            }

            ids.forEach { idString ->
                idString.toIntOrNull()?.let { id ->
                    val pendingIntent = createPendingIntent(id, createIntent(id))
                    alarmManager.cancel(pendingIntent)
                    pendingIntent.cancel()
                }
            }
            dataStoreManager.removeValue(InternalStateKeys.SCHEDULED_NOTIFICATION_IDS)
            logger.d(TAG, "All notifications cancelled: count=${ids.size}")
        }
    }

    override fun isNotificationScheduled(notificationId: Int): Boolean {
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            notificationId,
            createIntent(notificationId),
            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
        )

        return pendingIntent != null
    }

    private fun createIntent(notificationId: Int): Intent {
        return Intent(context, AlarmReceiver::class.java).apply {
            action = ACTION_NOTIFICATION
            putExtra(EXTRA_NOTIFICATION_ID, notificationId)
        }
    }

    private fun createPendingIntent(notificationId: Int, intent: Intent): PendingIntent {
        return PendingIntent.getBroadcast(
            context,
            notificationId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    private suspend fun getScheduledIds(): Set<String> {
        return dataStoreManager.getValue(InternalStateKeys.SCHEDULED_NOTIFICATION_IDS, emptySet<String>())
    }

    private suspend fun saveNotificationId(id: Int) {
        val currentIds = getScheduledIds().toMutableSet()
        if (currentIds.add(id.toString())) {
            dataStoreManager.setValue<Set<String>>(InternalStateKeys.SCHEDULED_NOTIFICATION_IDS, currentIds)
        }
    }

    private suspend fun removeNotificationId(id: Int) {
        val currentIds = getScheduledIds().toMutableSet()
        if (currentIds.remove(id.toString())) {
            dataStoreManager.setValue<Set<String>>(InternalStateKeys.SCHEDULED_NOTIFICATION_IDS, currentIds)
        }
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
