package com.jujodevs.pomodoro.libs.notifications.impl

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.content.ContextCompat
import com.jujodevs.pomodoro.core.domain.util.DataError
import com.jujodevs.pomodoro.core.domain.util.EmptyResult
import com.jujodevs.pomodoro.core.domain.util.Result
import com.jujodevs.pomodoro.core.domain.util.onFailure
import com.jujodevs.pomodoro.libs.datastore.DataStoreManager
import com.jujodevs.pomodoro.libs.datastore.InternalStateKeys
import com.jujodevs.pomodoro.libs.logger.Logger
import com.jujodevs.pomodoro.libs.notifications.NotificationData
import com.jujodevs.pomodoro.libs.notifications.NotificationScheduler
import com.jujodevs.pomodoro.libs.notifications.RunningTimerNotificationData
import java.io.IOException

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
    private val logger: Logger,
) : NotificationScheduler {
    override suspend fun scheduleNotification(notification: NotificationData): EmptyResult<DataError.Local> {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !alarmManager.canScheduleExactAlarms()) {
            logger.w(
                TAG,
                "Cannot schedule exact alarms. Permission not granted.",
            )
            return Result.Failure(DataError.Local.INSUFFICIENT_PERMISSIONS)
        }

        return executeOperation("scheduleNotification") {
            val intent =
                createIntent(notification.id).apply {
                    putExtra(EXTRA_NOTIFICATION_TITLE_RES_ID, notification.titleResId)
                    putExtra(EXTRA_NOTIFICATION_MESSAGE_RES_ID, notification.messageResId)
                    putExtra(EXTRA_NOTIFICATION_CHANNEL_ID, notification.channelId)
                    putExtra(EXTRA_NOTIFICATION_TYPE, notification.type.name)
                    putExtra(EXTRA_NOTIFICATION_TOKEN, notification.token)
                }

            val pendingIntent = createPendingIntent(notification.id, intent)

            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                notification.scheduledTimeMillis,
                pendingIntent,
            )

            saveNotificationId(notification.id).onFailure { error ->
                return@executeOperation Result.Failure(error)
            }
            if (notification.token.isNotEmpty()) {
                val currentToken = dataStoreManager.getValue(InternalStateKeys.ACTIVE_NOTIFICATION_TOKEN, "")
                when (currentToken) {
                    is Result.Success -> {
                        if (currentToken.data != notification.token) {
                            dataStoreManager
                                .setValue(
                                    InternalStateKeys.ACTIVE_NOTIFICATION_TOKEN,
                                    notification.token,
                                ).onFailure { error ->
                                    return@executeOperation Result.Failure(error)
                                }
                        }
                    }

                    is Result.Failure -> {
                        return@executeOperation currentToken.asEmptyResult()
                    }
                }
            }

            logger.d(
                TAG,
                "Notification scheduled: id=${notification.id}, time=${notification.scheduledTimeMillis}",
            )
            Result.Success(Unit)
        }
    }

    override suspend fun cancelNotification(notificationId: Int): EmptyResult<DataError.Local> {
        return executeOperation("cancelNotification") {
            val pendingIntent = createPendingIntent(notificationId, createIntent(notificationId))

            alarmManager.cancel(pendingIntent)
            pendingIntent.cancel()

            removeNotificationId(notificationId).onFailure { error ->
                return@executeOperation Result.Failure(error)
            }
            dataStoreManager.removeValue(InternalStateKeys.ACTIVE_NOTIFICATION_TOKEN).onFailure { error ->
                return@executeOperation Result.Failure(error)
            }

            logger.d(TAG, "Notification cancelled: id=$notificationId")
            Result.Success(Unit)
        }
    }

    override suspend fun cancelAllNotifications(): EmptyResult<DataError.Local> {
        return executeOperation("cancelAllNotifications") {
            val ids =
                when (val storedIds = getScheduledIds()) {
                    is Result.Success -> storedIds.data
                    is Result.Failure -> return@executeOperation storedIds.asEmptyResult()
                }
            if (ids.isEmpty()) {
                logger.d(TAG, "No notifications to cancel")
                return@executeOperation Result.Success(Unit)
            }

            ids.forEach { idString ->
                idString.toIntOrNull()?.let { id ->
                    val pendingIntent = createPendingIntent(id, createIntent(id))
                    alarmManager.cancel(pendingIntent)
                    pendingIntent.cancel()
                }
            }
            dataStoreManager.removeValue(InternalStateKeys.SCHEDULED_NOTIFICATION_IDS).onFailure { error ->
                return@executeOperation Result.Failure(error)
            }
            dataStoreManager.removeValue(InternalStateKeys.ACTIVE_NOTIFICATION_TOKEN).onFailure { error ->
                return@executeOperation Result.Failure(error)
            }
            logger.d(TAG, "All notifications cancelled: count=${ids.size}")
            Result.Success(Unit)
        }
    }

    override fun isNotificationScheduled(notificationId: Int): Boolean {
        val pendingIntent =
            PendingIntent.getBroadcast(
                context,
                notificationId,
                createIntent(notificationId),
                PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE,
            )

        return pendingIntent != null
    }

    override suspend fun showPersistentNotification(notification: NotificationData): EmptyResult<DataError.Local> =
        executeOperation("showPersistentNotification") {
            NotificationHelper.showNotification(
                context = context,
                notificationId = notification.id,
                title = context.getString(notification.titleResId),
                message = context.getString(notification.messageResId),
                channelId = notification.channelId,
                isPersistent = true,
            )
            Result.Success(Unit)
        }

    override suspend fun dismissPersistentNotification(notificationId: Int): EmptyResult<DataError.Local> =
        executeOperation("dismissPersistentNotification") {
            NotificationHelper.dismissNotification(context, notificationId)
            Result.Success(Unit)
        }

    override suspend fun startRunningForegroundTimer(
        notification: RunningTimerNotificationData,
    ): EmptyResult<DataError.Local> =
        executeOperation("startRunningForegroundTimer") {
            val intent =
                PomodoroTimerForegroundService.createStartIntent(
                    context = context,
                    notification = notification,
                )
            ContextCompat.startForegroundService(context, intent)
            Result.Success(Unit)
        }

    override suspend fun stopRunningForegroundTimer(): EmptyResult<DataError.Local> =
        executeOperation("stopRunningForegroundTimer") {
            context.stopService(Intent(context, PomodoroTimerForegroundService::class.java))
            Result.Success(Unit)
        }

    private suspend fun executeOperation(
        operationName: String,
        operation: suspend () -> EmptyResult<DataError.Local>,
    ): EmptyResult<DataError.Local> =
        runCatching {
            operation()
        }.fold(
            onSuccess = { result ->
                result.onFailure { error ->
                    when (error) {
                        DataError.Local.INSUFFICIENT_PERMISSIONS ->
                            logger.w(
                                TAG,
                                "$operationName failed due to insufficient permissions",
                            )

                        DataError.Local.DISK_FULL ->
                            logger.e(
                                TAG,
                                "$operationName failed due to storage constraints",
                            )

                        DataError.Local.NOT_FOUND ->
                            logger.w(
                                TAG,
                                "$operationName failed because requested data was not found",
                            )

                        DataError.Local.UNKNOWN ->
                            logger.e(
                                TAG,
                                "$operationName failed due to an unknown local error",
                            )
                    }
                }
            },
            onFailure = { throwable ->
                when (throwable) {
                    is SecurityException -> {
                        logger.w(
                            TAG,
                            "$operationName failed due to insufficient permissions",
                            throwable,
                        )
                        Result.Failure(DataError.Local.INSUFFICIENT_PERMISSIONS)
                    }

                    is IOException -> {
                        logger.e(
                            TAG,
                            "$operationName failed due to storage constraints",
                            throwable,
                        )
                        Result.Failure(DataError.Local.DISK_FULL)
                    }

                    else -> {
                        logger.e(
                            TAG,
                            "$operationName failed: ${throwable.message}",
                            throwable,
                        )
                        Result.Failure(DataError.Local.UNKNOWN)
                    }
                }
            },
        )

    private fun createIntent(notificationId: Int): Intent =
        Intent(context, AlarmReceiver::class.java).apply {
            action = ACTION_NOTIFICATION
            putExtra(EXTRA_NOTIFICATION_ID, notificationId)
        }

    private fun createPendingIntent(
        notificationId: Int,
        intent: Intent,
    ): PendingIntent =
        PendingIntent.getBroadcast(
            context,
            notificationId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )

    private suspend fun getScheduledIds(): Result<Set<String>, DataError.Local> =
        dataStoreManager.getValue(InternalStateKeys.SCHEDULED_NOTIFICATION_IDS, emptySet<String>())

    private suspend fun saveNotificationId(id: Int): EmptyResult<DataError.Local> {
        val scheduledIds = getScheduledIds()
        return when (scheduledIds) {
            is Result.Failure -> scheduledIds.asEmptyResult()
            is Result.Success -> {
                val currentIds = scheduledIds.data.toMutableSet()
                if (currentIds.add(id.toString())) {
                    dataStoreManager.setValue<Set<String>>(InternalStateKeys.SCHEDULED_NOTIFICATION_IDS, currentIds)
                } else {
                    Result.Success(Unit)
                }
            }
        }
    }

    private suspend fun removeNotificationId(id: Int): EmptyResult<DataError.Local> {
        val scheduledIds = getScheduledIds()
        return when (scheduledIds) {
            is Result.Failure -> scheduledIds.asEmptyResult()
            is Result.Success -> {
                val currentIds = scheduledIds.data.toMutableSet()
                if (currentIds.remove(id.toString())) {
                    dataStoreManager.setValue<Set<String>>(InternalStateKeys.SCHEDULED_NOTIFICATION_IDS, currentIds)
                } else {
                    Result.Success(Unit)
                }
            }
        }
    }

    private fun <T> Result<T, DataError.Local>.asEmptyResult(): EmptyResult<DataError.Local> =
        when (this) {
            is Result.Success -> Result.Success(Unit)
            is Result.Failure -> Result.Failure(error)
        }

    companion object {
        const val ACTION_NOTIFICATION = "com.jujodevs.pomodoro.NOTIFICATION_ACTION"
        const val EXTRA_NOTIFICATION_ID = "notification_id"
        const val EXTRA_NOTIFICATION_TITLE_RES_ID = "notification_title_res_id"
        const val EXTRA_NOTIFICATION_MESSAGE_RES_ID = "notification_message_res_id"
        const val EXTRA_NOTIFICATION_CHANNEL_ID = "notification_channel_id"
        const val EXTRA_NOTIFICATION_TYPE = "notification_type"
        const val EXTRA_NOTIFICATION_TOKEN = "notification_token"

        private const val TAG = "NotificationScheduler"
    }
}
