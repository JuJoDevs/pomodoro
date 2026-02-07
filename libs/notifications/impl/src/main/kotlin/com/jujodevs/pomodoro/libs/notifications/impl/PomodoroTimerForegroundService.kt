package com.jujodevs.pomodoro.libs.notifications.impl

import android.app.AlarmManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.IBinder
import com.jujodevs.pomodoro.libs.notifications.NotificationChannel
import com.jujodevs.pomodoro.libs.notifications.RunningTimerNotificationData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Foreground service that keeps the running timer notification alive while the device is locked.
 */
class PomodoroTimerForegroundService : Service() {

    private var isForegroundStarted: Boolean = false
    private var completionFallbackJob: Job? = null
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START_OR_UPDATE -> startOrUpdateForeground(intent)
        }
        return START_NOT_STICKY
    }

    override fun onDestroy() {
        if (isForegroundStarted) {
            stopForeground(STOP_FOREGROUND_REMOVE)
            isForegroundStarted = false
        }
        completionFallbackJob?.cancel()
        serviceScope.coroutineContext.cancel()
        super.onDestroy()
    }

    private fun startOrUpdateForeground(intent: Intent) {
        val payload = intent.toForegroundStartPayload() ?: return

        val notification = NotificationHelper.createRunningTimerNotification(
            context = this,
            notificationId = payload.notificationId,
            title = getString(payload.titleResId),
            message = resolveString(
                resId = payload.messageResId,
                firstArg = payload.messageArgFirst,
                secondArg = payload.messageArgSecond
            ),
            channelId = payload.channelId,
            endTimeMillis = payload.endTimeMillis
        )

        if (!isForegroundStarted) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                startForeground(
                    payload.notificationId,
                    notification,
                    ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC
                )
            } else {
                startForeground(payload.notificationId, notification)
            }
            isForegroundStarted = true
        } else {
            NotificationHelper.updateNotification(
                context = this,
                notificationId = payload.notificationId,
                notification = notification
            )
        }

        scheduleCompletionFallbackIfNeeded(
            completionNotificationId = payload.completionNotificationId,
            completionTitle = getString(payload.completionTitleResId),
            completionMessage = getString(payload.completionMessageResId),
            endTimeMillis = payload.endTimeMillis
        )
    }

    private fun Intent.toForegroundStartPayload(): ForegroundStartPayload? {
        var payload: ForegroundStartPayload? = null

        val titleResId = getIntExtra(EXTRA_TITLE_RES_ID, 0)
        val messageResId = getIntExtra(EXTRA_MESSAGE_RES_ID, 0)
        val channelId = getStringExtra(EXTRA_CHANNEL_ID)
        val completionTitleResId = getIntExtra(EXTRA_COMPLETION_TITLE_RES_ID, 0)
        val completionMessageResId = getIntExtra(EXTRA_COMPLETION_MESSAGE_RES_ID, 0)

        if (
            hasRequiredStartExtras(
                titleResId = titleResId,
                messageResId = messageResId,
                completionTitleResId = completionTitleResId,
                completionMessageResId = completionMessageResId
            ) && channelId != null
        ) {
            payload = ForegroundStartPayload(
                notificationId = getIntExtra(EXTRA_NOTIFICATION_ID, DEFAULT_NOTIFICATION_ID),
                titleResId = titleResId,
                messageResId = messageResId,
                messageArgFirst = optionalIntExtra(EXTRA_MESSAGE_ARG_FIRST),
                messageArgSecond = optionalIntExtra(EXTRA_MESSAGE_ARG_SECOND),
                channelId = channelId,
                endTimeMillis = getLongExtra(EXTRA_END_TIME_MILLIS, 0L),
                completionNotificationId = getIntExtra(
                    EXTRA_COMPLETION_NOTIFICATION_ID,
                    DEFAULT_COMPLETION_NOTIFICATION_ID
                ),
                completionTitleResId = completionTitleResId,
                completionMessageResId = completionMessageResId
            )
        }

        return payload
    }

    private fun hasRequiredStartExtras(
        titleResId: Int,
        messageResId: Int,
        completionTitleResId: Int,
        completionMessageResId: Int
    ): Boolean {
        return titleResId > 0 &&
            messageResId > 0 &&
            completionTitleResId > 0 &&
            completionMessageResId > 0
    }

    private fun Intent.optionalIntExtra(key: String): Int? {
        return if (hasExtra(key)) getIntExtra(key, 0) else null
    }

    private fun resolveString(resId: Int, firstArg: Int?, secondArg: Int?): String {
        return when {
            firstArg == null -> getString(resId)
            secondArg == null -> getString(resId, firstArg)
            else -> getString(resId, firstArg, secondArg)
        }
    }

    private fun scheduleCompletionFallbackIfNeeded(
        completionNotificationId: Int,
        completionTitle: String,
        completionMessage: String,
        endTimeMillis: Long
    ) {
        completionFallbackJob?.cancel()

        val alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && alarmManager.canScheduleExactAlarms()) {
            return
        }

        val delayMillis = (endTimeMillis - System.currentTimeMillis()).coerceAtLeast(0L)
        completionFallbackJob = serviceScope.launch {
            delay(delayMillis)
            NotificationHelper.showNotification(
                context = this@PomodoroTimerForegroundService,
                notificationId = completionNotificationId,
                title = completionTitle,
                message = completionMessage,
                channelId = NotificationChannel.PomodoroSession.id
            )
            stopSelf()
        }
    }

    companion object {
        private const val DEFAULT_NOTIFICATION_ID = 2

        const val ACTION_START_OR_UPDATE = "com.jujodevs.pomodoro.notifications.action.START_OR_UPDATE"

        const val EXTRA_NOTIFICATION_ID = "extra_notification_id"
        const val EXTRA_TITLE_RES_ID = "extra_title_res_id"
        const val EXTRA_MESSAGE_RES_ID = "extra_message_res_id"
        const val EXTRA_MESSAGE_ARG_FIRST = "extra_message_arg_first"
        const val EXTRA_MESSAGE_ARG_SECOND = "extra_message_arg_second"
        const val EXTRA_CHANNEL_ID = "extra_channel_id"
        const val EXTRA_END_TIME_MILLIS = "extra_end_time_millis"
        const val EXTRA_COMPLETION_NOTIFICATION_ID = "extra_completion_notification_id"
        const val EXTRA_COMPLETION_TITLE_RES_ID = "extra_completion_title_res_id"
        const val EXTRA_COMPLETION_MESSAGE_RES_ID = "extra_completion_message_res_id"

        private const val DEFAULT_COMPLETION_NOTIFICATION_ID = 1

        fun createStartIntent(
            context: Context,
            notification: RunningTimerNotificationData
        ): Intent = Intent(context, PomodoroTimerForegroundService::class.java).apply {
            action = ACTION_START_OR_UPDATE
            putExtra(EXTRA_NOTIFICATION_ID, notification.notificationId)
            putExtra(EXTRA_TITLE_RES_ID, notification.titleResId)
            putExtra(EXTRA_MESSAGE_RES_ID, notification.messageResId)
            notification.messageArgFirst?.let { putExtra(EXTRA_MESSAGE_ARG_FIRST, it) }
            notification.messageArgSecond?.let { putExtra(EXTRA_MESSAGE_ARG_SECOND, it) }
            putExtra(EXTRA_CHANNEL_ID, notification.channelId)
            putExtra(EXTRA_END_TIME_MILLIS, notification.endTimeMillis)
            putExtra(EXTRA_COMPLETION_NOTIFICATION_ID, notification.completionNotificationId)
            putExtra(EXTRA_COMPLETION_TITLE_RES_ID, notification.completionTitleResId)
            putExtra(EXTRA_COMPLETION_MESSAGE_RES_ID, notification.completionMessageResId)
        }
    }
}

private data class ForegroundStartPayload(
    val notificationId: Int,
    val titleResId: Int,
    val messageResId: Int,
    val messageArgFirst: Int?,
    val messageArgSecond: Int?,
    val channelId: String,
    val endTimeMillis: Long,
    val completionNotificationId: Int,
    val completionTitleResId: Int,
    val completionMessageResId: Int
)
