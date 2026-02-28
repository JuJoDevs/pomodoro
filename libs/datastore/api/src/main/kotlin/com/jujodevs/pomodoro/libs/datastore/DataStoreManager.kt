package com.jujodevs.pomodoro.libs.datastore

import com.jujodevs.pomodoro.core.domain.util.DataError
import com.jujodevs.pomodoro.core.domain.util.EmptyResult
import com.jujodevs.pomodoro.core.domain.util.Result
import kotlinx.coroutines.flow.Flow

/**
 * Interface for managing app preferences and configuration using DataStore.
 *
 * Provides type-safe access to key-value preferences with support for:
 * - String, Int, Boolean, Float, Long types
 * - Reactive observation via Flow
 * - Default values for missing keys
 */
interface DataStoreManager {
    /**
     * Get a value from DataStore.
     *
     * @param key The preference key
     * @param defaultValue The default value to return if key doesn't exist
     * @return The stored value or defaultValue
     */
    suspend fun <T> getValue(
        key: String,
        defaultValue: T,
    ): Result<T, DataError.Local>

    /**
     * Set a value in DataStore.
     *
     * @param key The preference key
     * @param value The value to store
     */
    suspend fun <T> setValue(
        key: String,
        value: T,
    ): EmptyResult<DataError.Local>

    /**
     * Set multiple values in a single DataStore transaction.
     *
     * Useful for state updates that need to persist several keys together
     * while minimizing disk writes and emission churn.
     *
     * @param values Map of key/value pairs to store
     */
    suspend fun setValues(values: Map<String, Any>): EmptyResult<DataError.Local>

    /**
     * Remove a value from DataStore.
     *
     * @param key The preference key to remove
     */
    suspend fun removeValue(key: String): EmptyResult<DataError.Local>

    /**
     * Clear all preferences.
     */
    suspend fun clear(): EmptyResult<DataError.Local>

    /**
     * Observe a value as a Flow.
     *
     * @param key The preference key
     * @param defaultValue The default value to emit if key doesn't exist
     * @return Flow that emits the current value and updates when it changes
     */
    fun <T> observeValue(
        key: String,
        defaultValue: T,
    ): Flow<Result<T, DataError.Local>>
}
