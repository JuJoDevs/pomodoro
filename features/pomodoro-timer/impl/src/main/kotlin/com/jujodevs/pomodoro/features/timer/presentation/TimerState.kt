package com.jujodevs.pomodoro.features.timer.presentation

import com.jujodevs.pomodoro.features.timer.domain.model.PomodoroBusinessRules
import com.jujodevs.pomodoro.features.timer.domain.model.PomodoroPhase
import com.jujodevs.pomodoro.features.timer.domain.model.PomodoroStatus

private const val DEFAULT_REMAINING_TIME_TEXT = "25:00"

data class TimerState(
    val phase: PomodoroPhase = PomodoroPhase.WORK,
    val status: PomodoroStatus = PomodoroStatus.IDLE,
    val remainingTimeText: String = DEFAULT_REMAINING_TIME_TEXT,
    val progress: Float = 0f,
    val completedSessions: Int = 0,
    val selectedWorkMinutes: Int = PomodoroBusinessRules.DEFAULT_WORK_MINUTES,
    val selectedShortBreakMinutes: Int = PomodoroBusinessRules.DEFAULT_SHORT_BREAK_MINUTES,
    val totalSessions: Int = PomodoroBusinessRules.SESSIONS_BEFORE_LONG_BREAK,
    val workDurationOptions: List<Int> = PomodoroBusinessRules.WORK_DURATION_OPTIONS_MINUTES,
    val breakDurationOptions: List<Int> = PomodoroBusinessRules.SHORT_BREAK_DURATION_OPTIONS_MINUTES,
    val autoStartBreaks: Boolean = false,
    val autoStartWork: Boolean = false,
    val isLoading: Boolean = false,
    val showStopConfirmation: Boolean = false,
    val showResetConfirmation: Boolean = false,
    val isExactAlarmPermissionMissing: Boolean = false
)
