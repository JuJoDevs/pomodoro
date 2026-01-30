package com.jujodevs.pomodoro.libs.logger

/**
 * No-op implementation of [Logger] for testing.
 *
 * Use this in tests to avoid actual logging and to verify log calls.
 */
class FakeLogger : Logger {
    override fun d(
        tag: String,
        message: String,
        throwable: Throwable?,
    ) {
        // No-op for tests
    }

    override fun i(
        tag: String,
        message: String,
        throwable: Throwable?,
    ) {
        // No-op for tests
    }

    override fun w(
        tag: String,
        message: String,
        throwable: Throwable?,
    ) {
        // No-op for tests
    }

    override fun e(
        tag: String,
        message: String,
        throwable: Throwable?,
    ) {
        // No-op for tests
    }
}
