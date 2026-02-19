package com.jujodevs.pomodoro.libs.analytics.impl

import com.jujodevs.pomodoro.core.domain.coroutines.AppDispatchers
import com.jujodevs.pomodoro.core.domain.util.DataError
import com.jujodevs.pomodoro.core.domain.util.EmptyResult
import com.jujodevs.pomodoro.core.domain.util.Result
import com.jujodevs.pomodoro.libs.analytics.AnalyticsCollectionManager
import com.jujodevs.pomodoro.libs.datastore.DataStoreManager
import com.jujodevs.pomodoro.libs.logger.Logger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

internal class AnalyticsCollectionManagerImpl(
    private val dataStoreManager: DataStoreManager,
    private val firebaseAnalytics: FirebaseAnalyticsWrapper,
    private val logger: Logger,
    private val applicationScope: CoroutineScope,
    private val appDispatchers: AppDispatchers,
) : AnalyticsCollectionManager {
    private val analyticsEnabledState = MutableStateFlow(DEFAULT_ANALYTICS_ENABLED)

    init {
        firebaseAnalytics.setAnalyticsCollectionEnabled(DEFAULT_ANALYTICS_ENABLED)

        applicationScope.launch(appDispatchers.io) {
            dataStoreManager
                .observeValue(ANALYTICS_ENABLED_KEY, DEFAULT_ANALYTICS_ENABLED)
                .collectLatest { result ->
                    val isEnabled =
                        when (result) {
                            is Result.Success -> result.data
                            is Result.Failure -> {
                                logger.w(TAG, "Failed to read analytics flag. Falling back to disabled.", null)
                                DEFAULT_ANALYTICS_ENABLED
                            }
                        }

                    analyticsEnabledState.value = isEnabled
                    firebaseAnalytics.setAnalyticsCollectionEnabled(isEnabled)
                }
        }
    }

    override fun isAnalyticsEnabled(): Boolean = analyticsEnabledState.value

    override fun observeAnalyticsEnabled(): Flow<Boolean> = analyticsEnabledState.asStateFlow()

    override suspend fun setAnalyticsEnabled(enabled: Boolean): EmptyResult<DataError.Local> {
        val result = dataStoreManager.setValue(ANALYTICS_ENABLED_KEY, enabled)

        if (result is Result.Success) {
            analyticsEnabledState.value = enabled
            firebaseAnalytics.setAnalyticsCollectionEnabled(enabled)
        }

        return result
    }

    private companion object {
        const val TAG = "Analytics"
        const val DEFAULT_ANALYTICS_ENABLED = false
        const val ANALYTICS_ENABLED_KEY = "analytics_enabled"
    }
}
