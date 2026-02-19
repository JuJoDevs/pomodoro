package com.jujodevs.pomodoro.libs.usagestats.domain.usecase

import com.jujodevs.pomodoro.core.domain.util.DataError
import com.jujodevs.pomodoro.core.domain.util.EmptyResult
import com.jujodevs.pomodoro.libs.usagestats.domain.model.UsageStatsEvent
import com.jujodevs.pomodoro.libs.usagestats.domain.repository.UsageStatsRepository

class RecordUsageStatsEventUseCase(
    private val repository: UsageStatsRepository,
) {
    suspend operator fun invoke(event: UsageStatsEvent): EmptyResult<DataError.Local> = repository.recordEvent(event)
}
