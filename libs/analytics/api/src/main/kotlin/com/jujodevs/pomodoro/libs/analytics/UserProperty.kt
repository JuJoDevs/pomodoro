package com.jujodevs.pomodoro.libs.analytics

/**
 * Represents a user property for analytics.
 *
 * @param key The property key
 * @param value The property value (must be String, Int, Long, Double, or Boolean)
 */
data class UserProperty(
    val key: String,
    val value: Any,
) {
    init {
        require(value is String || value is Int || value is Long || value is Double || value is Boolean) {
            "UserProperty value must be String, Int, Long, Double, or Boolean. Got: ${value::class.simpleName}"
        }
    }
}
