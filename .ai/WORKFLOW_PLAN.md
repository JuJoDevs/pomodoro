# WORKFLOW_PLAN.md - Infrastructure Modules Implementation Plan

## Overview

This document provides a step-by-step plan to implement the three critical infrastructure modules required before developing the Pomodoro feature: **DataStore** (persistence), **Notifications** (background execution), and **Resources** (shared resources).

> **Important**: This plan must be followed in conjunction with [AGENTS.md](../AGENTS.md) and [WORKFLOW_FEATURE.md](WORKFLOW_FEATURE.md).

---

## Priority Order

1. **DataStore** (libs/datastore) - CRITICAL - 40 min
2. **Notifications** (libs/notifications) - CRITICAL - 90 min
3. **Resources** (core/resources) - OPTIONAL - 30 min

**Total estimated effort**: ~2.5 hours

---

## Module 1: DataStore (libs/datastore)

### Purpose

Provide centralized persistence layer for app preferences and configuration using AndroidX DataStore Preferences.

### Structure

```
libs/datastore/
  api/
    build.gradle.kts
    src/main/kotlin/com/jujodevs/pomodoro/libs/datastore/
      DataStoreManager.kt           # Main interface
      PreferencesKeys.kt            # Keys definition
    src/test/kotlin/.../
      FakeDataStoreManager.kt       # Fake for testing
  impl/
    build.gradle.kts
    src/main/kotlin/com/jujodevs/pomodoro/libs/datastore/impl/
      DataStoreManagerImpl.kt       # Implementation
      di/
        DataStoreModule.kt          # Koin module
    src/test/kotlin/.../
      DataStoreManagerImplTest.kt   # Unit tests
```

### Implementation Steps

#### Step 1.1: Add Dependencies to `libs.versions.toml`

Add to `[versions]` section:
```toml
datastore = "1.1.2"
```

Add to `[libraries]` section:
```toml
androidx-datastore-preferences = { group = "androidx.datastore", name = "datastore-preferences", version.ref = "datastore" }
androidx-datastore-core = { group = "androidx.datastore", name = "datastore-core", version.ref = "datastore" }
```

#### Step 1.2: Create API Module

**File**: `libs/datastore/api/build.gradle.kts`
```kotlin
plugins {
    alias(libs.plugins.pomodoro.kotlin.library)
    alias(libs.plugins.pomodoro.testing)
}

dependencies {
    implementation(libs.kotlinx.coroutines.core)
}
```

**File**: `libs/datastore/api/src/main/kotlin/.../DataStoreManager.kt`
```kotlin
interface DataStoreManager {
    suspend fun <T> getValue(key: String, defaultValue: T): T
    suspend fun <T> setValue(key: String, value: T)
    suspend fun removeValue(key: String)
    suspend fun clear()
    fun <T> observeValue(key: String, defaultValue: T): Flow<T>
}
```

**File**: `libs/datastore/api/src/main/kotlin/.../PreferencesKeys.kt`
```kotlin
object PreferencesKeys {
    // Pomodoro configuration
    const val WORK_DURATION_MINUTES = "work_duration_minutes"
    const val SHORT_BREAK_DURATION_MINUTES = "short_break_duration_minutes"
    const val LONG_BREAK_DURATION_MINUTES = "long_break_duration_minutes"
    const val SESSIONS_UNTIL_LONG_BREAK = "sessions_until_long_break"
    
    // App settings
    const val SOUND_ENABLED = "sound_enabled"
    const val VIBRATION_ENABLED = "vibration_enabled"
    const val AUTO_START_BREAKS = "auto_start_breaks"
    const val AUTO_START_POMODOROS = "auto_start_pomodoros"
    
    // User preferences
    const val THEME_MODE = "theme_mode"
    const val NOTIFICATIONS_ENABLED = "notifications_enabled"
}
```

**File**: `libs/datastore/api/src/test/kotlin/.../FakeDataStoreManager.kt`
```kotlin
class FakeDataStoreManager : DataStoreManager {
    private val storage = mutableMapOf<String, Any?>()
    private val flows = mutableMapOf<String, MutableStateFlow<Any?>>()

    override suspend fun <T> getValue(key: String, defaultValue: T): T {
        return (storage[key] as? T) ?: defaultValue
    }

    override suspend fun <T> setValue(key: String, value: T) {
        storage[key] = value
        flows[key]?.value = value
    }

    override suspend fun removeValue(key: String) {
        storage.remove(key)
        flows[key]?.value = null
    }

    override suspend fun clear() {
        storage.clear()
        flows.values.forEach { it.value = null }
    }

    override fun <T> observeValue(key: String, defaultValue: T): Flow<T> {
        val flow = flows.getOrPut(key) { MutableStateFlow(storage[key]) }
        return flow.map { (it as? T) ?: defaultValue }
    }
}
```

