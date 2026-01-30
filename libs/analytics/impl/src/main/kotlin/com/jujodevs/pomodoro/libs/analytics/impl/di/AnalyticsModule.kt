package com.jujodevs.pomodoro.libs.analytics.impl.di

import android.content.Context
import com.google.firebase.analytics.FirebaseAnalytics
import com.jujodevs.pomodoro.libs.analytics.AnalyticsTracker
import com.jujodevs.pomodoro.libs.analytics.impl.FirebaseAnalyticsTracker
import com.jujodevs.pomodoro.libs.analytics.impl.FirebaseAnalyticsWrapper
import com.jujodevs.pomodoro.libs.analytics.impl.FirebaseAnalyticsWrapperImpl
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val analyticsModule = module {
    single { FirebaseAnalytics.getInstance(get<Context>()) }
    singleOf(::FirebaseAnalyticsWrapperImpl).bind<FirebaseAnalyticsWrapper>()
    singleOf(::FirebaseAnalyticsTracker).bind<AnalyticsTracker>()
}
