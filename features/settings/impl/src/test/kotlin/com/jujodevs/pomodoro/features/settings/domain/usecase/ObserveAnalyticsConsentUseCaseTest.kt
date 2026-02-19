package com.jujodevs.pomodoro.features.settings.domain.usecase

import com.jujodevs.pomodoro.libs.analytics.AnalyticsCollectionManager
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.amshove.kluent.shouldBeEqualTo
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class ObserveAnalyticsConsentUseCaseTest {
    private lateinit var analyticsCollectionManager: AnalyticsCollectionManager
    private lateinit var useCase: ObserveAnalyticsConsentUseCase

    @BeforeEach
    fun setUp() {
        analyticsCollectionManager = mockk()
        useCase = ObserveAnalyticsConsentUseCase(analyticsCollectionManager)
    }

    @Test
    fun `GIVEN analytics disabled WHEN observing THEN emit false`() =
        runTest {
            every { analyticsCollectionManager.observeAnalyticsEnabled() } returns flowOf(false)

            val consent = useCase().first()

            consent shouldBeEqualTo false
        }
}
