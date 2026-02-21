package com.jujodevs.pomodoro.features.statistics.di

import com.jujodevs.pomodoro.features.statistics.domain.usecase.LoadStatisticsSummaryUseCase
import com.jujodevs.pomodoro.features.statistics.presentation.StatisticsViewModel
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val statisticsModule =
    module {
        factoryOf(::LoadStatisticsSummaryUseCase)

        viewModel {
            StatisticsViewModel(
                loadStatisticsSummary = get(),
                observeUsageStatsEventsCount = get(),
            )
        }
    }
