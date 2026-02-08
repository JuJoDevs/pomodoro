package com.jujodevs.pomodoro.features.timer.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jujodevs.pomodoro.core.domain.util.DataError
import com.jujodevs.pomodoro.core.domain.util.Result
import com.jujodevs.pomodoro.core.resources.R
import com.jujodevs.pomodoro.core.ui.UiText
import com.jujodevs.pomodoro.core.ui.asUiText
import com.jujodevs.pomodoro.features.timer.domain.model.PomodoroBusinessRules
import com.jujodevs.pomodoro.features.timer.domain.model.PomodoroPhase
import com.jujodevs.pomodoro.features.timer.domain.model.PomodoroSessionState
import com.jujodevs.pomodoro.features.timer.domain.model.PomodoroStatus
import com.jujodevs.pomodoro.features.timer.domain.provider.TimeProvider
import com.jujodevs.pomodoro.features.timer.domain.usecase.AdvancePomodoroPhaseUseCase
import com.jujodevs.pomodoro.features.timer.domain.usecase.ObservePomodoroSessionStateUseCase
import com.jujodevs.pomodoro.features.timer.domain.usecase.PausePomodoroUseCase
import com.jujodevs.pomodoro.features.timer.domain.usecase.ResetPomodoroUseCase
import com.jujodevs.pomodoro.features.timer.domain.usecase.SkipPomodoroPhaseUseCase
import com.jujodevs.pomodoro.features.timer.domain.usecase.StartOrResumePomodoroUseCase
import com.jujodevs.pomodoro.features.timer.domain.usecase.StopPomodoroUseCase
import com.jujodevs.pomodoro.features.timer.domain.usecase.UpdatePomodoroConfigUseCase
import com.jujodevs.pomodoro.libs.notifications.NotificationChannel
import com.jujodevs.pomodoro.libs.notifications.NotificationData
import com.jujodevs.pomodoro.libs.notifications.NotificationScheduler
import com.jujodevs.pomodoro.libs.notifications.NotificationType
import com.jujodevs.pomodoro.libs.notifications.RunningTimerNotificationData
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

data class TimerUseCases(
    val observeSessionState: ObservePomodoroSessionStateUseCase,
    val startOrResume: StartOrResumePomodoroUseCase,
    val pause: PausePomodoroUseCase,
    val skip: SkipPomodoroPhaseUseCase,
    val stop: StopPomodoroUseCase,
    val reset: ResetPomodoroUseCase,
    val advancePhase: AdvancePomodoroPhaseUseCase,
    val updateConfig: UpdatePomodoroConfigUseCase
)

