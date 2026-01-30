package com.jujodevs.pomodoro.libs.crashlytics

/**
 * No-op implementation of [CrashReporter] for testing.
 *
 * Use this in tests to avoid actual crash reporting and to verify reporting calls.
 */
class FakeCrashReporter : CrashReporter {
    private val recordedExceptions = mutableListOf<Throwable>()
    private val customKeys = mutableMapOf<String, Any>()
    private val logMessages = mutableListOf<String>()
    private var userId: String? = null

    override fun recordException(throwable: Throwable) {
        recordedExceptions.add(throwable)
    }

    override fun setUserId(userId: String) {
        this.userId = userId
    }

    override fun setCustomKey(
        key: String,
        value: String,
    ) {
        customKeys[key] = value
    }

    override fun setCustomKey(
        key: String,
        value: Int,
    ) {
        customKeys[key] = value
    }

    override fun setCustomKey(
        key: String,
        value: Boolean,
    ) {
        customKeys[key] = value
    }

    override fun log(message: String) {
        logMessages.add(message)
    }

    /**
     * Get all recorded exceptions (for test verification).
     */
    fun getRecordedExceptions(): List<Throwable> = recordedExceptions.toList()

    /**
     * Get custom keys (for test verification).
     */
    fun getCustomKeys(): Map<String, Any> = customKeys.toMap()

    /**
     * Get log messages (for test verification).
     */
    fun getLogMessages(): List<String> = logMessages.toList()

    /**
     * Get current user ID (for test verification).
     */
    fun getUserId(): String? = userId

    /**
     * Clear all recorded data (useful for test cleanup).
     */
    fun clear() {
        recordedExceptions.clear()
        customKeys.clear()
        logMessages.clear()
        userId = null
    }
}
