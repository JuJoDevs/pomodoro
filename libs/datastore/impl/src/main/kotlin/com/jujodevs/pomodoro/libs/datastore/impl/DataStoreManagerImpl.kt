package com.jujodevs.pomodoro.libs.datastore.impl

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.MutablePreferences
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import com.jujodevs.pomodoro.core.domain.util.DataError
import com.jujodevs.pomodoro.core.domain.util.EmptyResult
import com.jujodevs.pomodoro.core.domain.util.Result
import com.jujodevs.pomodoro.core.domain.util.map
import com.jujodevs.pomodoro.libs.datastore.DataStoreManager
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.io.IOException

/**
 * Android implementation of DataStoreManager using DataStore Preferences.
 */
class DataStoreManagerImpl(
    private val dataStore: DataStore<Preferences>
) : DataStoreManager {

    override suspend fun <T> getValue(key: String, defaultValue: T): Result<T, DataError.Local> {
        return runOperation {
            dataStore.data.map { preferences ->
                getPreferenceValue(preferences, key, defaultValue)
            }.first()
        }
    }

    override suspend fun <T> setValue(key: String, value: T): EmptyResult<DataError.Local> {
        return runEmptyOperation {
            dataStore.edit { preferences ->
                setPreferenceValue(preferences, key, value)
            }
        }
    }

    override suspend fun removeValue(key: String): EmptyResult<DataError.Local> {
        return runEmptyOperation {
            dataStore.edit { preferences ->
                preferences.remove(stringPreferencesKey(key))
                preferences.remove(intPreferencesKey(key))
                preferences.remove(booleanPreferencesKey(key))
                preferences.remove(floatPreferencesKey(key))
                preferences.remove(longPreferencesKey(key))
                preferences.remove(stringSetPreferencesKey(key))
            }
        }
    }

    override suspend fun clear(): EmptyResult<DataError.Local> {
        return runEmptyOperation {
            dataStore.edit { it.clear() }
        }
    }

    override fun <T> observeValue(key: String, defaultValue: T): Flow<Result<T, DataError.Local>> {
        return dataStore.data
            .map<Preferences, Result<T, DataError.Local>> { preferences ->
                Result.Success(getPreferenceValue(preferences, key, defaultValue))
            }
            .catch { throwable ->
                if (throwable is CancellationException) {
                    throw throwable
                }
                emit(Result.Failure(throwable.toDataStoreError()))
            }
    }

    @Suppress("UNCHECKED_CAST")
    private fun <T> getPreferenceValue(preferences: Preferences, key: String, defaultValue: T): T {
        return when (defaultValue) {
            is String -> preferences[stringPreferencesKey(key)] ?: defaultValue
            is Int -> preferences[intPreferencesKey(key)] ?: defaultValue
            is Boolean -> preferences[booleanPreferencesKey(key)] ?: defaultValue
            is Float -> preferences[floatPreferencesKey(key)] ?: defaultValue
            is Long -> preferences[longPreferencesKey(key)] ?: defaultValue
            is Set<*> -> preferences[stringSetPreferencesKey(key)] ?: defaultValue
            else -> defaultValue
        } as T
    }

    private fun <T> setPreferenceValue(preferences: MutablePreferences, key: String, value: T) {
        when (value) {
            is String -> preferences[stringPreferencesKey(key)] = value
            is Int -> preferences[intPreferencesKey(key)] = value
            is Boolean -> preferences[booleanPreferencesKey(key)] = value
            is Float -> preferences[floatPreferencesKey(key)] = value
            is Long -> preferences[longPreferencesKey(key)] = value
            is Set<*> -> {
                if (value.all { it is String }) {
                    preferences[stringSetPreferencesKey(key)] = value.filterIsInstance<String>().toSet()
                }
            }
        }
    }

    private suspend fun <T> runOperation(
        operation: suspend () -> T
    ): Result<T, DataError.Local> {
        return runCatching {
            operation()
        }.fold(
            onSuccess = { value ->
                Result.Success(value)
            },
            onFailure = { throwable ->
                if (throwable is CancellationException) {
                    throw throwable
                }
                Result.Failure(throwable.toDataStoreError())
            }
        )
    }

    private suspend fun runEmptyOperation(
        operation: suspend () -> Unit
    ): EmptyResult<DataError.Local> {
        return runOperation(operation).map { Unit }
    }

    private fun Throwable.toDataStoreError(): DataError.Local {
        return when (this) {
            is SecurityException -> DataError.Local.INSUFFICIENT_PERMISSIONS
            is IOException -> DataError.Local.DISK_FULL
            else -> DataError.Local.UNKNOWN
        }
    }
}
