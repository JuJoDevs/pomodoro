package com.jujodevs.pomodoro.libs.usagestats.domain.repository

import com.jujodevs.pomodoro.core.domain.util.DataError
import com.jujodevs.pomodoro.core.domain.util.EmptyResult
import com.jujodevs.pomodoro.core.domain.util.Result
import com.jujodevs.pomodoro.libs.usagestats.domain.model.UsageStatsEvent
import com.jujodevs.pomodoro.libs.usagestats.domain.model.UsageStatsSummary

interface UsageStatsRepository {
    suspend fun recordEvent(event: UsageStatsEvent): EmptyResult<DataError.Local>

    suspend fun getSummary(
        periodStartMillis: Long,
        periodEndMillis: Long,
    ): Result<UsageStatsSummary, DataError.Local>
}
