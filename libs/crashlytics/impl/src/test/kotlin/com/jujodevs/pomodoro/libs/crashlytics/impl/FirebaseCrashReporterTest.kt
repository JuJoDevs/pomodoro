package com.jujodevs.pomodoro.libs.crashlytics.impl

import com.jujodevs.pomodoro.libs.logger.Logger
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class FirebaseCrashReporterTest {
    private val firebaseCrashlytics: FirebaseCrashlyticsWrapper = mockk()
    private val logger: Logger = mockk(relaxed = true)

    private lateinit var crashReporter: FirebaseCrashReporter

    @BeforeEach
    fun setUp() {
        every { firebaseCrashlytics.recordException(any()) } just runs
        every { firebaseCrashlytics.setUserId(any()) } just runs
        every { firebaseCrashlytics.setCustomKey(any(), any<String>()) } just runs
        every { firebaseCrashlytics.setCustomKey(any(), any<Int>()) } just runs
        every { firebaseCrashlytics.setCustomKey(any(), any<Boolean>()) } just runs
        every { firebaseCrashlytics.log(any()) } just runs

        crashReporter = FirebaseCrashReporter(firebaseCrashlytics, logger)
    }

    @Test
    fun `GIVEN exception WHEN recordException THEN should record exception in Firebase`() {
        // GIVEN
        val exception = RuntimeException("Test exception")

        // WHEN
        crashReporter.recordException(exception)

        // THEN
        verify(exactly = 1) {
            firebaseCrashlytics.recordException(exception)
        }
        verify(exactly = 1) {
            logger.d("Crashlytics", "Exception recorded: Test exception")
        }
    }

    @Test
    fun `GIVEN Firebase throws exception WHEN recordException THEN should log error and not crash`() {
        // GIVEN
        val exception = RuntimeException("Test exception")
        every { firebaseCrashlytics.recordException(any()) } throws RuntimeException("Firebase error")

        // WHEN
        crashReporter.recordException(exception)

        // THEN
        verify(exactly = 1) {
            logger.e("Crashlytics", "Failed to record exception", any())
        }
    }

    @Test
    fun `GIVEN valid userId WHEN setUserId THEN should set userId in Firebase`() {
        // GIVEN
        val userId = "user123"

        // WHEN
        crashReporter.setUserId(userId)

        // THEN
        verify(exactly = 1) {
            firebaseCrashlytics.setUserId("user123")
        }
        verify(exactly = 1) {
            logger.d("Crashlytics", "User ID set: user123")
        }
    }

    @Test
    fun `GIVEN empty userId WHEN setUserId THEN should clear userId in Firebase`() {
        // GIVEN
        val userId = ""

        // WHEN
        crashReporter.setUserId(userId)

        // THEN
        verify(exactly = 1) {
            firebaseCrashlytics.setUserId("")
        }
        verify(exactly = 1) {
            logger.d("Crashlytics", "User ID set: ")
        }
    }

    @Test
    fun `GIVEN Firebase throws exception WHEN setUserId THEN should log error and not crash`() {
        // GIVEN
        every { firebaseCrashlytics.setUserId(any()) } throws RuntimeException("Firebase error")

        // WHEN
        crashReporter.setUserId("user123")

        // THEN
        verify(exactly = 1) {
            logger.e("Crashlytics", "Failed to set user ID", any())
        }
    }

    @Test
    fun `GIVEN string custom key WHEN setCustomKey THEN should set key in Firebase`() {
        // GIVEN
        val key = "test_key"
        val value = "test_value"

        // WHEN
        crashReporter.setCustomKey(key, value)

        // THEN
        verify(exactly = 1) {
            firebaseCrashlytics.setCustomKey("test_key", "test_value")
        }
        verify(exactly = 1) {
            logger.d("Crashlytics", "Custom key set: test_key = test_value")
        }
    }

    @Test
    fun `GIVEN int custom key WHEN setCustomKey THEN should set key in Firebase`() {
        // GIVEN
        val key = "count"
        val value = 42

        // WHEN
        crashReporter.setCustomKey(key, value)

        // THEN
        verify(exactly = 1) {
            firebaseCrashlytics.setCustomKey("count", 42)
        }
        verify(exactly = 1) {
            logger.d("Crashlytics", "Custom key set: count = 42")
        }
    }

    @Test
    fun `GIVEN boolean custom key WHEN setCustomKey THEN should set key in Firebase`() {
        // GIVEN
        val key = "enabled"
        val value = true

        // WHEN
        crashReporter.setCustomKey(key, value)

        // THEN
        verify(exactly = 1) {
            firebaseCrashlytics.setCustomKey("enabled", true)
        }
        verify(exactly = 1) {
            logger.d("Crashlytics", "Custom key set: enabled = true")
        }
    }

    @Test
    fun `GIVEN Firebase throws exception WHEN setCustomKey string THEN should log error and not crash`() {
        // GIVEN
        every { firebaseCrashlytics.setCustomKey(any(), any<String>()) } throws RuntimeException("Firebase error")

        // WHEN
        crashReporter.setCustomKey("key", "value")

        // THEN
        verify(exactly = 1) {
            logger.e("Crashlytics", "Failed to set custom key: key", any())
        }
    }

    @Test
    fun `GIVEN log message WHEN log THEN should log message in Firebase`() {
        // GIVEN
        val message = "Test log message"

        // WHEN
        crashReporter.log(message)

        // THEN
        verify(exactly = 1) {
            firebaseCrashlytics.log("Test log message")
        }
        verify(exactly = 1) {
            logger.d("Crashlytics", "Log message: Test log message")
        }
    }

    @Test
    fun `GIVEN Firebase throws exception WHEN log THEN should log error and not crash`() {
        // GIVEN
        every { firebaseCrashlytics.log(any()) } throws RuntimeException("Firebase error")

        // WHEN
        crashReporter.log("test message")

        // THEN
        verify(exactly = 1) {
            logger.e("Crashlytics", "Failed to log message", any())
        }
    }
}
