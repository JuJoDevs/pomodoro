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
import com.jujodevs.pomodoro.libs.datastore.DataStoreManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

/**
 * Android implementation of DataStoreManager using DataStore Preferences.
 */
class DataStoreManagerImpl(
    private val dataStore: DataStore<Preferences>
) : DataStoreManager {

    override suspend fun <T> getValue(key: String, defaultValue: T): T {
        return dataStore.data.map { preferences ->
            getPreferenceValue(preferences, key, defaultValue)
        }.first()
    }

    override suspend fun <T> setValue(key: String, value: T) {
        dataStore.edit { preferences ->
            setPreferenceValue(preferences, key, value)
        }
    }

    override suspend fun removeValue(key: String) {
        dataStore.edit { preferences ->
            preferences.remove(stringPreferencesKey(key))
        }
    }

    override suspend fun clear() {
        dataStore.edit { it.clear() }
    }

    override fun <T> observeValue(key: String, defaultValue: T): Flow<T> {
        return dataStore.data.map { preferences ->
            getPreferenceValue(preferences, key, defaultValue)
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
        }
    }
}
