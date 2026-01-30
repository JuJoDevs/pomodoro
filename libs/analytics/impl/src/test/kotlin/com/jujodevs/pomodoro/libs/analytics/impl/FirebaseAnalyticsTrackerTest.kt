package com.jujodevs.pomodoro.libs.analytics.impl

import com.jujodevs.pomodoro.libs.analytics.CustomAnalyticsEvent
import com.jujodevs.pomodoro.libs.analytics.UserProperty
import com.jujodevs.pomodoro.libs.logger.Logger
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class FirebaseAnalyticsTrackerTest {

    private val firebaseAnalytics: FirebaseAnalyticsWrapper = mockk()
    private val logger: Logger = mockk(relaxed = true)

    private lateinit var tracker: FirebaseAnalyticsTracker

    @BeforeEach
    fun setUp() {
        every { firebaseAnalytics.logEvent(any(), any()) } just runs
        every { firebaseAnalytics.setUserProperty(any(), any()) } just runs
        every { firebaseAnalytics.setUserId(any()) } just runs
        every { firebaseAnalytics.resetAnalyticsData() } just runs

        tracker = FirebaseAnalyticsTracker(firebaseAnalytics, logger)
    }

    @Test
    fun `GIVEN valid event WHEN track THEN should log event to Firebase`() {
        // GIVEN
        val event = CustomAnalyticsEvent(
            name = "test_event",
            parameters = mapOf(
                "param1" to "value1",
                "param2" to 42
            )
        )

        // WHEN
        tracker.track(event)

        // THEN
        verify(exactly = 1) {
            firebaseAnalytics.logEvent("test_event", any())
        }
        verify(exactly = 1) {
            logger.d("Analytics", "Event tracked: test_event")
        }
    }

    @Test
    fun `GIVEN event with all parameter types WHEN track THEN should map all types correctly`() {
        // GIVEN
        val event = CustomAnalyticsEvent(
            name = "complex_event",
            parameters = mapOf(
                "string_param" to "value",
                "int_param" to 10,
                "long_param" to 100L,
                "double_param" to 3.14,
                "bool_param" to true
            )
        )

        // WHEN
        tracker.track(event)

        // THEN
        verify(exactly = 1) {
            firebaseAnalytics.logEvent("complex_event", any())
        }
    }

    @Test
    fun `GIVEN Firebase throws exception WHEN track THEN should log error and not crash`() {
        // GIVEN
        val event = CustomAnalyticsEvent(
            name = "failing_event",
            parameters = emptyMap()
        )
        every { firebaseAnalytics.logEvent(any(), any()) } throws RuntimeException("Firebase error")

        // WHEN
        tracker.track(event)

        // THEN
        verify(exactly = 1) {
            logger.e("Analytics", "Failed to track event: failing_event", any())
        }
    }

    @Test
    fun `GIVEN string user property WHEN setUserProperty THEN should set property in Firebase`() {
        // GIVEN
        val property = UserProperty("test_key", "test_value")

        // WHEN
        tracker.setUserProperty(property)

        // THEN
        verify(exactly = 1) {
            firebaseAnalytics.setUserProperty("test_key", "test_value")
        }
        verify(exactly = 1) {
            logger.d("Analytics", "User property set: test_key = test_value")
        }
    }

    @Test
    fun `GIVEN int user property WHEN setUserProperty THEN should convert to string and set`() {
        // GIVEN
        val property = UserProperty("count", 42)

        // WHEN
        tracker.setUserProperty(property)

        // THEN
        verify(exactly = 1) {
            firebaseAnalytics.setUserProperty("count", "42")
        }
    }

    @Test
    fun `GIVEN boolean user property WHEN setUserProperty THEN should convert to string and set`() {
        // GIVEN
        val property = UserProperty("enabled", true)

        // WHEN
        tracker.setUserProperty(property)

        // THEN
        verify(exactly = 1) {
            firebaseAnalytics.setUserProperty("enabled", "true")
        }
    }

    @Test
    fun `GIVEN unsupported parameter type in event WHEN track THEN should log warning but still track event`() {
        // GIVEN
        val event = CustomAnalyticsEvent(
            name = "event_with_unsupported_param",
            parameters = mapOf(
                "valid_param" to "value",
                "unsupported_param" to listOf("item1", "item2") // List is not supported
            )
        )

        // WHEN
        tracker.track(event)

        // THEN
        verify(exactly = 1) {
            firebaseAnalytics.logEvent("event_with_unsupported_param", any())
        }
        verify(exactly = 1) {
            logger.w("Analytics", "Unsupported parameter type for key 'unsupported_param': ArrayList", null)
        }
    }

    @Test
    fun `GIVEN valid userId WHEN setUserId THEN should set userId in Firebase`() {
        // GIVEN
        val userId = "user123"

        // WHEN
        tracker.setUserId(userId)

        // THEN
        verify(exactly = 1) {
            firebaseAnalytics.setUserId("user123")
        }
        verify(exactly = 1) {
            logger.d("Analytics", "User ID set: user123")
        }
    }

    @Test
    fun `GIVEN null userId WHEN setUserId THEN should clear userId in Firebase`() {
        // GIVEN
        val userId: String? = null

        // WHEN
        tracker.setUserId(userId)

        // THEN
        verify(exactly = 1) {
            firebaseAnalytics.setUserId(null)
        }
        verify(exactly = 1) {
            logger.d("Analytics", "User ID set: null")
        }
    }

    @Test
    fun `GIVEN Firebase throws exception WHEN setUserId THEN should log error and not crash`() {
        // GIVEN
        every { firebaseAnalytics.setUserId(any()) } throws RuntimeException("Firebase error")

        // WHEN
        tracker.setUserId("user123")

        // THEN
        verify(exactly = 1) {
            logger.e("Analytics", "Failed to set user ID", any())
        }
    }

    @Test
    fun `GIVEN resetAnalyticsData called WHEN resetAnalyticsData THEN should reset data in Firebase`() {
        // WHEN
        tracker.resetAnalyticsData()

        // THEN
        verify(exactly = 1) {
            firebaseAnalytics.resetAnalyticsData()
        }
        verify(exactly = 1) {
            logger.d("Analytics", "Analytics data reset")
        }
    }

    @Test
    fun `GIVEN Firebase throws exception WHEN resetAnalyticsData THEN should log error and not crash`() {
        // GIVEN
        every { firebaseAnalytics.resetAnalyticsData() } throws RuntimeException("Firebase error")

        // WHEN
        tracker.resetAnalyticsData()

        // THEN
        verify(exactly = 1) {
            logger.e("Analytics", "Failed to reset analytics data", any())
        }
    }
}