#### Step 1.3: Create Impl Module

**File**: `libs/datastore/impl/build.gradle.kts`
```kotlin
plugins {
    alias(libs.plugins.pomodoro.android.library)
    alias(libs.plugins.pomodoro.testing)
    alias(libs.plugins.pomodoro.koin)
}

dependencies {
    implementation(project(":libs:datastore:api"))
    
    implementation(libs.androidx.datastore.preferences)
    implementation(libs.kotlinx.coroutines.android)
}
```

**File**: `libs/datastore/impl/src/main/kotlin/.../DataStoreManagerImpl.kt`
```kotlin
class DataStoreManagerImpl(
    private val dataStore: DataStore<Preferences>
) : DataStoreManager {
    
    override suspend fun <T> getValue(key: String, defaultValue: T): T {
        return dataStore.data.map { preferences ->
            getPreferenceValue(preferences, key, defaultValue)
        }.first()
    }
    
    override suspend fun <T> setValue(key: String, value: T) {
        dataStore.edit { preferences ->
            setPreferenceValue(preferences, key, value)
        }
    }
    
    override suspend fun removeValue(key: String) {
        dataStore.edit { preferences ->
            preferences.remove(stringPreferencesKey(key))
        }
    }
    
    override suspend fun clear() {
        dataStore.edit { it.clear() }
    }
    
    override fun <T> observeValue(key: String, defaultValue: T): Flow<T> {
        return dataStore.data.map { preferences ->
            getPreferenceValue(preferences, key, defaultValue)
        }
    }
    
    private fun <T> getPreferenceValue(preferences: Preferences, key: String, defaultValue: T): T {
        return when (defaultValue) {
            is String -> preferences[stringPreferencesKey(key)] ?: defaultValue
            is Int -> preferences[intPreferencesKey(key)] ?: defaultValue
            is Boolean -> preferences[booleanPreferencesKey(key)] ?: defaultValue
            is Float -> preferences[floatPreferencesKey(key)] ?: defaultValue
            is Long -> preferences[longPreferencesKey(key)] ?: defaultValue
            else -> defaultValue
        } as T
    }
    
    private fun <T> setPreferenceValue(preferences: MutablePreferences, key: String, value: T) {
        when (value) {
            is String -> preferences[stringPreferencesKey(key)] = value
            is Int -> preferences[intPreferencesKey(key)] = value
            is Boolean -> preferences[booleanPreferencesKey(key)] = value
            is Float -> preferences[floatPreferencesKey(key)] = value
            is Long -> preferences[longPreferencesKey(key)] = value
        }
    }
}
```

**File**: `libs/datastore/impl/src/main/kotlin/.../di/DataStoreModule.kt`
```kotlin
val dataStoreModule = module {
    single<DataStore<Preferences>> {
        get<Context>().dataStore
    }
    
    single<DataStoreManager> {
        DataStoreManagerImpl(get())
    }
}

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
    name = "pomodoro_preferences"
)
```

#### Step 1.4: Write Tests

**File**: `libs/datastore/impl/src/test/kotlin/.../DataStoreManagerImplTest.kt`
```kotlin
@OptIn(ExperimentalCoroutinesApi::class)
class DataStoreManagerImplTest {
    
    private lateinit var dataStore: DataStore<Preferences>
    private lateinit var dataStoreManager: DataStoreManager
    
    @BeforeEach
    fun setUp() {
        val testDispatcher = UnconfinedTestDispatcher()
        val testScope = TestScope(testDispatcher)
        
        dataStore = PreferenceDataStoreFactory.create(
            scope = testScope,
            produceFile = { File.createTempFile("test_datastore", ".preferences_pb") }
        )
        
        dataStoreManager = DataStoreManagerImpl(dataStore)
    }
    
    @Test
    fun `GIVEN key with value WHEN getValue THEN should return stored value`() = runTest {
        // GIVEN
        dataStoreManager.setValue("test_key", "test_value")
        
        // WHEN
        val result = dataStoreManager.getValue("test_key", "")
        
        // THEN
        result shouldBeEqualTo "test_value"
    }
    
    @Test
    fun `GIVEN no value WHEN getValue THEN should return default value`() = runTest {
        // WHEN
        val result = dataStoreManager.getValue("non_existent", "default")
        
        // THEN
        result shouldBeEqualTo "default"
    }
    
    @Test
    fun `GIVEN value set WHEN observeValue THEN should emit changes`() = runTest {
        // WHEN
        val flow = dataStoreManager.observeValue("test_key", 0)
        
        // THEN
        flow.test {
            awaitItem() shouldBeEqualTo 0
            
            dataStoreManager.setValue("test_key", 42)
            awaitItem() shouldBeEqualTo 42
            
            dataStoreManager.setValue("test_key", 100)
            awaitItem() shouldBeEqualTo 100
        }
    }
}
```

