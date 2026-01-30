package com.jujodevs.pomodoro.libs.logger.impl.di

import com.jujodevs.pomodoro.libs.logger.Logger
import com.jujodevs.pomodoro.libs.logger.impl.TimberLogger
import org.koin.dsl.module

val loggerModule = module {
    single<Logger> { TimberLogger() }
}
