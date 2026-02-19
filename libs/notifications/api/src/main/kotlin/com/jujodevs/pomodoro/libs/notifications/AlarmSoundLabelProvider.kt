package com.jujodevs.pomodoro.libs.notifications

/**
 * Provides a human-readable label for the completion notification channel sound.
 *
 * Best-effort resolution: may return "Silent", "Custom sound", "Default", or "Unknown"
 * depending on platform capabilities.
 */
interface AlarmSoundLabelProvider {
    /**
     * Returns the current sound label for the Pomodoro completion channel.
     */
    fun getCompletionChannelSoundLabel(): String
}