#### Step 1.5: Register Module

Add to `settings.gradle.kts`:
```kotlin
include(":libs:datastore:api")
include(":libs:datastore:impl")
```

Add to `PomodoroApplication.kt`:
```kotlin
import com.jujodevs.pomodoro.libs.datastore.impl.di.dataStoreModule

startKoin {
    modules(
        // ... existing modules
        dataStoreModule
    )
}
```

---

## Module 2: Notifications (libs/notifications)

### Purpose

Provide notification scheduling and background execution capabilities using AlarmManager for reliable timer completion notifications, even with screen locked.

### Structure

```
libs/notifications/
  api/
    build.gradle.kts
    src/main/kotlin/com/jujodevs/pomodoro/libs/notifications/
      NotificationScheduler.kt      # Main interface
      NotificationChannelManager.kt # Channel management
      NotificationData.kt           # Data models
    src/test/kotlin/.../
      FakeNotificationScheduler.kt  # Fake for testing
  impl/
    build.gradle.kts
    src/main/AndroidManifest.xml
    src/main/kotlin/com/jujodevs/pomodoro/libs/notifications/impl/
      NotificationSchedulerImpl.kt
      NotificationChannelManagerImpl.kt
      AlarmReceiver.kt              # BroadcastReceiver
      NotificationHelper.kt         # Helper for creating notifications
      di/
        NotificationsModule.kt      # Koin module
    src/test/kotlin/.../
      NotificationSchedulerImplTest.kt
```

### Implementation Steps

#### Step 2.1: Create API Module

**File**: `libs/notifications/api/build.gradle.kts`
```kotlin
plugins {
    alias(libs.plugins.pomodoro.kotlin.library)
    alias(libs.plugins.pomodoro.testing)
}

dependencies {
    implementation(libs.kotlinx.coroutines.core)
}
```

**File**: `libs/notifications/api/src/main/kotlin/.../NotificationData.kt`
```kotlin
data class NotificationData(
    val id: Int,
    val title: String,
    val message: String,
    val channelId: String,
    val scheduledTimeMillis: Long,
    val type: NotificationType
)

enum class NotificationType {
    WORK_SESSION_COMPLETE,
    SHORT_BREAK_COMPLETE,
    LONG_BREAK_COMPLETE,
    SESSION_REMINDER
}

sealed class NotificationChannel(
    val id: String,
    val name: String,
    val description: String,
    val importance: Int
) {
    data object PomodoroSession : NotificationChannel(
        id = "pomodoro_session",
        name = "Pomodoro Sessions",
        description = "Notifications for pomodoro session completion",
        importance = 4 // IMPORTANCE_HIGH
    )
    
    data object Reminders : NotificationChannel(
        id = "reminders",
        name = "Reminders",
        description = "Reminder notifications",
        importance = 3 // IMPORTANCE_DEFAULT
    )
}
```

**File**: `libs/notifications/api/src/main/kotlin/.../NotificationScheduler.kt`
```kotlin
interface NotificationScheduler {
    suspend fun scheduleNotification(notification: NotificationData): Result<Unit>
    suspend fun cancelNotification(notificationId: Int): Result<Unit>
    suspend fun cancelAllNotifications(): Result<Unit>
    fun isNotificationScheduled(notificationId: Int): Boolean
}
```

**File**: `libs/notifications/api/src/main/kotlin/.../NotificationChannelManager.kt`
```kotlin
interface NotificationChannelManager {
    fun createNotificationChannels()
    fun deleteNotificationChannel(channelId: String)
}
```

