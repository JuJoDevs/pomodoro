package com.jujodevs.pomodoro.features.timer.domain.model

object PomodoroBusinessRules {
    private const val MILLIS_IN_DAY = 24 * 60 * 60 * 1000L

    const val SESSIONS_BEFORE_LONG_BREAK = 4

    const val DEFAULT_WORK_MINUTES = 25
    const val DEFAULT_SHORT_BREAK_MINUTES = 5

    const val WORK_OPTION_15_MINUTES = 15
    const val WORK_OPTION_20_MINUTES = 20
    const val WORK_OPTION_25_MINUTES = 25
    const val WORK_OPTION_30_MINUTES = 30

    const val BREAK_OPTION_3_MINUTES = 3
    const val BREAK_OPTION_5_MINUTES = 5
    const val BREAK_OPTION_10_MINUTES = 10
    const val BREAK_OPTION_15_MINUTES = 15

    val WORK_DURATION_OPTIONS_MINUTES =
        listOf(
            WORK_OPTION_15_MINUTES,
            WORK_OPTION_20_MINUTES,
            WORK_OPTION_25_MINUTES,
            WORK_OPTION_30_MINUTES,
        )

    val SHORT_BREAK_DURATION_OPTIONS_MINUTES =
        listOf(
            BREAK_OPTION_3_MINUTES,
            BREAK_OPTION_5_MINUTES,
            BREAK_OPTION_10_MINUTES,
            BREAK_OPTION_15_MINUTES,
        )

    const val LONG_BREAK_MULTIPLIER = 3
    const val MIN_LONG_BREAK_MINUTES = 10
    const val MAX_LONG_BREAK_MINUTES = 30

    const val EXACT_ALARM_WARNING_SNOOZE_DAYS = 7
    const val EXACT_ALARM_WARNING_SNOOZE_MILLIS = EXACT_ALARM_WARNING_SNOOZE_DAYS * MILLIS_IN_DAY
}
