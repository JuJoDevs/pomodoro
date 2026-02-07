package com.jujodevs.pomodoro.features.timer.data

import com.jujodevs.pomodoro.features.timer.domain.model.PomodoroBusinessRules
import com.jujodevs.pomodoro.features.timer.domain.model.PomodoroPhase
import com.jujodevs.pomodoro.features.timer.domain.model.PomodoroSessionState
import com.jujodevs.pomodoro.features.timer.domain.model.PomodoroStatus
import com.jujodevs.pomodoro.features.timer.domain.repository.PomodoroRepository
import com.jujodevs.pomodoro.libs.datastore.DataStoreManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first

class PomodoroRepositoryImpl(
    private val dataStoreManager: DataStoreManager
) : PomodoroRepository {

    override fun getSessionState(): Flow<PomodoroSessionState> {
        return combine(
            dataStoreManager.observeValue<Int>(KEY_WORK_MINUTES, DEFAULT_WORK_MINUTES),
            dataStoreManager.observeValue<Int>(KEY_SHORT_BREAK_MINUTES, DEFAULT_SHORT_BREAK_MINUTES),
            dataStoreManager.observeValue<Boolean>(KEY_AUTO_START_BREAKS, false),
            dataStoreManager.observeValue<Boolean>(KEY_AUTO_START_WORK, false),
            dataStoreManager.observeValue<String>(KEY_CURRENT_PHASE, PomodoroPhase.WORK.name),
            dataStoreManager.observeValue<String>(KEY_STATUS, PomodoroStatus.IDLE.name),
            dataStoreManager.observeValue<Long>(KEY_REMAINING_MILLIS, DEFAULT_REMAINING_MILLIS),
            dataStoreManager.observeValue<Int>(KEY_COMPLETED_SESSIONS, 0),
            dataStoreManager.observeValue<String>(KEY_PHASE_TOKEN, ""),
            dataStoreManager.observeValue<Int>(KEY_NOTIFICATION_ID, DEFAULT_NOTIFICATION_ID),
            dataStoreManager.observeValue<Long>(KEY_END_TIMESTAMP, DEFAULT_END_TIMESTAMP),
            dataStoreManager.observeValue<Long>(KEY_EXACT_ALARM_WARNING_SNOOZED_UNTIL, DEFAULT_SNOOZED_UNTIL)
        ) { values: Array<Any?> ->
            val workMinutes = values[INDEX_WORK_MINUTES] as Int
            val shortBreakMinutes = values[INDEX_SHORT_BREAK_MINUTES] as Int
            val autoStartBreaks = values[INDEX_AUTO_START_BREAKS] as Boolean
            val autoStartWork = values[INDEX_AUTO_START_WORK] as Boolean
            val phaseName = values[INDEX_CURRENT_PHASE] as String
            val statusName = values[INDEX_STATUS] as String
            val remainingMillis = values[INDEX_REMAINING_MILLIS] as Long
            val completedSessions = values[INDEX_COMPLETED_SESSIONS] as Int
            val phaseToken = values[INDEX_PHASE_TOKEN] as String
            val notificationId = values[INDEX_NOTIFICATION_ID] as Int
            val endTimestamp = values[INDEX_END_TIMESTAMP] as Long
            val exactAlarmWarningSnoozedUntil = values[INDEX_EXACT_ALARM_WARNING_SNOOZED_UNTIL] as Long
            val exactAlarmWarningSnoozedUntilMillis =
                if (exactAlarmWarningSnoozedUntil == DEFAULT_SNOOZED_UNTIL) {
                    null
                } else {
                    exactAlarmWarningSnoozedUntil
                }

            PomodoroSessionState(
                selectedWorkMinutes = workMinutes,
                selectedShortBreakMinutes = shortBreakMinutes,
                autoStartBreaks = autoStartBreaks,
                autoStartWork = autoStartWork,
                currentPhase = PomodoroPhase.valueOf(phaseName),
                status = PomodoroStatus.valueOf(statusName),
                remainingMillis = remainingMillis,
                completedWorkSessions = completedSessions,
                phaseToken = phaseToken,
                scheduledNotificationId = if (notificationId == DEFAULT_NOTIFICATION_ID) null else notificationId,
                lastKnownEndTimestamp = if (endTimestamp == DEFAULT_END_TIMESTAMP) null else endTimestamp,
                exactAlarmWarningSnoozedUntilMillis = exactAlarmWarningSnoozedUntilMillis
            )
        }
    }

    override suspend fun updateSessionState(state: PomodoroSessionState) {
        dataStoreManager.setValue(KEY_WORK_MINUTES, state.selectedWorkMinutes)
        dataStoreManager.setValue(KEY_SHORT_BREAK_MINUTES, state.selectedShortBreakMinutes)
        dataStoreManager.setValue(KEY_AUTO_START_BREAKS, state.autoStartBreaks)
        dataStoreManager.setValue(KEY_AUTO_START_WORK, state.autoStartWork)
        dataStoreManager.setValue(KEY_CURRENT_PHASE, state.currentPhase.name)
        dataStoreManager.setValue(KEY_STATUS, state.status.name)
        dataStoreManager.setValue(KEY_REMAINING_MILLIS, state.remainingMillis)
        dataStoreManager.setValue(KEY_COMPLETED_SESSIONS, state.completedWorkSessions)
        dataStoreManager.setValue(KEY_PHASE_TOKEN, state.phaseToken)
        dataStoreManager.setValue(KEY_NOTIFICATION_ID, state.scheduledNotificationId ?: DEFAULT_NOTIFICATION_ID)
        dataStoreManager.setValue(KEY_END_TIMESTAMP, state.lastKnownEndTimestamp ?: DEFAULT_END_TIMESTAMP)
        dataStoreManager.setValue(
            KEY_EXACT_ALARM_WARNING_SNOOZED_UNTIL,
            state.exactAlarmWarningSnoozedUntilMillis ?: DEFAULT_SNOOZED_UNTIL
        )
    }

    override suspend fun updateSessionState(update: (PomodoroSessionState) -> PomodoroSessionState) {
        val currentState = getSessionState().first()
        val newState = update(currentState)
        updateSessionState(newState)
    }

    companion object {
        private const val KEY_WORK_MINUTES = "pomodoro_work_minutes"
        private const val KEY_SHORT_BREAK_MINUTES = "pomodoro_short_break_minutes"
        private const val KEY_AUTO_START_BREAKS = "pomodoro_auto_start_breaks"
        private const val KEY_AUTO_START_WORK = "pomodoro_auto_start_work"
        private const val KEY_CURRENT_PHASE = "pomodoro_current_phase"
        private const val KEY_STATUS = "pomodoro_status"
        private const val KEY_REMAINING_MILLIS = "pomodoro_remaining_millis"
        private const val KEY_COMPLETED_SESSIONS = "pomodoro_completed_sessions"
        private const val KEY_PHASE_TOKEN = "pomodoro_phase_token"
        private const val KEY_NOTIFICATION_ID = "pomodoro_notification_id"
        private const val KEY_END_TIMESTAMP = "pomodoro_end_timestamp"
        private const val KEY_EXACT_ALARM_WARNING_SNOOZED_UNTIL =
            "pomodoro_exact_alarm_warning_snoozed_until"

        private const val DEFAULT_WORK_MINUTES = PomodoroBusinessRules.DEFAULT_WORK_MINUTES
        private const val DEFAULT_SHORT_BREAK_MINUTES = PomodoroBusinessRules.DEFAULT_SHORT_BREAK_MINUTES
        private const val MILLIS_IN_MINUTE = 60 * 1000L
        private const val DEFAULT_REMAINING_MILLIS = DEFAULT_WORK_MINUTES * MILLIS_IN_MINUTE
        private const val DEFAULT_NOTIFICATION_ID = -1
        private const val DEFAULT_END_TIMESTAMP = -1L
        private const val DEFAULT_SNOOZED_UNTIL = -1L

        private const val INDEX_WORK_MINUTES = 0
        private const val INDEX_SHORT_BREAK_MINUTES = 1
        private const val INDEX_AUTO_START_BREAKS = 2
        private const val INDEX_AUTO_START_WORK = 3
        private const val INDEX_CURRENT_PHASE = 4
        private const val INDEX_STATUS = 5
        private const val INDEX_REMAINING_MILLIS = 6
        private const val INDEX_COMPLETED_SESSIONS = 7
        private const val INDEX_PHASE_TOKEN = 8
        private const val INDEX_NOTIFICATION_ID = 9
        private const val INDEX_END_TIMESTAMP = 10
        private const val INDEX_EXACT_ALARM_WARNING_SNOOZED_UNTIL = 11
    }
}
