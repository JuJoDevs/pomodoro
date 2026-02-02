package com.jujodevs.pomodoro.libs.notifications.impl.di

import android.app.AlarmManager
import android.content.Context
import com.jujodevs.pomodoro.libs.datastore.DataStoreManager
import com.jujodevs.pomodoro.libs.logger.Logger
import com.jujodevs.pomodoro.libs.notifications.NotificationChannelManager
import com.jujodevs.pomodoro.libs.notifications.NotificationScheduler
import com.jujodevs.pomodoro.libs.notifications.impl.NotificationChannelManagerImpl
import com.jujodevs.pomodoro.libs.notifications.impl.NotificationSchedulerImpl
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val notificationsModule = module {
    single<AlarmManager> {
        androidContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager
    }

    single<NotificationScheduler> {
        NotificationSchedulerImpl(
            context = androidContext(),
            alarmManager = get(),
            dataStoreManager = get<DataStoreManager>(),
            logger = get<Logger>()
        )
    }

    single<NotificationChannelManager> {
        NotificationChannelManagerImpl(androidContext())
    }
}
