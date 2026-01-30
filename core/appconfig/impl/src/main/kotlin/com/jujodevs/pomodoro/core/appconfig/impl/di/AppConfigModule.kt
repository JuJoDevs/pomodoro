package com.jujodevs.pomodoro.core.appconfig.impl.di

import com.jujodevs.pomodoro.core.appconfig.AppConfig
import com.jujodevs.pomodoro.core.appconfig.impl.AppConfigImpl
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val appConfigModule = module {
    singleOf(::AppConfigImpl).bind<AppConfig>()
}
