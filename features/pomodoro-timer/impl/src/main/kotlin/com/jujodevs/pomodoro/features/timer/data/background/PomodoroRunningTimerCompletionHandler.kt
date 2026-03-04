package com.jujodevs.pomodoro.features.timer.data.background

import com.jujodevs.pomodoro.core.domain.util.Result
import com.jujodevs.pomodoro.core.resources.R
import com.jujodevs.pomodoro.features.timer.domain.model.PomodoroPhase
import com.jujodevs.pomodoro.features.timer.domain.model.PomodoroSessionState
import com.jujodevs.pomodoro.features.timer.domain.model.PomodoroStatus
import com.jujodevs.pomodoro.features.timer.domain.provider.TimeProvider
import com.jujodevs.pomodoro.features.timer.domain.repository.PomodoroRepository
import com.jujodevs.pomodoro.features.timer.domain.usecase.ReconcileExpiredPomodoroPhasesUseCase
import com.jujodevs.pomodoro.libs.notifications.NotificationChannel
import com.jujodevs.pomodoro.libs.notifications.NotificationData
import com.jujodevs.pomodoro.libs.notifications.NotificationScheduler
import com.jujodevs.pomodoro.libs.notifications.NotificationType
import com.jujodevs.pomodoro.libs.notifications.RunningTimerCompletionHandler
import com.jujodevs.pomodoro.libs.notifications.RunningTimerNotificationData
import com.jujodevs.pomodoro.libs.usagestats.domain.model.UsageStatsEvent
import com.jujodevs.pomodoro.libs.usagestats.domain.model.UsageStatsEventType
import com.jujodevs.pomodoro.libs.usagestats.domain.model.UsageStatsPhase
import com.jujodevs.pomodoro.libs.usagestats.domain.usecase.RecordUsageStatsEventUseCase
import kotlinx.coroutines.flow.first