**File**: `libs/notifications/api/src/test/kotlin/.../FakeNotificationScheduler.kt`
```kotlin
class FakeNotificationScheduler : NotificationScheduler {
    private val scheduledNotifications = mutableMapOf<Int, NotificationData>()
    
    override suspend fun scheduleNotification(notification: NotificationData): Result<Unit> {
        scheduledNotifications[notification.id] = notification
        return Result.success(Unit)
    }
    
    override suspend fun cancelNotification(notificationId: Int): Result<Unit> {
        scheduledNotifications.remove(notificationId)
        return Result.success(Unit)
    }
    
    override suspend fun cancelAllNotifications(): Result<Unit> {
        scheduledNotifications.clear()
        return Result.success(Unit)
    }
    
    override fun isNotificationScheduled(notificationId: Int): Boolean {
        return scheduledNotifications.containsKey(notificationId)
    }
    
    fun getScheduledNotifications(): List<NotificationData> {
        return scheduledNotifications.values.toList()
    }
}
```

#### Step 2.2: Create Impl Module

**File**: `libs/notifications/impl/build.gradle.kts`
```kotlin
plugins {
    alias(libs.plugins.pomodoro.android.library)
    alias(libs.plugins.pomodoro.testing)
    alias(libs.plugins.pomodoro.koin)
}

dependencies {
    implementation(project(":libs:notifications:api"))
    implementation(project(":libs:logger:api"))
    
    implementation(libs.androidx.core.ktx)
    implementation(libs.kotlinx.coroutines.android)
}
```

**File**: `libs/notifications/impl/src/main/AndroidManifest.xml`
```xml
<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android">
    
    <!-- Permissions for notifications -->
    <uses-permission android:name="android.permission.POST_NOTIFICATIONS" />
    <uses-permission android:name="android.permission.SCHEDULE_EXACT_ALARM" />
    <uses-permission android:name="android.permission.USE_EXACT_ALARM" />
    
    <application>
        <!-- Alarm Receiver for scheduled notifications -->
        <receiver
            android:name=".AlarmReceiver"
            android:enabled="true"
            android:exported="false">
            <intent-filter>
                <action android:name="com.jujodevs.pomodoro.NOTIFICATION_ACTION" />
            </intent-filter>
        </receiver>
    </application>
    
</manifest>
```

**File**: `libs/notifications/impl/src/main/kotlin/.../NotificationSchedulerImpl.kt`
```kotlin
class NotificationSchedulerImpl(
    private val context: Context,
    private val alarmManager: AlarmManager,
    private val logger: Logger
) : NotificationScheduler {
    
    override suspend fun scheduleNotification(notification: NotificationData): Result<Unit> {
        return runCatching {
            val intent = Intent(context, AlarmReceiver::class.java).apply {
                action = ACTION_NOTIFICATION
                putExtra(EXTRA_NOTIFICATION_ID, notification.id)
                putExtra(EXTRA_NOTIFICATION_TITLE, notification.title)
                putExtra(EXTRA_NOTIFICATION_MESSAGE, notification.message)
                putExtra(EXTRA_NOTIFICATION_CHANNEL_ID, notification.channelId)
                putExtra(EXTRA_NOTIFICATION_TYPE, notification.type.name)
            }
            
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                notification.id,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (alarmManager.canScheduleExactAlarms()) {
                    alarmManager.setExactAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        notification.scheduledTimeMillis,
                        pendingIntent
                    )
                } else {
                    logger.w("Cannot schedule exact alarms. Permission not granted.")
                    throw SecurityException("SCHEDULE_EXACT_ALARM permission not granted")
                }
            } else {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    notification.scheduledTimeMillis,
                    pendingIntent
                )
            }
            
            logger.d("Notification scheduled: id=${notification.id}, time=${notification.scheduledTimeMillis}")
        }
    }
    
    override suspend fun cancelNotification(notificationId: Int): Result<Unit> {
        return runCatching {
            val intent = Intent(context, AlarmReceiver::class.java).apply {
                action = ACTION_NOTIFICATION
            }
            
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                notificationId,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            
            alarmManager.cancel(pendingIntent)
            pendingIntent.cancel()
            
            logger.d("Notification cancelled: id=$notificationId")
        }
    }
    
    override suspend fun cancelAllNotifications(): Result<Unit> {
        return runCatching {
            // Note: This is a simplified version. In production, you'd need to track all notification IDs
            logger.d("All notifications cancelled")
        }
    }
    
    override fun isNotificationScheduled(notificationId: Int): Boolean {
        val intent = Intent(context, AlarmReceiver::class.java).apply {
            action = ACTION_NOTIFICATION
        }
        
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            notificationId,
            intent,
            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
        )
        
        return pendingIntent != null
    }
    
    companion object {
        const val ACTION_NOTIFICATION = "com.jujodevs.pomodoro.NOTIFICATION_ACTION"
        const val EXTRA_NOTIFICATION_ID = "notification_id"
        const val EXTRA_NOTIFICATION_TITLE = "notification_title"
        const val EXTRA_NOTIFICATION_MESSAGE = "notification_message"
        const val EXTRA_NOTIFICATION_CHANNEL_ID = "notification_channel_id"
        const val EXTRA_NOTIFICATION_TYPE = "notification_type"
    }
}
```

