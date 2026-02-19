package com.jujodevs.pomodoro.libs.analytics.impl

import com.jujodevs.pomodoro.core.domain.coroutines.AppDispatchers
import com.jujodevs.pomodoro.core.domain.util.DataError
import com.jujodevs.pomodoro.core.domain.util.Result
import com.jujodevs.pomodoro.libs.datastore.DataStoreManager
import com.jujodevs.pomodoro.libs.logger.Logger
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import io.mockk.verify
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.amshove.kluent.shouldBeEqualTo
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AnalyticsCollectionManagerImplTest {
    private companion object {
        const val ANALYTICS_ENABLED_KEY = "analytics_enabled"
    }

    private lateinit var dataStoreManager: DataStoreManager
    private lateinit var firebaseAnalyticsWrapper: FirebaseAnalyticsWrapper
    private lateinit var logger: Logger

    @BeforeEach
    fun setUp() {
        dataStoreManager = mockk()
        firebaseAnalyticsWrapper = mockk()
        logger = mockk(relaxed = true)
        every { firebaseAnalyticsWrapper.setAnalyticsCollectionEnabled(any()) } just runs
    }

    private fun createManager(
        applicationScope: CoroutineScope,
        appDispatchers: AppDispatchers,
    ) = AnalyticsCollectionManagerImpl(
        dataStoreManager = dataStoreManager,
        firebaseAnalytics = firebaseAnalyticsWrapper,
        logger = logger,
        applicationScope = applicationScope,
        appDispatchers = appDispatchers,
    )

    @Test
    fun `GIVEN stored consent true WHEN initialized THEN analytics should be enabled`() =
        runTest {
            every {
                dataStoreManager.observeValue(ANALYTICS_ENABLED_KEY, false)
            } returns flowOf(Result.Success(true))
            val testDispatcher = StandardTestDispatcher(testScheduler)

            val manager =
                createManager(
                    applicationScope = this,
                    appDispatchers = TestAppDispatchers(testDispatcher),
                )

            advanceUntilIdle()

            manager.isAnalyticsEnabled() shouldBeEqualTo true
            verify(exactly = 1) { firebaseAnalyticsWrapper.setAnalyticsCollectionEnabled(true) }
        }

    @Test
    fun `GIVEN setAnalyticsEnabled true WHEN persisting succeeds THEN should update local state`() =
        runTest {
            every {
                dataStoreManager.observeValue(ANALYTICS_ENABLED_KEY, false)
            } returns flowOf(Result.Success(false))
            coEvery {
                dataStoreManager.setValue(ANALYTICS_ENABLED_KEY, true)
            } returns Result.Success(Unit)
            val testDispatcher = StandardTestDispatcher(testScheduler)

            val manager =
                createManager(
                    applicationScope = this,
                    appDispatchers = TestAppDispatchers(testDispatcher),
                )

            advanceUntilIdle()
            manager.setAnalyticsEnabled(true)

            manager.isAnalyticsEnabled() shouldBeEqualTo true
            coVerify(exactly = 1) {
                dataStoreManager.setValue(ANALYTICS_ENABLED_KEY, true)
            }
        }

    @Test
    fun `GIVEN observe fails WHEN initialized THEN should fallback to disabled`() =
        runTest {
            every {
                dataStoreManager.observeValue(ANALYTICS_ENABLED_KEY, false)
            } returns flowOf(Result.Failure(DataError.Local.UNKNOWN))
            val testDispatcher = StandardTestDispatcher(testScheduler)

            val manager =
                createManager(
                    applicationScope = this,
                    appDispatchers = TestAppDispatchers(testDispatcher),
                )

            advanceUntilIdle()

            manager.isAnalyticsEnabled() shouldBeEqualTo false
            verify(exactly = 1) {
                logger.w(
                    "Analytics",
                    "Failed to read analytics flag. Falling back to disabled.",
                    null,
                )
            }
        }
}

private class TestAppDispatchers(
    private val dispatcher: CoroutineDispatcher,
) : AppDispatchers {
    override val io: CoroutineDispatcher = dispatcher
    override val default: CoroutineDispatcher = dispatcher
    override val main: CoroutineDispatcher = dispatcher
}
