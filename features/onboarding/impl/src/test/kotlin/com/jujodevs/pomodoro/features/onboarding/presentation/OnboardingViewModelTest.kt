package com.jujodevs.pomodoro.features.onboarding.presentation

import app.cash.turbine.testIn
import app.cash.turbine.turbineScope
import com.jujodevs.pomodoro.core.domain.util.DataError
import com.jujodevs.pomodoro.core.domain.util.EmptyResult
import com.jujodevs.pomodoro.core.domain.util.Result
import com.jujodevs.pomodoro.core.testing.extenion.CoroutineTestExtension
import com.jujodevs.pomodoro.features.onboarding.domain.usecase.SetOnboardingCompletedUseCase
import com.jujodevs.pomodoro.features.onboarding.domain.usecase.UpdateAnalyticsConsentUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.amshove.kluent.shouldBeEqualTo
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension

@OptIn(ExperimentalCoroutinesApi::class)
class OnboardingViewModelTest {
    @RegisterExtension
    val coroutineTestExtension = CoroutineTestExtension()

    private lateinit var setOnboardingCompleted: SetOnboardingCompletedUseCase
    private lateinit var updateAnalyticsConsent: UpdateAnalyticsConsentUseCase
    private lateinit var viewModel: OnboardingViewModel

    @BeforeEach
    fun setUp() {
        setOnboardingCompleted = mockk()
        updateAnalyticsConsent = mockk()

        coEvery { setOnboardingCompleted() } returns (Result.Success(Unit) as EmptyResult<DataError.Local>)
        coEvery { updateAnalyticsConsent(any()) } just runs

        viewModel =
            OnboardingViewModel(
                setOnboardingCompleted = setOnboardingCompleted,
                updateAnalyticsConsent = updateAnalyticsConsent,
            )
    }

    @Test
    fun `GIVEN first launch WHEN loading onboarding status THEN onboarding should be shown`() =
        runTest {
            turbineScope {
                val state = viewModel.state.testIn(this)
                val currentState = state.awaitItem()

                currentState.isLoading shouldBeEqualTo false
                currentState.currentSlideIndex shouldBeEqualTo 0
                currentState.analyticsConsentChecked shouldBeEqualTo true
                state.cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `GIVEN next slide action WHEN on first slide THEN should advance to second slide`() =
        runTest {
            turbineScope {
                val state = viewModel.state.testIn(this)
                state.awaitItem()

                viewModel.onAction(OnboardingAction.NextSlide)

                val updatedState = state.awaitItem()
                updatedState.currentSlideIndex shouldBeEqualTo 1
                state.cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `GIVEN consent checked WHEN toggle consent false THEN state should be updated without persistence`() =
        runTest {
            turbineScope {
                val state = viewModel.state.testIn(this)
                state.awaitItem()

                viewModel.onAction(OnboardingAction.ToggleAnalyticsConsent(enabled = false))

                val updatedState = state.awaitItem()
                updatedState.analyticsConsentChecked shouldBeEqualTo false
                coVerify(exactly = 0) { updateAnalyticsConsent(any()) }
                state.cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `GIVEN user swipes to previous slide WHEN current page changes THEN state should be updated`() =
        runTest {
            turbineScope {
                val state = viewModel.state.testIn(this)
                state.awaitItem()

                viewModel.onAction(OnboardingAction.UpdateCurrentSlide(index = 2))
                state.awaitItem().currentSlideIndex shouldBeEqualTo 2

                viewModel.onAction(OnboardingAction.UpdateCurrentSlide(index = 1))
                state.awaitItem().currentSlideIndex shouldBeEqualTo 1
                state.cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `GIVEN complete onboarding with default consent WHEN executed THEN should persist consent`() =
        runTest {
            turbineScope {
                val effects = viewModel.effects.testIn(this)
                viewModel.onAction(OnboardingAction.CompleteOnboarding)

                effects.awaitItem() shouldBeEqualTo OnboardingEffect.RequestPermissionsAndNavigateToHome
                coVerify(exactly = 1) { setOnboardingCompleted() }
                coVerify(exactly = 1) { updateAnalyticsConsent(true) }
                effects.cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `GIVEN complete onboarding with consent disabled WHEN executed THEN should persist disabled consent`() =
        runTest {
            turbineScope {
                val effects = viewModel.effects.testIn(this)
                viewModel.onAction(OnboardingAction.ToggleAnalyticsConsent(enabled = false))
                viewModel.onAction(OnboardingAction.CompleteOnboarding)

                effects.awaitItem() shouldBeEqualTo OnboardingEffect.RequestPermissionsAndNavigateToHome
                coVerify(exactly = 1) { updateAnalyticsConsent(false) }
                effects.cancelAndIgnoreRemainingEvents()
            }
        }
}
