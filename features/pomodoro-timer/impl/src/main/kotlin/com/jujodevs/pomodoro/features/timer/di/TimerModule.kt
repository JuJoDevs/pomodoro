package com.jujodevs.pomodoro.features.timer.di

import com.jujodevs.pomodoro.features.timer.data.PomodoroRepositoryImpl
import com.jujodevs.pomodoro.features.timer.data.SystemTimeProvider
import com.jujodevs.pomodoro.features.timer.domain.provider.TimeProvider
import com.jujodevs.pomodoro.features.timer.domain.repository.PomodoroRepository
import com.jujodevs.pomodoro.features.timer.domain.usecase.AdvancePomodoroPhaseUseCase
import com.jujodevs.pomodoro.features.timer.domain.usecase.ObservePomodoroSessionStateUseCase
import com.jujodevs.pomodoro.features.timer.domain.usecase.PausePomodoroUseCase
import com.jujodevs.pomodoro.features.timer.domain.usecase.ResetPomodoroUseCase
import com.jujodevs.pomodoro.features.timer.domain.usecase.SkipPomodoroPhaseUseCase
import com.jujodevs.pomodoro.features.timer.domain.usecase.StartOrResumePomodoroUseCase
import com.jujodevs.pomodoro.features.timer.domain.usecase.StopPomodoroUseCase
import com.jujodevs.pomodoro.features.timer.domain.usecase.UpdatePomodoroConfigUseCase
import com.jujodevs.pomodoro.features.timer.presentation.TimerUseCases
import com.jujodevs.pomodoro.features.timer.presentation.TimerViewModel
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.bind
import org.koin.dsl.module

val timerModule = module {
    // Data
    singleOf(::PomodoroRepositoryImpl) bind PomodoroRepository::class
    singleOf(::SystemTimeProvider) bind TimeProvider::class

    // Use Cases
    factoryOf(::ObservePomodoroSessionStateUseCase)
    factoryOf(::AdvancePomodoroPhaseUseCase)
    factoryOf(::StartOrResumePomodoroUseCase)
    factoryOf(::PausePomodoroUseCase)
    factoryOf(::SkipPomodoroPhaseUseCase)
    factoryOf(::StopPomodoroUseCase)
    factoryOf(::ResetPomodoroUseCase)
    factoryOf(::UpdatePomodoroConfigUseCase)

    // ViewModel
    factory {
        TimerUseCases(
            observeSessionState = get(),
            startOrResume = get(),
            pause = get(),
            skip = get(),
            stop = get(),
            reset = get(),
            advancePhase = get(),
            updateConfig = get()
        )
    }

    viewModel {
        TimerViewModel(
            useCases = get(),
            notificationScheduler = get(),
            timeProvider = get()
        )
    }
}
