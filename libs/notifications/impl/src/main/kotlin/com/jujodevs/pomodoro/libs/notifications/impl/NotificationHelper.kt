package com.jujodevs.pomodoro.libs.notifications.impl

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat

/**
 * Helper object for creating and displaying notifications.
 */
object NotificationHelper {

    /**
     * Show a notification with the given parameters.
     *
     * @param context The application context
     * @param notificationId Unique ID for the notification
     * @param title Title text for the notification
     * @param message Message text for the notification
     * @param channelId ID of the notification channel
     */
    fun showNotification(
        context: Context,
        notificationId: Int,
        title: String,
        message: String,
        channelId: String,
        isPersistent: Boolean = false
    ) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Create intent for when notification is tapped
        val intent = context.packageManager.getLaunchIntentForPackage(context.packageName)?.apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            notificationId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(com.jujodevs.pomodoro.core.resources.R.drawable.ic_notification)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(if (isPersistent) NotificationCompat.PRIORITY_LOW else NotificationCompat.PRIORITY_HIGH)
            .setCategory(if (isPersistent) NotificationCompat.CATEGORY_SERVICE else NotificationCompat.CATEGORY_ALARM)
            .setAutoCancel(!isPersistent)
            .setOngoing(isPersistent)
            .setOnlyAlertOnce(isPersistent)
            .setContentIntent(pendingIntent)
            .apply {
                if (!isPersistent) {
                    setDefaults(NotificationCompat.DEFAULT_ALL)
                }
            }
            .build()

        notificationManager.notify(notificationId, notification)
    }

    fun createRunningTimerNotification(
        context: Context,
        notificationId: Int,
        title: String,
        message: String,
        channelId: String,
        endTimeMillis: Long
    ): Notification {
        val intent = context.packageManager.getLaunchIntentForPackage(context.packageName)?.apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            notificationId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(com.jujodevs.pomodoro.core.resources.R.drawable.ic_notification)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setCategory(NotificationCompat.CATEGORY_SERVICE)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setSilent(true)
            .setOnlyAlertOnce(true)
            .setOngoing(true)
            .setAutoCancel(false)
            .setContentIntent(pendingIntent)

        if (endTimeMillis > System.currentTimeMillis()) {
            builder
                .setWhen(endTimeMillis)
                .setUsesChronometer(true)
                .setChronometerCountDown(true)
        }

        return builder.build()
    }

    fun updateNotification(
        context: Context,
        notificationId: Int,
        notification: Notification
    ) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(notificationId, notification)
    }

    fun dismissNotification(context: Context, notificationId: Int) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(notificationId)
    }
}
