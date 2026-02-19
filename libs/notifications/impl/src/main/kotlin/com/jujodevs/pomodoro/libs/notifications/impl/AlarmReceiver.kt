package com.jujodevs.pomodoro.libs.notifications.impl

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.jujodevs.pomodoro.core.domain.util.Result
import com.jujodevs.pomodoro.libs.datastore.DataStoreManager
import com.jujodevs.pomodoro.libs.datastore.InternalStateKeys
import com.jujodevs.pomodoro.libs.notifications.impl.NotificationSchedulerImpl.Companion.ACTION_NOTIFICATION
import com.jujodevs.pomodoro.libs.notifications.impl.NotificationSchedulerImpl.Companion.EXTRA_NOTIFICATION_CHANNEL_ID
import com.jujodevs.pomodoro.libs.notifications.impl.NotificationSchedulerImpl.Companion.EXTRA_NOTIFICATION_ID
import com.jujodevs.pomodoro.libs.notifications.impl.NotificationSchedulerImpl.Companion.EXTRA_NOTIFICATION_MESSAGE_RES_ID
import com.jujodevs.pomodoro.libs.notifications.impl.NotificationSchedulerImpl.Companion.EXTRA_NOTIFICATION_TITLE_RES_ID
import com.jujodevs.pomodoro.libs.notifications.impl.NotificationSchedulerImpl.Companion.EXTRA_NOTIFICATION_TOKEN
import kotlinx.coroutines.runBlocking
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

/**
 * BroadcastReceiver that handles alarm triggers and displays notifications.
 *
 * Registered in AndroidManifest.xml to receive alarm intents from AlarmManager.
 */
class AlarmReceiver :
    BroadcastReceiver(),
    KoinComponent {
    private val dataStoreManager: DataStoreManager by inject()

    override fun onReceive(
        context: Context,
        intent: Intent,
    ) {
        if (intent.action != ACTION_NOTIFICATION) return

        val notificationId = intent.getIntExtra(EXTRA_NOTIFICATION_ID, -1)
        val titleResId = intent.getIntExtra(EXTRA_NOTIFICATION_TITLE_RES_ID, 0)
        val messageResId = intent.getIntExtra(EXTRA_NOTIFICATION_MESSAGE_RES_ID, 0)
        val channelId = intent.getStringExtra(EXTRA_NOTIFICATION_CHANNEL_ID)
        val token = intent.getStringExtra(EXTRA_NOTIFICATION_TOKEN) ?: ""

        if (titleResId > 0 && messageResId > 0 && channelId != null) {
            runBlocking {
                when (val activeToken = dataStoreManager.getValue(InternalStateKeys.ACTIVE_NOTIFICATION_TOKEN, "")) {
                    is Result.Success -> {
                        if (token.isNotEmpty() && token == activeToken.data) {
                            val title = context.getString(titleResId)
                            val message = context.getString(messageResId)
                            NotificationHelper.showNotification(
                                context = context,
                                notificationId = notificationId,
                                title = title,
                                message = message,
                                channelId = channelId,
                            )
                            context.stopService(Intent(context, PomodoroTimerForegroundService::class.java))
                        }
                    }

                    is Result.Failure -> Unit
                }
            }
        }
    }
}