@OptIn(FlowPreview::class)
class TimerViewModel(
    private val useCases: TimerUseCases,
    private val notificationScheduler: NotificationScheduler,
    private val timeProvider: TimeProvider,
    stateSyncDebounceMs: Long = DEFAULT_STATE_SYNC_DEBOUNCE_MS
) : ViewModel() {

    private val _state = MutableStateFlow(TimerState(isLoading = true))
    val state = _state.asStateFlow()

    private val _effects = MutableSharedFlow<TimerEffect>()
    val effects = _effects.asSharedFlow()

    private var tickerJob: Job? = null
    private var tickerPhaseToken: String? = null
    private var latestSessionState: PomodoroSessionState? = null
    private var lastScheduledCompletionToken: String? = null
    private var lastScheduledCompletionEnd: Long? = null
    private var lastForegroundTimerToken: String? = null
    private var lastForegroundTimerEnd: Long? = null
    private var exactAlarmPermissionGranted: Boolean? = null
    private var exactAlarmWarningSnoozedUntilMillis: Long? = null

    init {
        useCases.observeSessionState()
            .debounce(stateSyncDebounceMs)
            .onEach { sessionState ->
                latestSessionState = sessionState
                exactAlarmWarningSnoozedUntilMillis = sessionState.exactAlarmWarningSnoozedUntilMillis
                _state.update { it.toUiState(sessionState) }
                handleTicker(sessionState)
                handleNotifications(sessionState)
                syncExactAlarmWarningVisibility(sessionState)
            }
            .launchIn(viewModelScope)
    }

    fun onAction(action: TimerAction) {
        viewModelScope.launch {
            handleConfigurationActions(action)
            handleTimerControlActions(action)
            handleConfirmationActions(action)
            handleUiActions(action)
        }
    }

    private suspend fun handleConfigurationActions(action: TimerAction) {
        when (action) {
            is TimerAction.SelectWorkDuration -> useCases.updateConfig.updateWorkDuration(action.minutes)
            is TimerAction.SelectShortBreakDuration -> useCases.updateConfig.updateShortBreakDuration(action.minutes)
            is TimerAction.ToggleAutoStartBreaks -> useCases.updateConfig.toggleAutoStartBreaks(action.enabled)
            is TimerAction.ToggleAutoStartWork -> useCases.updateConfig.toggleAutoStartWork(action.enabled)
            else -> {}
        }
    }

    private suspend fun handleTimerControlActions(action: TimerAction) {
        when (action) {
            TimerAction.Start, TimerAction.Resume -> useCases.startOrResume()
            TimerAction.Pause -> useCases.pause()
            TimerAction.Skip -> useCases.skip()
            TimerAction.Stop -> handleStopAction()
            TimerAction.Reset -> handleResetAction()
            else -> {}
        }
    }

    private suspend fun handleStopAction() {
        if (_state.value.status != PomodoroStatus.IDLE) {
            _state.update { it.copy(showStopConfirmation = true) }
        } else {
            useCases.stop()
        }
    }

    private suspend fun handleResetAction() {
        if (_state.value.status != PomodoroStatus.IDLE || _state.value.completedSessions > 0) {
            _state.update { it.copy(showResetConfirmation = true) }
        } else {
            useCases.reset()
        }
    }

    private suspend fun handleConfirmationActions(action: TimerAction) {
        when (action) {
            TimerAction.ConfirmStop -> {
                _state.update { it.copy(showStopConfirmation = false) }
                useCases.stop()
            }
            TimerAction.ConfirmReset -> {
                _state.update { it.copy(showResetConfirmation = false) }
                useCases.reset()
            }
            else -> {}
        }
    }

    private suspend fun handleUiActions(action: TimerAction) {
        when (action) {
            TimerAction.DismissDialog -> {
                _state.update { it.copy(showStopConfirmation = false, showResetConfirmation = false) }
            }
            TimerAction.DismissExactAlarmWarning -> {
                dismissExactAlarmWarningForAWeek()
            }
            TimerAction.RequestExactAlarmPermission -> {
                _effects.emit(TimerEffect.RequestExactAlarmPermission)
            }
            is TimerAction.UpdateExactAlarmPermission -> {
                updateExactAlarmPermission(action.isGranted)
            }
            else -> {}
        }
    }

    private suspend fun dismissExactAlarmWarningForAWeek() {
        val snoozedUntilMillis = timeProvider.getCurrentTimeMillis() +
            PomodoroBusinessRules.EXACT_ALARM_WARNING_SNOOZE_MILLIS
        exactAlarmWarningSnoozedUntilMillis = snoozedUntilMillis
        _state.update { it.copy(isExactAlarmPermissionMissing = false) }
        useCases.updateConfig.updateExactAlarmWarningSnoozedUntil(snoozedUntilMillis)
    }

    private suspend fun updateExactAlarmPermission(isGranted: Boolean) {
        exactAlarmPermissionGranted = isGranted
        if (isGranted) {
            _state.update { it.copy(isExactAlarmPermissionMissing = false) }
            if (exactAlarmWarningSnoozedUntilMillis != null) {
                exactAlarmWarningSnoozedUntilMillis = null
                useCases.updateConfig.updateExactAlarmWarningSnoozedUntil(null)
            }
            return
        }

        _state.update { it.copy(isExactAlarmPermissionMissing = shouldShowExactAlarmWarning(latestSessionState)) }
    }

    private fun handleTicker(sessionState: PomodoroSessionState) {
        if (!sessionState.isStableRunningState) {
            stopTicker()
            return
        }

        if (tickerJob == null || tickerPhaseToken != sessionState.phaseToken) {
            startTicker(sessionState.phaseToken)
        }
    }

    private fun startTicker(phaseToken: String) {
        stopTicker()
        tickerPhaseToken = phaseToken

        tickerJob = viewModelScope.launch {
            while (isActive) {
                delay(TICK_DELAY)
                if (!processTickerTick()) {
                    break
                }
            }
        }
    }

    private suspend fun processTickerTick(): Boolean {
        var shouldContinue = true
        val currentState = latestSessionState
        val remaining = currentState
            ?.lastKnownEndTimestamp
            ?.minus(timeProvider.getCurrentTimeMillis())

        when {
            currentState == null -> Unit
            !currentState.isStableRunningState -> shouldContinue = false
            remaining == null -> Unit
            remaining <= 0 -> {
                useCases.advancePhase()
                shouldContinue = false
            }

            else -> updateTickerState(remaining, currentState.currentPhaseDurationMillis)
        }

        return shouldContinue
    }

    private fun stopTicker() {
        tickerJob?.cancel()
        tickerJob = null
        tickerPhaseToken = null
    }

    private fun updateTickerState(remaining: Long, duration: Long) {
        _state.update {
            it.copy(
                remainingTimeText = formatTime(remaining),
                progress = 1f - (remaining.toFloat() / duration)
            )
        }
    }

    private suspend fun handleNotifications(sessionState: PomodoroSessionState) {
        if (sessionState.isStableRunningState) {
            val end = sessionState.lastKnownEndTimestamp ?: return

            scheduleCompletionNotificationIfNeeded(sessionState, end)
            startForegroundTimerIfNeeded(sessionState, end)
        } else {
            cancelNotifications()
            clearScheduledCompletionTracking()
            stopForegroundTimer()
        }
    }

    private suspend fun scheduleCompletionNotificationIfNeeded(
        sessionState: PomodoroSessionState,
        end: Long
    ) {
        if (
            sessionState.phaseToken == lastScheduledCompletionToken &&
            end == lastScheduledCompletionEnd
        ) {
            return
        }

        scheduleCompletionNotification(sessionState, end)
        lastScheduledCompletionToken = sessionState.phaseToken
        lastScheduledCompletionEnd = end
    }

    private suspend fun scheduleCompletionNotification(sessionState: PomodoroSessionState, end: Long) {
        val phaseNotificationTexts = sessionState.currentPhase.toPhaseNotificationTexts()

        val notificationData = NotificationData(
            id = NOTIFICATION_ID_COMPLETION,
            titleResId = phaseNotificationTexts.completionTitle.id,
            messageResId = phaseNotificationTexts.completionMessage.id,
            channelId = NotificationChannel.PomodoroSession.id,
            scheduledTimeMillis = end,
            type = when (sessionState.currentPhase) {
                PomodoroPhase.WORK -> NotificationType.WORK_SESSION_COMPLETE
                PomodoroPhase.SHORT_BREAK -> NotificationType.SHORT_BREAK_COMPLETE
                PomodoroPhase.LONG_BREAK -> NotificationType.LONG_BREAK_COMPLETE
            },
            token = sessionState.phaseToken
        )
        when (val result = notificationScheduler.scheduleNotification(notificationData)) {
            is Result.Success -> {
                if (_state.value.isExactAlarmPermissionMissing) {
                    _state.update { it.copy(isExactAlarmPermissionMissing = false) }
                }
            }

            is Result.Failure -> {
                handleCompletionNotificationFailure(
                    error = result.error,
                    sessionState = sessionState
                )
            }
        }
    }

    private suspend fun cancelNotifications() {
        notificationScheduler.cancelNotification(NOTIFICATION_ID_COMPLETION)
    }

    private suspend fun startForegroundTimerIfNeeded(sessionState: PomodoroSessionState, end: Long) {
        if (sessionState.phaseToken == lastForegroundTimerToken && end == lastForegroundTimerEnd) {
            return
        }

        val phaseNotificationTexts = sessionState.currentPhase.toPhaseNotificationTexts()
        val runningMessage = UiText.StringResource(
            id = R.string.label_sessions_completed,
            args = listOf(sessionState.completedWorkSessions, sessionState.totalSessions)
        )

        val result = notificationScheduler.startRunningForegroundTimer(
            RunningTimerNotificationData(
                notificationId = NOTIFICATION_ID_PERSISTENT,
                titleResId = phaseNotificationTexts.runningTitle.id,
                messageResId = runningMessage.id,
                messageArgFirst = runningMessage.intArgAt(0),
                messageArgSecond = runningMessage.intArgAt(1),
                channelId = NotificationChannel.RunningTimer.id,
                endTimeMillis = end,
                completionNotificationId = NOTIFICATION_ID_COMPLETION,
                completionTitleResId = phaseNotificationTexts.completionTitle.id,
                completionMessageResId = phaseNotificationTexts.completionMessage.id
            )
        )

        when (result) {
            is Result.Success -> {
                lastForegroundTimerToken = sessionState.phaseToken
                lastForegroundTimerEnd = end
            }

            is Result.Failure -> {
                _effects.emit(TimerEffect.ShowMessage(result.error.asUiText()))
            }
        }
    }

    private suspend fun stopForegroundTimer() {
        notificationScheduler.stopRunningForegroundTimer()
        lastForegroundTimerToken = null
        lastForegroundTimerEnd = null
    }

    private fun clearScheduledCompletionTracking() {
        lastScheduledCompletionToken = null
        lastScheduledCompletionEnd = null
    }

    private suspend fun handleCompletionNotificationFailure(
        error: DataError.Local,
        sessionState: PomodoroSessionState
    ) {
        if (error == DataError.Local.INSUFFICIENT_PERMISSIONS) {
            val shouldShowWarning = shouldShowExactAlarmWarning(sessionState)
            _state.update { it.copy(isExactAlarmPermissionMissing = shouldShowWarning) }
            return
        }

        _effects.emit(TimerEffect.ShowMessage(error.asUiText()))
    }

    private fun PomodoroPhase.toPhaseNotificationTexts(): PhaseNotificationTexts {
        return when (this) {
            PomodoroPhase.WORK -> PhaseNotificationTexts(
                runningTitle = UiText.StringResource(R.string.status_focusing),
                completionTitle = UiText.StringResource(R.string.notification_work_complete_title),
                completionMessage = UiText.StringResource(R.string.notification_work_complete_message)
            )

            PomodoroPhase.SHORT_BREAK -> PhaseNotificationTexts(
                runningTitle = UiText.StringResource(R.string.session_type_short_break),
                completionTitle = UiText.StringResource(R.string.notification_short_break_complete_title),
                completionMessage = UiText.StringResource(R.string.notification_short_break_complete_message)
            )

            PomodoroPhase.LONG_BREAK -> PhaseNotificationTexts(
                runningTitle = UiText.StringResource(R.string.session_type_long_break),
                completionTitle = UiText.StringResource(R.string.notification_long_break_complete_title),
                completionMessage = UiText.StringResource(R.string.notification_long_break_complete_message)
            )
        }
    }

    private fun UiText.StringResource.intArgAt(index: Int): Int? = args.getOrNull(index) as? Int

    private fun syncExactAlarmWarningVisibility(sessionState: PomodoroSessionState) {
        when (exactAlarmPermissionGranted) {
            true -> {
                if (_state.value.isExactAlarmPermissionMissing) {
                    _state.update { it.copy(isExactAlarmPermissionMissing = false) }
                }
            }

            false -> {
                val shouldShowWarning = shouldShowExactAlarmWarning(sessionState)
                if (_state.value.isExactAlarmPermissionMissing != shouldShowWarning) {
                    _state.update { it.copy(isExactAlarmPermissionMissing = shouldShowWarning) }
                }
            }

            null -> Unit
        }
    }

    private fun shouldShowExactAlarmWarning(sessionState: PomodoroSessionState?): Boolean {
        if (exactAlarmPermissionGranted == true) {
            return false
        }
        val snoozedUntilMillis = sessionState?.exactAlarmWarningSnoozedUntilMillis
            ?: exactAlarmWarningSnoozedUntilMillis
            ?: 0L
        return snoozedUntilMillis <= timeProvider.getCurrentTimeMillis()
    }

    private fun TimerState.toUiState(sessionState: PomodoroSessionState): TimerState {
        return copy(
            phase = sessionState.currentPhase,
            status = sessionState.status,
            remainingTimeText = formatTime(sessionState.remainingMillis),
            progress = 1f - (sessionState.remainingMillis.toFloat() / sessionState.currentPhaseDurationMillis),
            completedSessions = sessionState.completedWorkSessions,
            selectedWorkMinutes = sessionState.selectedWorkMinutes,
            selectedShortBreakMinutes = sessionState.selectedShortBreakMinutes,
            totalSessions = sessionState.totalSessions,
            workDurationOptions = sessionState.workDurationOptions,
            breakDurationOptions = sessionState.shortBreakDurationOptions,
            autoStartBreaks = sessionState.autoStartBreaks,
            autoStartWork = sessionState.autoStartWork,
            isLoading = false
        )
    }

    private fun formatTime(millis: Long): String {
        val totalSeconds = (millis / MILLIS_IN_SECOND).coerceAtLeast(0)
        val minutes = totalSeconds / SECONDS_IN_MINUTE
        val seconds = totalSeconds % SECONDS_IN_MINUTE
        return "%02d:%02d".format(minutes, seconds)
    }

    private val PomodoroSessionState.isStableRunningState: Boolean
        get() = status == PomodoroStatus.RUNNING &&
            phaseToken.isNotBlank() &&
            lastKnownEndTimestamp != null

    companion object {
        private const val TICK_DELAY = 1000L
        private const val DEFAULT_STATE_SYNC_DEBOUNCE_MS = 50L
        private const val NOTIFICATION_ID_COMPLETION = 1
        private const val NOTIFICATION_ID_PERSISTENT = 2
        private const val MILLIS_IN_SECOND = 1000
        private const val SECONDS_IN_MINUTE = 60
    }
}

private data class PhaseNotificationTexts(
    val runningTitle: UiText.StringResource,
    val completionTitle: UiText.StringResource,
    val completionMessage: UiText.StringResource
)
