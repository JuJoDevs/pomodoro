package com.jujodevs.pomodoro.libs.notifications.impl

import android.app.AlarmManager
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import com.jujodevs.pomodoro.libs.datastore.DataStoreManager
import com.jujodevs.pomodoro.libs.datastore.InternalStateKeys
import com.jujodevs.pomodoro.libs.datastore.impl.DataStoreManagerImpl
import com.jujodevs.pomodoro.libs.logger.Logger
import com.jujodevs.pomodoro.libs.notifications.NotificationChannel
import com.jujodevs.pomodoro.libs.notifications.NotificationData
import com.jujodevs.pomodoro.libs.notifications.NotificationType
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.amshove.kluent.shouldBeEqualTo
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import java.io.File

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
class NotificationSchedulerIntegrationTest {

    private lateinit var context: Context
    private lateinit var alarmManager: AlarmManager
    private lateinit var dataStoreManager: DataStoreManager
    private lateinit var logger: Logger
    private lateinit var scheduler: NotificationSchedulerImpl
    private lateinit var dataStore: DataStore<Preferences>

    @Before
    fun setUp() {
        context = RuntimeEnvironment.getApplication()
        alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        logger = mockk(relaxed = true)

        val testDispatcher = UnconfinedTestDispatcher()
        val testScope = TestScope(testDispatcher)
        dataStore = PreferenceDataStoreFactory.create(
            scope = testScope,
            produceFile = { File.createTempFile("test_datastore", ".preferences_pb") }
        )
        dataStoreManager = DataStoreManagerImpl(dataStore)

        scheduler = NotificationSchedulerImpl(
            context = context,
            alarmManager = alarmManager,
            dataStoreManager = dataStoreManager,
            logger = logger
        )
    }

    @Test
    fun `GIVEN IDs stored WHEN cancelAllNotifications THEN should clear DataStore`() = runTest {
        // GIVEN
        val notification1 = createTestNotification(id = 1)
        val notification2 = createTestNotification(id = 2)
        scheduler.scheduleNotification(notification1)
        scheduler.scheduleNotification(notification2)

        val storedIdsBefore = dataStoreManager.getValue(
            InternalStateKeys.SCHEDULED_NOTIFICATION_IDS,
            emptySet<String>()
        )
        storedIdsBefore shouldBeEqualTo setOf("1", "2")

        // WHEN
        scheduler.cancelAllNotifications()

        // THEN
        val storedIdsAfter = dataStoreManager.getValue(InternalStateKeys.SCHEDULED_NOTIFICATION_IDS, emptySet<String>())
        storedIdsAfter shouldBeEqualTo emptySet()
    }

    @Test
    fun `GIVEN no IDs stored WHEN cancelAllNotifications THEN should be idempotent`() = runTest {
        // GIVEN
        val storedIdsBefore = dataStoreManager.getValue(
            InternalStateKeys.SCHEDULED_NOTIFICATION_IDS,
            emptySet<String>()
        )
        storedIdsBefore shouldBeEqualTo emptySet()

        // WHEN
        scheduler.cancelAllNotifications()

        // THEN
        val storedIdsAfter = dataStoreManager.getValue(InternalStateKeys.SCHEDULED_NOTIFICATION_IDS, emptySet<String>())
        storedIdsAfter shouldBeEqualTo emptySet()
    }

    private fun createTestNotification(
        id: Int,
        title: String = "Test Notification",
        message: String = "Test message",
        type: NotificationType = NotificationType.WORK_SESSION_COMPLETE
    ) = NotificationData(
        id = id,
        title = title,
        message = message,
        channelId = NotificationChannel.PomodoroSession.id,
        scheduledTimeMillis = System.currentTimeMillis() + 60000,
        type = type
    )
}
