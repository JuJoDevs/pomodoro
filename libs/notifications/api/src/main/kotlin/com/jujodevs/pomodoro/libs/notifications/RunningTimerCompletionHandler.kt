package com.jujodevs.pomodoro.libs.notifications

/**
 * Handles running timer completion events fired by background alarm delivery.
 */
interface RunningTimerCompletionHandler {
    suspend fun onRunningTimerCompleted(token: String)
}
