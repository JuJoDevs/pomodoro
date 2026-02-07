package com.jujodevs.pomodoro.libs.notifications

/**
 * Data for the running timer foreground notification.
 *
 * @param notificationId Unique notification id used by the foreground service
 * @param titleResId Notification title resource id
 * @param messageResId Notification message resource id
 * @param messageArgFirst First optional format arg for message string resource
 * @param messageArgSecond Second optional format arg for message string resource
 * @param channelId Notification channel id
 * @param endTimeMillis Epoch time when the running phase should end
 * @param completionNotificationId Notification id for completion event
 * @param completionTitleResId Completion title resource id
 * @param completionMessageResId Completion message resource id
 */
data class RunningTimerNotificationData(
    val notificationId: Int,
    val titleResId: Int,
    val messageResId: Int,
    val messageArgFirst: Int? = null,
    val messageArgSecond: Int? = null,
    val channelId: String,
    val endTimeMillis: Long,
    val completionNotificationId: Int,
    val completionTitleResId: Int,
    val completionMessageResId: Int,
)
