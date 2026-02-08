package com.jujodevs.pomodoro.libs.notifications.impl

import android.app.AlarmManager
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import com.jujodevs.pomodoro.core.domain.util.Result
import com.jujodevs.pomodoro.core.domain.util.isSuccess
import com.jujodevs.pomodoro.libs.datastore.DataStoreManager
import com.jujodevs.pomodoro.libs.datastore.InternalStateKeys
import com.jujodevs.pomodoro.libs.datastore.impl.DataStoreManagerImpl
import com.jujodevs.pomodoro.libs.logger.Logger
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.amshove.kluent.shouldBe
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
        dataStoreManager.setValue(InternalStateKeys.SCHEDULED_NOTIFICATION_IDS, setOf("1", "2"))
            .isSuccess shouldBe true
        dataStoreManager.setValue(InternalStateKeys.ACTIVE_NOTIFICATION_TOKEN, "active-token")
            .isSuccess shouldBe true

        val storedIdsBefore = dataStoreManager.getValue(
            InternalStateKeys.SCHEDULED_NOTIFICATION_IDS,
            emptySet<String>()
        )
        storedIdsBefore shouldBeEqualTo Result.Success(setOf("1", "2"))

        // WHEN
        scheduler.cancelAllNotifications().isSuccess shouldBe true

        // THEN
        val storedIdsAfter = dataStoreManager.getValue(InternalStateKeys.SCHEDULED_NOTIFICATION_IDS, emptySet<String>())
        storedIdsAfter shouldBeEqualTo Result.Success(emptySet())
        val activeTokenAfter = dataStoreManager.getValue(InternalStateKeys.ACTIVE_NOTIFICATION_TOKEN, "default")
        activeTokenAfter shouldBeEqualTo Result.Success("default")
    }

    @Test
    fun `GIVEN no IDs stored WHEN cancelAllNotifications THEN should be idempotent`() = runTest {
        // GIVEN
        val storedIdsBefore = dataStoreManager.getValue(
            InternalStateKeys.SCHEDULED_NOTIFICATION_IDS,
            emptySet<String>()
        )
        storedIdsBefore shouldBeEqualTo Result.Success(emptySet())

        // WHEN
        scheduler.cancelAllNotifications().isSuccess shouldBe true

        // THEN
        val storedIdsAfter = dataStoreManager.getValue(InternalStateKeys.SCHEDULED_NOTIFICATION_IDS, emptySet<String>())
        storedIdsAfter shouldBeEqualTo Result.Success(emptySet())
    }
}
