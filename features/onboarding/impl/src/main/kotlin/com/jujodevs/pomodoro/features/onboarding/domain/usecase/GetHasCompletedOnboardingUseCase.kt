package com.jujodevs.pomodoro.features.onboarding.domain.usecase

import com.jujodevs.pomodoro.core.domain.util.Result
import com.jujodevs.pomodoro.libs.datastore.DataStoreManager
import com.jujodevs.pomodoro.libs.datastore.InternalStateKeys

class GetHasCompletedOnboardingUseCase(
    private val dataStoreManager: DataStoreManager,
) {
    suspend operator fun invoke(): Boolean {
        val result =
            dataStoreManager.getValue(
                InternalStateKeys.HAS_COMPLETED_ONBOARDING,
                false,
            )
        return when (result) {
            is Result.Success -> result.data
            is Result.Failure -> false
        }
    }
}
