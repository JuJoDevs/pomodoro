package com.jujodevs.pomodoro.libs.crashlytics.impl.di

import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.jujodevs.pomodoro.libs.crashlytics.CrashReporter
import com.jujodevs.pomodoro.libs.crashlytics.impl.FirebaseCrashReporter
import com.jujodevs.pomodoro.libs.crashlytics.impl.FirebaseCrashlyticsWrapper
import com.jujodevs.pomodoro.libs.crashlytics.impl.FirebaseCrashlyticsWrapperImpl
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val crashlyticsModule =
    module {
        single { FirebaseCrashlytics.getInstance() }
        singleOf(::FirebaseCrashlyticsWrapperImpl).bind<FirebaseCrashlyticsWrapper>()
        singleOf(::FirebaseCrashReporter).bind<CrashReporter>()
    }
