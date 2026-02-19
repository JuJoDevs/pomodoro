package com.jujodevs.pomodoro.features.settings.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jujodevs.pomodoro.features.settings.domain.usecase.GetCanScheduleExactAlarmsUseCase
import com.jujodevs.pomodoro.features.settings.domain.usecase.GetCompletionAlarmSoundLabelUseCase
import com.jujodevs.pomodoro.features.settings.domain.usecase.GetHasNotificationPermissionUseCase
import com.jujodevs.pomodoro.features.settings.domain.usecase.ObserveAnalyticsConsentUseCase
import com.jujodevs.pomodoro.features.settings.domain.usecase.UpdateAnalyticsConsentUseCase
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val getCanScheduleExactAlarms: GetCanScheduleExactAlarmsUseCase,
    private val getHasNotificationPermission: GetHasNotificationPermissionUseCase,
    private val getCompletionAlarmSoundLabel: GetCompletionAlarmSoundLabelUseCase,
    private val observeAnalyticsConsent: ObserveAnalyticsConsentUseCase,
    private val updateAnalyticsConsent: UpdateAnalyticsConsentUseCase,
) : ViewModel() {
    private val _state = MutableStateFlow(SettingsState())
    val state = _state.asStateFlow()

    private val _effects = MutableSharedFlow<SettingsEffect>()
    val effects = _effects.asSharedFlow()

    init {
        viewModelScope.launch {
            observeAnalyticsConsent().collectLatest { isEnabled ->
                _state.update { current ->
                    current.copy(analyticsCollectionEnabled = isEnabled)
                }
            }
        }

        viewModelScope.launch {
            syncPermissionAndAlarmState()
        }
    }

    fun onAction(action: SettingsAction) {
        viewModelScope.launch {
            when (action) {
                is SettingsAction.ToggleAnalyticsCollection -> {
                    _state.update { current ->
                        current.copy(analyticsCollectionEnabled = action.enabled)
                    }
                    updateAnalyticsConsent(action.enabled)
                }
                SettingsAction.OpenNotificationChannelSettings -> {
                    _effects.emit(SettingsEffect.OpenNotificationChannelSettings)
                }
                SettingsAction.GrantExactAlarmPermission -> {
                    _effects.emit(SettingsEffect.GrantExactAlarmPermission)
                }
                SettingsAction.RequestNotificationPermission -> {
                    _effects.emit(SettingsEffect.RequestNotificationPermission)
                }
            }
        }
    }

    fun refreshPermissionAndAlarmState() {
        viewModelScope.launch {
            syncPermissionAndAlarmState()
        }
    }

    private suspend fun syncPermissionAndAlarmState() {
        val canScheduleExactAlarms = getCanScheduleExactAlarms()
        val hasNotificationPermission = getHasNotificationPermission()
        val alarmSoundLabel = getCompletionAlarmSoundLabel()
        _state.update {
            it.copy(
                canScheduleExactAlarms = canScheduleExactAlarms,
                hasNotificationPermission = hasNotificationPermission,
                alarmSoundLabel = alarmSoundLabel,
                isLoading = false,
            )
        }
    }
}
