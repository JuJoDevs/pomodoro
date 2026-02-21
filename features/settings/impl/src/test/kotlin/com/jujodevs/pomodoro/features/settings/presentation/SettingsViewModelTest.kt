package com.jujodevs.pomodoro.features.settings.presentation

import app.cash.turbine.testIn
import app.cash.turbine.turbineScope
import com.jujodevs.pomodoro.core.domain.util.DataError
import com.jujodevs.pomodoro.core.domain.util.EmptyResult
import com.jujodevs.pomodoro.core.domain.util.Result
import com.jujodevs.pomodoro.core.testing.extenion.CoroutineTestExtension
import com.jujodevs.pomodoro.features.settings.domain.usecase.GetCanScheduleExactAlarmsUseCase
import com.jujodevs.pomodoro.features.settings.domain.usecase.GetCompletionAlarmSoundLabelUseCase
import com.jujodevs.pomodoro.features.settings.domain.usecase.GetHasNotificationPermissionUseCase
import com.jujodevs.pomodoro.features.settings.domain.usecase.ObserveAnalyticsConsentUseCase
import com.jujodevs.pomodoro.features.settings.domain.usecase.UpdateAnalyticsConsentUseCase
import com.jujodevs.pomodoro.libs.analytics.AnalyticsCollectionManager
import com.jujodevs.pomodoro.libs.notifications.AlarmSoundLabelProvider
import com.jujodevs.pomodoro.libs.permissions.PermissionManager
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.test.runTest
import org.amshove.kluent.shouldBeEqualTo
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension

@OptIn(ExperimentalCoroutinesApi::class)
class SettingsViewModelTest {
    @RegisterExtension
    val coroutineTestExtension = CoroutineTestExtension()

    private lateinit var permissionManager: FakePermissionManager
    private lateinit var alarmSoundLabelProvider: FakeAlarmSoundLabelProvider
    private lateinit var analyticsCollectionManager: FakeAnalyticsCollectionManager
    private lateinit var viewModel: SettingsViewModel

    @BeforeEach
    fun setUp() {
        permissionManager = FakePermissionManager()
        alarmSoundLabelProvider = FakeAlarmSoundLabelProvider()
        analyticsCollectionManager = FakeAnalyticsCollectionManager(initialEnabled = false)

        viewModel =
            SettingsViewModel(
                getCanScheduleExactAlarms = GetCanScheduleExactAlarmsUseCase(permissionManager),
                getHasNotificationPermission = GetHasNotificationPermissionUseCase(permissionManager),
                getCompletionAlarmSoundLabel = GetCompletionAlarmSoundLabelUseCase(alarmSoundLabelProvider),
                observeAnalyticsConsent = ObserveAnalyticsConsentUseCase(analyticsCollectionManager),
                updateAnalyticsConsent = UpdateAnalyticsConsentUseCase(analyticsCollectionManager),
            )
    }

    @Test
    fun `GIVEN initialized viewModel WHEN collecting state THEN emit permissions and alarm label`() =
        runTest {
            turbineScope {
                val state = viewModel.state.testIn(this)
                var currentState = state.awaitItem()
                while (currentState.isLoading) {
                    currentState = state.awaitItem()
                }

                currentState.isLoading shouldBeEqualTo false
                currentState.alarmSoundLabel shouldBeEqualTo "Default"
                currentState.analyticsCollectionEnabled shouldBeEqualTo false
                currentState.canScheduleExactAlarms shouldBeEqualTo true
                currentState.hasNotificationPermission shouldBeEqualTo true
                state.cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `GIVEN analytics toggle action WHEN onAction THEN analytics state is updated`() =
        runTest {
            turbineScope {
                val state = viewModel.state.testIn(this)

                var currentState = state.awaitItem()
                while (currentState.isLoading) {
                    currentState = state.awaitItem()
                }

                viewModel.onAction(SettingsAction.ToggleAnalyticsCollection(enabled = true))

                var updatedState = state.awaitItem()
                while (!updatedState.analyticsCollectionEnabled) {
                    updatedState = state.awaitItem()
                }

                updatedState.analyticsCollectionEnabled shouldBeEqualTo true
                analyticsCollectionManager.setAnalyticsEnabledCalls shouldBeEqualTo listOf(true)
                state.cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `GIVEN grant exact alarm action WHEN onAction THEN emit permission effect`() =
        runTest {
            turbineScope {
                val effects = viewModel.effects.testIn(this)
                viewModel.onAction(SettingsAction.GrantExactAlarmPermission)
                effects.awaitItem() shouldBeEqualTo SettingsEffect.GrantExactAlarmPermission
                effects.cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `GIVEN open channel settings action WHEN onAction THEN emit open channel effect`() =
        runTest {
            turbineScope {
                val effects = viewModel.effects.testIn(this)
                viewModel.onAction(SettingsAction.OpenNotificationChannelSettings)
                effects.awaitItem() shouldBeEqualTo SettingsEffect.OpenNotificationChannelSettings
                effects.cancelAndIgnoreRemainingEvents()
            }
        }
}

private class FakePermissionManager : PermissionManager {
    var canScheduleExactAlarms: Boolean = true
    var hasNotificationPermission: Boolean = true

    override fun hasNotificationPermission(): Boolean = hasNotificationPermission

    override fun canScheduleExactAlarms(): Boolean = canScheduleExactAlarms

    override fun getNotificationPermissionString(): String? = null
}

private class FakeAlarmSoundLabelProvider : AlarmSoundLabelProvider {
    var soundLabel: String = "Default"

    override fun getCompletionChannelSoundLabel(): String = soundLabel
}

private class FakeAnalyticsCollectionManager(
    initialEnabled: Boolean,
) : AnalyticsCollectionManager {
    private val analyticsEnabledFlow = MutableStateFlow(initialEnabled)
    val setAnalyticsEnabledCalls = mutableListOf<Boolean>()

    override fun isAnalyticsEnabled(): Boolean = analyticsEnabledFlow.value

    override fun observeAnalyticsEnabled(): Flow<Boolean> = analyticsEnabledFlow.asStateFlow()

    override suspend fun setAnalyticsEnabled(enabled: Boolean): EmptyResult<DataError.Local> {
        setAnalyticsEnabledCalls += enabled
        analyticsEnabledFlow.value = enabled
        return Result.Success(Unit)
    }
}