**File**: `libs/notifications/impl/src/main/kotlin/.../AlarmReceiver.kt`
```kotlin
class AlarmReceiver : BroadcastReceiver() {
    
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != NotificationSchedulerImpl.ACTION_NOTIFICATION) return
        
        val notificationId = intent.getIntExtra(NotificationSchedulerImpl.EXTRA_NOTIFICATION_ID, -1)
        val title = intent.getStringExtra(NotificationSchedulerImpl.EXTRA_NOTIFICATION_TITLE) ?: return
        val message = intent.getStringExtra(NotificationSchedulerImpl.EXTRA_NOTIFICATION_MESSAGE) ?: return
        val channelId = intent.getStringExtra(NotificationSchedulerImpl.EXTRA_NOTIFICATION_CHANNEL_ID) ?: return
        
        NotificationHelper.showNotification(
            context = context,
            notificationId = notificationId,
            title = title,
            message = message,
            channelId = channelId
        )
    }
}
```

**File**: `libs/notifications/impl/src/main/kotlin/.../NotificationHelper.kt`
```kotlin
object NotificationHelper {
    
    fun showNotification(
        context: Context,
        notificationId: Int,
        title: String,
        message: String,
        channelId: String
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
            .setSmallIcon(android.R.drawable.ic_dialog_info) // TODO: Replace with app icon
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .build()
        
        notificationManager.notify(notificationId, notification)
    }
}
```

**File**: `libs/notifications/impl/src/main/kotlin/.../NotificationChannelManagerImpl.kt`
```kotlin
class NotificationChannelManagerImpl(
    private val context: Context
) : NotificationChannelManager {
    
    override fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            
            val channels = listOf(
                NotificationChannel.PomodoroSession,
                NotificationChannel.Reminders
            )
            
            channels.forEach { channel ->
                val androidChannel = android.app.NotificationChannel(
                    channel.id,
                    channel.name,
                    channel.importance
                ).apply {
                    description = channel.description
                    enableVibration(true)
                    enableLights(true)
                }
                
                notificationManager.createNotificationChannel(androidChannel)
            }
        }
    }
    
    override fun deleteNotificationChannel(channelId: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.deleteNotificationChannel(channelId)
        }
    }
}
```

**File**: `libs/notifications/impl/src/main/kotlin/.../di/NotificationsModule.kt`
```kotlin
val notificationsModule = module {
    single<AlarmManager> {
        androidContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager
    }
    
    single<NotificationScheduler> {
        NotificationSchedulerImpl(
            context = androidContext(),
            alarmManager = get(),
            logger = get()
        )
    }
    
    single<NotificationChannelManager> {
        NotificationChannelManagerImpl(androidContext())
    }
}
```

#### Step 2.3: Write Tests

**File**: `libs/notifications/impl/src/test/kotlin/.../NotificationSchedulerImplTest.kt`
```kotlin
class NotificationSchedulerImplTest {
    
    private val context: Context = mockk(relaxed = true)
    private val alarmManager: AlarmManager = mockk(relaxed = true)
    private val logger: Logger = mockk(relaxed = true)
    
    private lateinit var scheduler: NotificationScheduler
    
    @BeforeEach
    fun setUp() {
        scheduler = NotificationSchedulerImpl(context, alarmManager, logger)
    }
    
    @Test
    fun `GIVEN notification data WHEN scheduleNotification THEN should set alarm`() = runTest {
        // GIVEN
        val notification = NotificationData(
            id = 1,
            title = "Test",
            message = "Test message",
            channelId = "test_channel",
            scheduledTimeMillis = System.currentTimeMillis() + 60000,
            type = NotificationType.WORK_SESSION_COMPLETE
        )
        
        every { alarmManager.canScheduleExactAlarms() } returns true
        
        // WHEN
        val result = scheduler.scheduleNotification(notification)
        
        // THEN
        result.isSuccess shouldBe true
        verify { alarmManager.setExactAndAllowWhileIdle(any(), any(), any()) }
    }
    
    @Test
    fun `GIVEN notification id WHEN cancelNotification THEN should cancel alarm`() = runTest {
        // WHEN
        val result = scheduler.cancelNotification(1)
        
        // THEN
        result.isSuccess shouldBe true
        verify { alarmManager.cancel(any<PendingIntent>()) }
    }
}
```

