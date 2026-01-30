package com.jujodevs.pomodoro.libs.analytics

/**
 * Sealed interface representing an analytics event.
 *
 * Each event has a name and optional parameters.
 * Implementations should create specific event classes for type safety.
 */
sealed interface AnalyticsEvent {
    /**
     * The event name (e.g., "pomodoro_started", "session_completed").
     */
    val name: String

    /**
     * Event parameters as key-value pairs.
     * Values must be of supported types: String, Int, Long, Double, Boolean.
     */
    val parameters: Map<String, Any>
}

/**
 * Custom analytics event that allows creating events with any name and parameters.
 *
 * Use this for quick event tracking or when specific event classes haven't been created yet.
 * For better type safety, create specific event classes that implement [AnalyticsEvent].
 */
data class CustomAnalyticsEvent(
    override val name: String,
    override val parameters: Map<String, Any> = emptyMap(),
) : AnalyticsEvent
