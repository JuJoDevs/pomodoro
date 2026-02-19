package com.jujodevs.pomodoro.features.settings.domain.usecase

import com.jujodevs.pomodoro.features.settings.domain.repository.AnalyticsConsentRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.amshove.kluent.shouldBeEqualTo
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class ObserveAnalyticsConsentUseCaseTest {

    private lateinit var repository: FakeObserveAnalyticsConsentRepository
    private lateinit var useCase: ObserveAnalyticsConsentUseCase

    @BeforeEach
    fun setUp() {
        repository = FakeObserveAnalyticsConsentRepository()
        useCase = ObserveAnalyticsConsentUseCase(repository)
    }

    @Test
    fun `GIVEN analytics disabled by default WHEN observing THEN emit false`() = runTest {
        val consent = useCase().first()

        consent shouldBeEqualTo false
    }
}

private class FakeObserveAnalyticsConsentRepository : AnalyticsConsentRepository {
    private val consent = MutableStateFlow(false)

    override fun observeAnalyticsConsent(): Flow<Boolean> = consent

    override suspend fun setAnalyticsConsent(enabled: Boolean) {
        consent.value = enabled
    }
}
