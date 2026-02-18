package com.jujodevs.pomodoro.features.settings.di

import com.jujodevs.pomodoro.features.settings.domain.usecase.GetCanScheduleExactAlarmsUseCase
import com.jujodevs.pomodoro.features.settings.domain.usecase.GetCompletionAlarmSoundLabelUseCase
import com.jujodevs.pomodoro.features.settings.domain.usecase.GetHasNotificationPermissionUseCase
import com.jujodevs.pomodoro.features.settings.presentation.SettingsViewModel
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val settingsModule = module {
    factoryOf(::GetCanScheduleExactAlarmsUseCase)
    factoryOf(::GetHasNotificationPermissionUseCase)
    factoryOf(::GetCompletionAlarmSoundLabelUseCase)

    viewModel {
        SettingsViewModel(
            getCanScheduleExactAlarms = get(),
            getHasNotificationPermission = get(),
            getCompletionAlarmSoundLabel = get()
        )
    }
}
