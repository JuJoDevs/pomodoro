package com.jujodevs.pomodoro.libs.analytics

import com.jujodevs.pomodoro.core.domain.util.DataError
import com.jujodevs.pomodoro.core.domain.util.EmptyResult
import kotlinx.coroutines.flow.Flow

interface AnalyticsCollectionManager {
    fun isAnalyticsEnabled(): Boolean

    fun observeAnalyticsEnabled(): Flow<Boolean>

    suspend fun setAnalyticsEnabled(enabled: Boolean): EmptyResult<DataError.Local>
}