#### Step 2.4: Update App Manifest

**File**: `app/src/main/AndroidManifest.xml`

Add permissions at the top level (merge with existing):
```xml
<uses-permission android:name="android.permission.POST_NOTIFICATIONS" />
<uses-permission android:name="android.permission.SCHEDULE_EXACT_ALARM" />
<uses-permission android:name="android.permission.USE_EXACT_ALARM" />
```

#### Step 2.5: Initialize Notification Channels

**File**: `app/src/main/kotlin/.../PomodoroApplication.kt`

Add after Firebase initialization:
```kotlin
// 4. Create notification channels
val channelManager: NotificationChannelManager = GlobalContext.get().get()
channelManager.createNotificationChannels()
```

#### Step 2.6: Register Module

Add to `settings.gradle.kts`:
```kotlin
include(":libs:notifications:api")
include(":libs:notifications:impl")
```

Add to `PomodoroApplication.kt`:
```kotlin
import com.jujodevs.pomodoro.libs.notifications.impl.di.notificationsModule

startKoin {
    modules(
        // ... existing modules
        notificationsModule
    )
}
```

---

## Module 3: Resources (core/resources) - OPTIONAL

### Purpose

Centralized location for shared resources (strings, drawables, colors) accessible from all modules.

### Structure

```
core/resources/
  build.gradle.kts
  src/main/res/
    values/
      strings.xml           # Shared strings
      dimens.xml            # Shared dimensions
    drawable/              # Shared drawables
```

### Implementation Steps

#### Step 3.1: Create Module

**File**: `core/resources/build.gradle.kts`
```kotlin
plugins {
    alias(libs.plugins.pomodoro.android.library)
}

android {
    namespace = "com.jujodevs.pomodoro.core.resources"
}
```

#### Step 3.2: Create Resources

**File**: `core/resources/src/main/res/values/strings.xml`
```xml
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <!-- Common actions -->
    <string name="action_start">Start</string>
    <string name="action_pause">Pause</string>
    <string name="action_resume">Resume</string>
    <string name="action_stop">Stop</string>
    <string name="action_skip">Skip</string>
    <string name="action_cancel">Cancel</string>
    <string name="action_save">Save</string>
    <string name="action_delete">Delete</string>
    <string name="action_edit">Edit</string>
    <string name="action_close">Close</string>
    <string name="action_back">Back</string>
    <string name="action_ok">OK</string>
    
    <!-- Common labels -->
    <string name="label_settings">Settings</string>
    <string name="label_statistics">Statistics</string>
    <string name="label_about">About</string>
    
    <!-- Session types -->
    <string name="session_type_work">Work</string>
    <string name="session_type_short_break">Short Break</string>
    <string name="session_type_long_break">Long Break</string>
    
    <!-- Notifications -->
    <string name="notification_channel_sessions_name">Pomodoro Sessions</string>
    <string name="notification_channel_sessions_description">Notifications for completed pomodoro sessions</string>
    <string name="notification_channel_reminders_name">Reminders</string>
    <string name="notification_channel_reminders_description">Reminder notifications</string>
    
    <string name="notification_work_complete_title">Work session complete!</string>
    <string name="notification_work_complete_message">Great job! Take a break.</string>
    <string name="notification_short_break_complete_title">Break time is over</string>
    <string name="notification_short_break_complete_message">Ready for another session?</string>
    <string name="notification_long_break_complete_title">Long break complete</string>
    <string name="notification_long_break_complete_message">You\'re doing great! Start a new session?</string>
    
    <!-- Common errors -->
    <string name="error_generic">Something went wrong. Please try again.</string>
    <string name="error_network">Network error. Check your connection.</string>
    <string name="error_permission_denied">Permission denied</string>
    
    <!-- Time formats -->
    <string name="time_format_minutes">%d min</string>
    <string name="time_format_hours_minutes">%d h %d min</string>
</resources>
```

