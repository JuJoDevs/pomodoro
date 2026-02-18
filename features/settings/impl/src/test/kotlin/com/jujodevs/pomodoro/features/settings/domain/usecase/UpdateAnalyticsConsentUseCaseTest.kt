package com.jujodevs.pomodoro.features.settings.domain.usecase

import com.jujodevs.pomodoro.features.settings.domain.repository.AnalyticsConsentRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.amshove.kluent.shouldBeEqualTo
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class UpdateAnalyticsConsentUseCaseTest {

    private lateinit var repository: FakeUpdateAnalyticsConsentRepository
    private lateinit var observeUseCase: ObserveAnalyticsConsentUseCase
    private lateinit var updateUseCase: UpdateAnalyticsConsentUseCase

    @BeforeEach
    fun setUp() {
        repository = FakeUpdateAnalyticsConsentRepository()
        observeUseCase = ObserveAnalyticsConsentUseCase(repository)
        updateUseCase = UpdateAnalyticsConsentUseCase(repository)
    }

    @Test
    fun `GIVEN analytics disabled WHEN updating consent true THEN emit true`() = runTest {
        updateUseCase(enabled = true)

        val consent = observeUseCase().first()
        consent shouldBeEqualTo true
    }
}

private class FakeUpdateAnalyticsConsentRepository : AnalyticsConsentRepository {
    private val consent = MutableStateFlow(false)

    override fun observeAnalyticsConsent(): Flow<Boolean> = consent

    override suspend fun setAnalyticsConsent(enabled: Boolean) {
        consent.value = enabled
    }
}
