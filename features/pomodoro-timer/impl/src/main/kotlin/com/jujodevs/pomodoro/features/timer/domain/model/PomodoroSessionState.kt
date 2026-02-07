package com.jujodevs.pomodoro.features.timer.domain.model

data class PomodoroSessionState(
    val selectedWorkMinutes: Int = PomodoroBusinessRules.DEFAULT_WORK_MINUTES,
    val selectedShortBreakMinutes: Int = PomodoroBusinessRules.DEFAULT_SHORT_BREAK_MINUTES,
    val autoStartBreaks: Boolean = false,
    val autoStartWork: Boolean = false,
    val currentPhase: PomodoroPhase = PomodoroPhase.WORK,
    val status: PomodoroStatus = PomodoroStatus.IDLE,
    val remainingMillis: Long = PomodoroBusinessRules.DEFAULT_WORK_MINUTES * MILLIS_IN_MINUTE,
    val completedWorkSessions: Int = 0,
    val phaseToken: String = "",
    val scheduledNotificationId: Int? = null,
    val lastKnownEndTimestamp: Long? = null,
    val exactAlarmWarningSnoozedUntilMillis: Long? = null
) {
    companion object {
        private const val MILLIS_IN_MINUTE = 60 * 1000L
    }

    val totalSessions: Int
        get() = PomodoroBusinessRules.SESSIONS_BEFORE_LONG_BREAK

    val workDurationOptions: List<Int>
        get() = PomodoroBusinessRules.WORK_DURATION_OPTIONS_MINUTES

    val shortBreakDurationOptions: List<Int>
        get() = PomodoroBusinessRules.SHORT_BREAK_DURATION_OPTIONS_MINUTES

    val longBreakMinutes: Int
        get() = (selectedShortBreakMinutes * PomodoroBusinessRules.LONG_BREAK_MULTIPLIER)
            .coerceIn(
                PomodoroBusinessRules.MIN_LONG_BREAK_MINUTES,
                PomodoroBusinessRules.MAX_LONG_BREAK_MINUTES
            )

    val currentPhaseDurationMillis: Long
        get() = when (currentPhase) {
            PomodoroPhase.WORK -> selectedWorkMinutes * MILLIS_IN_MINUTE
            PomodoroPhase.SHORT_BREAK -> selectedShortBreakMinutes * MILLIS_IN_MINUTE
            PomodoroPhase.LONG_BREAK -> longBreakMinutes * MILLIS_IN_MINUTE
        }
}
