package com.jujodevs.pomodoro.features.settings.domain.usecase

import com.jujodevs.pomodoro.core.domain.util.Result
import com.jujodevs.pomodoro.core.testing.coVerifyOnce
import com.jujodevs.pomodoro.core.testing.relaxedMockk
import com.jujodevs.pomodoro.libs.analytics.AnalyticsCollectionManager
import io.mockk.coEvery
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class UpdateAnalyticsConsentUseCaseTest {
    private lateinit var analyticsCollectionManager: AnalyticsCollectionManager
    private lateinit var updateUseCase: UpdateAnalyticsConsentUseCase

    @BeforeEach
    fun setUp() {
        analyticsCollectionManager = relaxedMockk()
        coEvery { analyticsCollectionManager.setAnalyticsEnabled(any()) } returns Result.Success(Unit)
        updateUseCase = UpdateAnalyticsConsentUseCase(analyticsCollectionManager)
    }

    @Test
    fun `GIVEN analytics disabled WHEN updating consent true THEN persist enabled flag`() =
        runTest {
            updateUseCase(enabled = true)

            coVerifyOnce {
                analyticsCollectionManager.setAnalyticsEnabled(true)
            }
        }
}
