package com.jujodevs.pomodoro.features.onboarding.domain.usecase

import com.jujodevs.pomodoro.core.domain.util.EmptyResult
import com.jujodevs.pomodoro.libs.datastore.DataStoreManager
import com.jujodevs.pomodoro.libs.datastore.InternalStateKeys

class SetOnboardingCompletedUseCase(
    private val dataStoreManager: DataStoreManager,
) {
    suspend operator fun invoke(): EmptyResult<com.jujodevs.pomodoro.core.domain.util.DataError.Local> =
        dataStoreManager.setValue(InternalStateKeys.HAS_COMPLETED_ONBOARDING, true)
}
