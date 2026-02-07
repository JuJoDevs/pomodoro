package com.jujodevs.pomodoro.features.timer.di

import com.jujodevs.pomodoro.features.timer.presentation.TimerViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val timerModule = module {
    viewModelOf(::TimerViewModel)
}
