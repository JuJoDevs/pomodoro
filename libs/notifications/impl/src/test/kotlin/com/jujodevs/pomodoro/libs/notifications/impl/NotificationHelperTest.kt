package com.jujodevs.pomodoro.libs.notifications.impl

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.jujodevs.pomodoro.libs.notifications.NotificationChannel
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment

@RunWith(RobolectricTestRunner::class)
class NotificationHelperTest {
    private lateinit var context: Context

    @Before
    fun setUp() {
        context = RuntimeEnvironment.getApplication()
    }

    @Test
    fun `GIVEN running notification delete intent WHEN notification is created THEN should attach delete intent`() {
        // GIVEN
        val deleteIntent =
            PendingIntent.getBroadcast(
                context,
                101,
                Intent("com.jujodevs.pomodoro.libs.notifications.impl.action.RESTORE_RUNNING_TIMER"),
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
            )

        // WHEN
        val notification =
            NotificationHelper.createRunningTimerNotification(
                context = context,
                notificationId = 2,
                title = "Running",
                message = "2/4 sessions",
                channelId = NotificationChannel.RunningTimer.id,
                endTimeMillis = System.currentTimeMillis() + 60_000L,
                deleteIntent = deleteIntent,
            )

        // THEN
        assertNotNull(notification.deleteIntent)
    }
}
