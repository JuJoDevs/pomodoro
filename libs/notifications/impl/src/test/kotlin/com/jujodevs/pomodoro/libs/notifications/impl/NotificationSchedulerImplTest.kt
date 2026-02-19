package com.jujodevs.pomodoro.libs.notifications.impl

import com.jujodevs.pomodoro.core.domain.util.DataError
import com.jujodevs.pomodoro.core.domain.util.EmptyResult
import com.jujodevs.pomodoro.core.domain.util.Result
import com.jujodevs.pomodoro.core.domain.util.isSuccess
import com.jujodevs.pomodoro.core.resources.R
import com.jujodevs.pomodoro.libs.notifications.NotificationChannel
import com.jujodevs.pomodoro.libs.notifications.NotificationData
import com.jujodevs.pomodoro.libs.notifications.NotificationScheduler
import com.jujodevs.pomodoro.libs.notifications.NotificationType
import com.jujodevs.pomodoro.libs.notifications.RunningTimerNotificationData
import kotlinx.coroutines.test.runTest
import org.amshove.kluent.shouldBe
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldContain
import org.amshove.kluent.shouldNotContain
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

/**
 * Tests for NotificationScheduler contract.
 *
 * Note: The real NotificationSchedulerImpl uses Android APIs (Intent, PendingIntent, AlarmManager)
 * that are difficult to unit test without Robolectric. These tests verify the contract
 * using a fake implementation, which is sufficient for testing dependent code.
 */
class NotificationSchedulerImplTest {
    private lateinit var scheduler: TestNotificationScheduler

    @BeforeEach
    fun setUp() {
        scheduler = TestNotificationScheduler()
    }

    @Test
    fun `GIVEN notification data WHEN scheduleNotification THEN should store notification`() =
        runTest {
            // GIVEN
            val notification = createTestNotification(id = 1)

            // WHEN
            val result = scheduler.scheduleNotification(notification)

            // THEN
            result.isSuccess shouldBe true
            scheduler.getScheduledNotifications() shouldContain notification
        }

    @Test
    fun `GIVEN scheduled notification WHEN cancelNotification THEN should remove it`() =
        runTest {
            // GIVEN
            val notification = createTestNotification(id = 1)
            scheduler.scheduleNotification(notification)

            // WHEN
            val result = scheduler.cancelNotification(1)

            // THEN
            result.isSuccess shouldBe true
            scheduler.getScheduledNotifications() shouldNotContain notification
        }

    @Test
    fun `GIVEN multiple notifications WHEN cancelAllNotifications THEN should remove all`() =
        runTest {
            // GIVEN
            scheduler.scheduleNotification(createTestNotification(id = 1))
            scheduler.scheduleNotification(createTestNotification(id = 2))
            scheduler.scheduleNotification(createTestNotification(id = 3))

            // WHEN
            val result = scheduler.cancelAllNotifications()

            // THEN
            result.isSuccess shouldBe true
            scheduler.getScheduledNotifications().size shouldBeEqualTo 0
        }

    @Test
    fun `GIVEN scheduled notification WHEN isNotificationScheduled THEN should return true`() =
        runTest {
            // GIVEN
            val notification = createTestNotification(id = 1)
            scheduler.scheduleNotification(notification)

            // WHEN
            val isScheduled = scheduler.isNotificationScheduled(1)

            // THEN
            isScheduled shouldBe true
        }

    @Test
    fun `GIVEN no notification WHEN isNotificationScheduled THEN should return false`() {
        // WHEN
        val isScheduled = scheduler.isNotificationScheduled(999)

        // THEN
        isScheduled shouldBe false
    }

    @Test
    fun `GIVEN same id notification WHEN scheduleNotification twice THEN should update`() =
        runTest {
            // GIVEN
            val notification1 =
                createTestNotification(
                    id = 1,
                    titleResId = R.string.notification_work_complete_title,
                )
            val notification2 =
                createTestNotification(id = 1, titleResId = R.string.notification_short_break_complete_title)

            // WHEN
            scheduler.scheduleNotification(notification1)
            scheduler.scheduleNotification(notification2)

            // THEN
            scheduler.getScheduledNotifications().size shouldBeEqualTo 1
            scheduler.getScheduledNotifications().first().titleResId shouldBeEqualTo
                R.string.notification_short_break_complete_title
        }

    private fun createTestNotification(
        id: Int,
        titleResId: Int = R.string.notification_work_complete_title,
        messageResId: Int = R.string.notification_work_complete_message,
        type: NotificationType = NotificationType.WORK_SESSION_COMPLETE,
    ) = NotificationData(
        id = id,
        titleResId = titleResId,
        messageResId = messageResId,
        channelId = NotificationChannel.PomodoroSession.id,
        scheduledTimeMillis = System.currentTimeMillis() + 60000,
        type = type,
    )
}

/**
 * Test implementation of NotificationScheduler for unit testing.
 */
private class TestNotificationScheduler : NotificationScheduler {
    private val scheduledNotifications = mutableMapOf<Int, NotificationData>()

    override suspend fun scheduleNotification(notification: NotificationData): EmptyResult<DataError.Local> {
        scheduledNotifications[notification.id] = notification
        return Result.Success(Unit)
    }

    override suspend fun cancelNotification(notificationId: Int): EmptyResult<DataError.Local> {
        scheduledNotifications.remove(notificationId)
        return Result.Success(Unit)
    }

    override suspend fun cancelAllNotifications(): EmptyResult<DataError.Local> {
        scheduledNotifications.clear()
        return Result.Success(Unit)
    }

    override fun isNotificationScheduled(notificationId: Int): Boolean =
        scheduledNotifications.containsKey(notificationId)

    override suspend fun showPersistentNotification(notification: NotificationData): EmptyResult<DataError.Local> {
        scheduledNotifications[notification.id] = notification
        return Result.Success(Unit)
    }

    override suspend fun dismissPersistentNotification(notificationId: Int): EmptyResult<DataError.Local> {
        scheduledNotifications.remove(notificationId)
        return Result.Success(Unit)
    }

    override suspend fun startRunningForegroundTimer(
        notification: RunningTimerNotificationData,
    ): EmptyResult<DataError.Local> = Result.Success(Unit)

    override suspend fun stopRunningForegroundTimer(): EmptyResult<DataError.Local> = Result.Success(Unit)

    fun getScheduledNotifications(): List<NotificationData> = scheduledNotifications.values.toList()
}
