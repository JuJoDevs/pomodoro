package com.jujodevs.pomodoro.features.statistics.presentation

sealed interface StatisticsEffect {
    data object ShareProgress : StatisticsEffect
}