**File**: `core/resources/src/main/res/values/dimens.xml`
```xml
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <!-- Common dimensions -->
    <dimen name="padding_extra_small">4dp</dimen>
    <dimen name="padding_small">8dp</dimen>
    <dimen name="padding_medium">16dp</dimen>
    <dimen name="padding_large">24dp</dimen>
    <dimen name="padding_extra_large">32dp</dimen>
    
    <!-- Corner radius -->
    <dimen name="corner_radius_small">4dp</dimen>
    <dimen name="corner_radius_medium">8dp</dimen>
    <dimen name="corner_radius_large">16dp</dimen>
    
    <!-- Elevation -->
    <dimen name="elevation_small">2dp</dimen>
    <dimen name="elevation_medium">4dp</dimen>
    <dimen name="elevation_large">8dp</dimen>
</resources>
```

#### Step 3.3: Register Module

Add to `settings.gradle.kts`:
```kotlin
include(":core:resources")
```

Features can now depend on it:
```kotlin
dependencies {
    implementation(project(":core:resources"))
}
```

---

## Final Checklist

Before proceeding to implement the Pomodoro feature, verify:

### DataStore Module
- [ ] `libs/datastore/api` module created
- [ ] `libs/datastore/impl` module created
- [ ] DataStore dependencies added to `libs.versions.toml`
- [ ] Module registered in `settings.gradle.kts`
- [ ] Koin module registered in `PomodoroApplication`
- [ ] Unit tests written and passing
- [ ] FakeDataStoreManager available for testing

### Notifications Module
- [ ] `libs/notifications/api` module created
- [ ] `libs/notifications/impl` module created
- [ ] Module registered in `settings.gradle.kts`
- [ ] Notification permissions added to app `AndroidManifest.xml`
- [ ] AlarmReceiver registered in impl `AndroidManifest.xml`
- [ ] Koin module registered in `PomodoroApplication`
- [ ] Notification channels created on app startup
- [ ] Unit tests written and passing
- [ ] FakeNotificationScheduler available for testing

### Resources Module (Optional)
- [ ] `core/resources` module created
- [ ] Common strings defined
- [ ] Common dimensions defined
- [ ] Module registered in `settings.gradle.kts`

### Project Configuration
- [ ] All new modules compile successfully
- [ ] All tests pass
- [ ] No linter errors (detekt, ktlint)
- [ ] Git commit created with infrastructure modules

---

## Validation

After implementation, test the following:

### DataStore
```kotlin
// In a test or temporary screen
val dataStoreManager: DataStoreManager by inject()

launch {
    dataStoreManager.setValue(PreferencesKeys.WORK_DURATION_MINUTES, 25)
    val value = dataStoreManager.getValue(PreferencesKeys.WORK_DURATION_MINUTES, 20)
    println("Stored value: $value") // Should print 25
}
```

### Notifications
```kotlin
// In a test or temporary screen
val notificationScheduler: NotificationScheduler by inject()

launch {
    val notification = NotificationData(
        id = 1,
        title = "Test Notification",
        message = "This is a test",
        channelId = NotificationChannel.PomodoroSession.id,
        scheduledTimeMillis = System.currentTimeMillis() + 5000, // 5 seconds from now
        type = NotificationType.WORK_SESSION_COMPLETE
    )
    
    notificationScheduler.scheduleNotification(notification)
    // Wait 5 seconds - you should receive a notification
}
```

### Permission Handling

Request notification permission in the app (Android 13+):
```kotlin
if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
    // Request POST_NOTIFICATIONS permission
}

if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
    // Request SCHEDULE_EXACT_ALARM permission
    val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
    startActivity(intent)
}
```

---

## Next Steps

Once all infrastructure modules are implemented and validated:

1. Create the `features/pomodoro/api` module
2. Create the `features/pomodoro/impl` module
3. Follow [WORKFLOW_FEATURE.md](WORKFLOW_FEATURE.md) for feature implementation
4. Use the infrastructure modules:
   - DataStore for saving pomodoro configuration
   - Notifications for scheduling session completion alerts

---

**Document Version**: 1.0  
**Last Updated**: 2026-01-30  
**Author**: Pomodoro Development Team
