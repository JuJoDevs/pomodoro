package com.jujodevs.pomodoro.di

import com.jujodevs.pomodoro.core.domain.coroutines.AppDispatchers
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import org.koin.dsl.module

val applicationScopeModule =
    module {
        single<CoroutineScope> {
            CoroutineScope(SupervisorJob())
        }
        single<AppDispatchers> { DefaultAppDispatchers }
    }

private object DefaultAppDispatchers : AppDispatchers {
    override val io: CoroutineDispatcher = Dispatchers.IO
    override val default: CoroutineDispatcher = Dispatchers.Default
    override val main: CoroutineDispatcher = Dispatchers.Main
}
