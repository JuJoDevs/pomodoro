package com.jujodevs.pomodoro.features.settings.presentation

import app.cash.turbine.test
import com.jujodevs.pomodoro.features.settings.domain.usecase.GetCanScheduleExactAlarmsUseCase
import com.jujodevs.pomodoro.features.settings.domain.usecase.GetCompletionAlarmSoundLabelUseCase
import com.jujodevs.pomodoro.features.settings.domain.usecase.GetHasNotificationPermissionUseCase
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.amshove.kluent.shouldBeEqualTo
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SettingsViewModelTest {

    private lateinit var getCanScheduleExactAlarms: GetCanScheduleExactAlarmsUseCase
    private lateinit var getHasNotificationPermission: GetHasNotificationPermissionUseCase
    private lateinit var getCompletionAlarmSoundLabel: GetCompletionAlarmSoundLabelUseCase
    private lateinit var viewModel: SettingsViewModel

    @BeforeEach
    fun setUp() {
        getCanScheduleExactAlarms = mockk()
        getHasNotificationPermission = mockk()
        getCompletionAlarmSoundLabel = mockk()
        every { getCanScheduleExactAlarms() } returns true
        every { getHasNotificationPermission() } returns true
        every { getCompletionAlarmSoundLabel() } returns "Default"
        viewModel = SettingsViewModel(
            getCanScheduleExactAlarms = getCanScheduleExactAlarms,
            getHasNotificationPermission = getHasNotificationPermission,
            getCompletionAlarmSoundLabel = getCompletionAlarmSoundLabel
        )
    }

    @Test
    fun `GIVEN initialized viewModel WHEN collecting state THEN emit permissions and alarm label`() = runTest {
        viewModel.state.test {
            val state = awaitItem()
            state.isLoading shouldBeEqualTo false
            state.alarmSoundLabel shouldBeEqualTo "Default"
            state.analyticsCollectionEnabled shouldBeEqualTo false
            state.canScheduleExactAlarms shouldBeEqualTo true
            state.hasNotificationPermission shouldBeEqualTo true
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `GIVEN analytics toggle action WHEN onAction THEN analytics state is updated`() = runTest {
        viewModel.state.test {
            awaitItem()

            viewModel.onAction(SettingsAction.ToggleAnalyticsCollection(enabled = true))
            advanceUntilIdle()

            val updatedState = awaitItem()
            updatedState.analyticsCollectionEnabled shouldBeEqualTo true
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `GIVEN grant exact alarm action WHEN onAction THEN emit permission effect`() = runTest {
        viewModel.effects.test {
            viewModel.onAction(SettingsAction.GrantExactAlarmPermission)
            awaitItem() shouldBeEqualTo SettingsEffect.GrantExactAlarmPermission
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `GIVEN open channel settings action WHEN onAction THEN emit open channel effect`() = runTest {
        viewModel.effects.test {
            viewModel.onAction(SettingsAction.OpenNotificationChannelSettings)
            awaitItem() shouldBeEqualTo SettingsEffect.OpenNotificationChannelSettings
            cancelAndIgnoreRemainingEvents()
        }
    }
}