class PomodoroRunningTimerCompletionHandler(
    private val repository: PomodoroRepository,
    private val reconcileExpiredPhases: ReconcileExpiredPomodoroPhasesUseCase,
    private val notificationScheduler: NotificationScheduler,
    private val recordUsageStatsEvent: RecordUsageStatsEventUseCase,
    private val timeProvider: TimeProvider,
) : RunningTimerCompletionHandler {
    override suspend fun onRunningTimerCompleted(token: String) {
        if (token.isBlank()) {
            return
        }

        val completedPhases = reconcileExpiredPhases.reconcileByToken(token)
        if (completedPhases.isNotEmpty()) {
            completedPhases.forEach { completedPhase ->
                recordCompletedPhase(completedPhase)
            }

            val updatedState = repository.getSessionState().first()
            if (updatedState.isStableRunningState) {
                updatedState.lastKnownEndTimestamp?.let { endTimestamp ->
                    scheduleNextCompletionNotification(updatedState, endTimestamp)
                    startForegroundTimer(updatedState, endTimestamp)
                }
            }
        }
    }

    private suspend fun scheduleNextCompletionNotification(
        sessionState: PomodoroSessionState,
        endTimestamp: Long,
    ) {
        val phaseTexts = sessionState.currentPhase.toPhaseNotificationTexts()
        val notificationData =
            NotificationData(
                id = COMPLETION_NOTIFICATION_ID,
                titleResId = phaseTexts.completionTitleResId,
                messageResId = phaseTexts.completionMessageResId,
                channelId = NotificationChannel.PomodoroSession.id,
                scheduledTimeMillis = endTimestamp,
                type =
                    when (sessionState.currentPhase) {
                        PomodoroPhase.WORK -> NotificationType.WORK_SESSION_COMPLETE
                        PomodoroPhase.SHORT_BREAK -> NotificationType.SHORT_BREAK_COMPLETE
                        PomodoroPhase.LONG_BREAK -> NotificationType.LONG_BREAK_COMPLETE
                    },
                token = sessionState.phaseToken,
            )
        when (notificationScheduler.scheduleNotification(notificationData)) {
            is Result.Success -> Unit
            is Result.Failure -> Unit
        }
    }

    private suspend fun startForegroundTimer(
        sessionState: PomodoroSessionState,
        endTimestamp: Long,
    ) {
        val phaseTexts = sessionState.currentPhase.toPhaseNotificationTexts()
        val result =
            notificationScheduler.startRunningForegroundTimer(
                RunningTimerNotificationData(
                    notificationId = FOREGROUND_NOTIFICATION_ID,
                    titleResId = phaseTexts.runningTitleResId,
                    messageResId = R.string.label_sessions_completed,
                    messageArgFirst = sessionState.completedWorkSessions,
                    messageArgSecond = sessionState.totalSessions,
                    channelId = NotificationChannel.RunningTimer.id,
                    endTimeMillis = endTimestamp,
                    completionNotificationId = COMPLETION_NOTIFICATION_ID,
                    completionTitleResId = phaseTexts.completionTitleResId,
                    completionMessageResId = phaseTexts.completionMessageResId,
                ),
            )
        when (result) {
            is Result.Success -> Unit
            is Result.Failure -> Unit
        }
    }

    private suspend fun recordCompletedPhase(sessionState: PomodoroSessionState) {
        val phase = sessionState.currentPhase.toUsageStatsPhase()
        recordUsageEvent(
            type = UsageStatsEventType.PHASE_TIME_RECORDED,
            phase = phase,
            durationMillis = sessionState.currentPhaseDurationMillis,
        )
        recordUsageEvent(
            type = UsageStatsEventType.PHASE_COMPLETED,
            phase = phase,
        )

        if (
            sessionState.currentPhase == PomodoroPhase.WORK &&
            sessionState.completedWorkSessions + 1 >= sessionState.totalSessions
        ) {
            recordUsageEvent(
                type = UsageStatsEventType.CYCLE_COMPLETED,
                phase = UsageStatsPhase.WORK,
            )
        }
    }

    private suspend fun recordUsageEvent(
        type: UsageStatsEventType,
        phase: UsageStatsPhase,
        durationMillis: Long? = null,
    ) {
        val event =
            UsageStatsEvent(
                type = type,
                phase = phase,
                occurredAtMillis = timeProvider.getCurrentTimeMillis(),
                durationMillis = durationMillis,
            )
        when (recordUsageStatsEvent(event)) {
            is Result.Success -> Unit
            is Result.Failure -> Unit
        }
    }

    private fun PomodoroPhase.toUsageStatsPhase(): UsageStatsPhase =
        when (this) {
            PomodoroPhase.WORK -> UsageStatsPhase.WORK
            PomodoroPhase.SHORT_BREAK -> UsageStatsPhase.SHORT_BREAK
            PomodoroPhase.LONG_BREAK -> UsageStatsPhase.LONG_BREAK
        }

    private fun PomodoroPhase.toPhaseNotificationTexts(): PhaseNotificationTexts =
        when (this) {
            PomodoroPhase.WORK ->
                PhaseNotificationTexts(
                    runningTitleResId = R.string.status_focusing,
                    completionTitleResId = R.string.notification_work_complete_title,
                    completionMessageResId = R.string.notification_work_complete_message,
                )

            PomodoroPhase.SHORT_BREAK ->
                PhaseNotificationTexts(
                    runningTitleResId = R.string.session_type_short_break,
                    completionTitleResId = R.string.notification_short_break_complete_title,
                    completionMessageResId = R.string.notification_short_break_complete_message,
                )

            PomodoroPhase.LONG_BREAK ->
                PhaseNotificationTexts(
                    runningTitleResId = R.string.session_type_long_break,
                    completionTitleResId = R.string.notification_long_break_complete_title,
                    completionMessageResId = R.string.notification_long_break_complete_message,
                )
        }

    private val PomodoroSessionState.isStableRunningState: Boolean
        get() =
            status == PomodoroStatus.RUNNING &&
                phaseToken.isNotBlank() &&
                lastKnownEndTimestamp != null

    private data class PhaseNotificationTexts(
        val runningTitleResId: Int,
        val completionTitleResId: Int,
        val completionMessageResId: Int,
    )

    companion object {
        private const val COMPLETION_NOTIFICATION_ID = 1
        private const val FOREGROUND_NOTIFICATION_ID = 2
    }
}
