package com.jujodevs.pomodoro.libs.usagestats.di

import androidx.room.Room
import com.jujodevs.pomodoro.libs.usagestats.data.local.UsageStatsDao
import com.jujodevs.pomodoro.libs.usagestats.data.local.UsageStatsDatabase
import com.jujodevs.pomodoro.libs.usagestats.data.mapper.UsageStatsAnalyticsEventMapper
import com.jujodevs.pomodoro.libs.usagestats.data.repository.UsageStatsRepositoryImpl
import com.jujodevs.pomodoro.libs.usagestats.domain.repository.UsageStatsRepository
import com.jujodevs.pomodoro.libs.usagestats.domain.usecase.GetDayStreakUseCase
import com.jujodevs.pomodoro.libs.usagestats.domain.usecase.GetUsageStatsSummaryUseCase
import com.jujodevs.pomodoro.libs.usagestats.domain.usecase.ObserveUsageStatsEventsCountUseCase
import com.jujodevs.pomodoro.libs.usagestats.domain.usecase.RecordUsageStatsEventUseCase
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val usageStatsModule =
    module {
        single {
            Room
                .databaseBuilder(
                    androidContext(),
                    UsageStatsDatabase::class.java,
                    USAGE_STATS_DATABASE_NAME,
                ).build()
        }

        single<UsageStatsDao> { get<UsageStatsDatabase>().usageStatsDao() }

        singleOf(::UsageStatsAnalyticsEventMapper)
        singleOf(::UsageStatsRepositoryImpl).bind<UsageStatsRepository>()

        factoryOf(::RecordUsageStatsEventUseCase)
        factoryOf(::GetUsageStatsSummaryUseCase)
        factoryOf(::GetDayStreakUseCase)
        factoryOf(::ObserveUsageStatsEventsCountUseCase)
    }

private const val USAGE_STATS_DATABASE_NAME = "usage_stats.db"
