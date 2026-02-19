package com.jujodevs.pomodoro

import android.app.Application
import com.google.firebase.FirebaseApp
import com.jujodevs.pomodoro.core.appconfig.AppConfig
import com.jujodevs.pomodoro.core.appconfig.impl.di.appConfigModule
import com.jujodevs.pomodoro.features.settings.di.settingsModule
import com.jujodevs.pomodoro.features.timer.di.timerModule
import com.jujodevs.pomodoro.libs.analytics.impl.di.analyticsModule
import com.jujodevs.pomodoro.libs.crashlytics.impl.di.crashlyticsModule
import com.jujodevs.pomodoro.libs.datastore.impl.di.dataStoreModule
import com.jujodevs.pomodoro.libs.logger.impl.LoggerInitializer
import com.jujodevs.pomodoro.libs.logger.impl.di.loggerModule
import com.jujodevs.pomodoro.libs.notifications.NotificationChannelManager
import com.jujodevs.pomodoro.libs.notifications.impl.di.notificationsModule
import com.jujodevs.pomodoro.libs.permissions.impl.di.permissionsModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext
import org.koin.core.context.startKoin

class PomodoroApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        // 1. Start Koin first to get AppConfig
        startKoin {
            androidContext(this@PomodoroApplication)
            modules(
                appConfigModule,
                loggerModule,
                analyticsModule,
                crashlyticsModule,
                dataStoreModule,
                notificationsModule,
                permissionsModule,
                timerModule,
                settingsModule
            )
        }

        // 2. Initialize Logger using AppConfig (must be done before other modules use it)
        val appConfig: AppConfig = GlobalContext.get().get()
        LoggerInitializer.initialize(appConfig.isDebug)

        // 3. Initialize Firebase
        FirebaseApp.initializeApp(this)

        // 4. Create notification channels
        val channelManager: NotificationChannelManager = GlobalContext.get().get()
        channelManager.createNotificationChannels()
    }
}
