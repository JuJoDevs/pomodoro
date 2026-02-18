package com.jujodevs.pomodoro.libs.notifications.impl

import android.content.Context
import android.media.RingtoneManager
import android.os.Build
import com.jujodevs.pomodoro.libs.notifications.AlarmSoundLabelProvider
import com.jujodevs.pomodoro.libs.notifications.NotificationChannel

/**
 * Android implementation that reads the completion channel sound from the system.
 *
 * Best-effort resolution: uses NotificationChannel.sound and RingtoneManager for label.
 */
class AlarmSoundLabelProviderImpl(
    private val context: Context
) : AlarmSoundLabelProvider {

    override fun getCompletionChannelSoundLabel(): String {
        return when {
            Build.VERSION.SDK_INT < Build.VERSION_CODES.O -> LABEL_UNKNOWN
            else -> {
                val notificationManager = context.getSystemService(
                    Context.NOTIFICATION_SERVICE
                ) as android.app.NotificationManager
                val channel = notificationManager.getNotificationChannel(NotificationChannel.PomodoroSession.id)
                when (val soundUri = channel?.sound) {
                    null -> LABEL_SILENT
                    else -> resolveSoundLabel(soundUri)
                }
            }
        }
    }

    private fun resolveSoundLabel(soundUri: android.net.Uri): String {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) return LABEL_CUSTOM_SOUND
        return runCatching {
            RingtoneManager.getRingtone(context, soundUri)?.getTitle(context) ?: LABEL_CUSTOM_SOUND
        }.getOrDefault(LABEL_CUSTOM_SOUND)
    }

    companion object {
        private const val LABEL_SILENT = "Silent"
        private const val LABEL_CUSTOM_SOUND = "Custom sound"
        private const val LABEL_UNKNOWN = "Unknown"
    }
}
