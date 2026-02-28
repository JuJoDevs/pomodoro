package com.jujodevs.pomodoro.features.onboarding.domain.usecase

import com.jujodevs.pomodoro.core.domain.util.Result
import com.jujodevs.pomodoro.libs.datastore.DataStoreManager
import com.jujodevs.pomodoro.libs.datastore.InternalStateKeys
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ObserveHasCompletedOnboardingUseCase(
    private val dataStoreManager: DataStoreManager,
) {
    operator fun invoke(): Flow<Boolean> =
        dataStoreManager
            .observeValue(InternalStateKeys.HAS_COMPLETED_ONBOARDING, false)
            .map { result ->
                when (result) {
                    is Result.Success -> result.data
                    is Result.Failure -> false
                }
            }
}
