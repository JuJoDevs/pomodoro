package com.jujodevs.pomodoro.libs.usagestats.domain.usecase

import com.jujodevs.pomodoro.libs.usagestats.domain.repository.UsageStatsRepository
import kotlinx.coroutines.flow.Flow

class ObserveUsageStatsEventsCountUseCase(
    private val repository: UsageStatsRepository,
) {
    operator fun invoke(): Flow<Long> = repository.observeEventsCount()
}
