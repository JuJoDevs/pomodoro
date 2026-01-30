package com.jujodevs.pomodoro.libs.datastore

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

/**
 * Fake implementation of DataStoreManager for testing purposes.
 *
 * Uses in-memory storage and provides the same interface as the real implementation.
 */
class FakeDataStoreManager : DataStoreManager {
    private val storage = mutableMapOf<String, Any?>()
    private val flows = mutableMapOf<String, MutableStateFlow<Any?>>()

    override suspend fun <T> getValue(
        key: String,
        defaultValue: T,
    ): T = (storage[key] as? T) ?: defaultValue

    override suspend fun <T> setValue(
        key: String,
        value: T,
    ) {
        storage[key] = value
        flows[key]?.value = value
    }

    override suspend fun removeValue(key: String) {
        storage.remove(key)
        flows[key]?.value = null
    }

    override suspend fun clear() {
        storage.clear()
        flows.values.forEach { it.value = null }
    }

    override fun <T> observeValue(
        key: String,
        defaultValue: T,
    ): Flow<T> {
        val flow =
            flows.getOrPut(key) {
                MutableStateFlow(storage[key] ?: defaultValue)
            }
        return flow.map { (it as? T) ?: defaultValue }
    }
}
