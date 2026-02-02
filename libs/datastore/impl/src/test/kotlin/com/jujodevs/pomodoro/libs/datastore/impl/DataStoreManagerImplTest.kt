package com.jujodevs.pomodoro.libs.datastore.impl

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import app.cash.turbine.test
import com.jujodevs.pomodoro.libs.datastore.DataStoreManager
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.amshove.kluent.shouldBeEqualTo
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.io.File

@OptIn(ExperimentalCoroutinesApi::class)
class DataStoreManagerImplTest {

    private lateinit var dataStore: DataStore<Preferences>
    private lateinit var dataStoreManager: DataStoreManager

    @BeforeEach
    fun setUp() {
        val testDispatcher = UnconfinedTestDispatcher()
        val testScope = TestScope(testDispatcher)

        dataStore = PreferenceDataStoreFactory.create(
            scope = testScope,
            produceFile = { File.createTempFile("test_datastore", ".preferences_pb") }
        )

        dataStoreManager = DataStoreManagerImpl(dataStore)
    }

    @Test
    fun `GIVEN key with value WHEN getValue THEN should return stored value`() = runTest {
        // GIVEN
        dataStoreManager.setValue("test_key", "test_value")

        // WHEN
        val result = dataStoreManager.getValue("test_key", "")

        // THEN
        result shouldBeEqualTo "test_value"
    }

    @Test
    fun `GIVEN no value WHEN getValue THEN should return default value`() = runTest {
        // WHEN
        val result = dataStoreManager.getValue("non_existent", "default")

        // THEN
        result shouldBeEqualTo "default"
    }

    @Test
    fun `GIVEN value set WHEN observeValue THEN should emit changes`() = runTest {
        // WHEN
        val flow = dataStoreManager.observeValue("test_key", 0)

        // THEN
        flow.test {
            awaitItem() shouldBeEqualTo 0

            dataStoreManager.setValue("test_key", 42)
            awaitItem() shouldBeEqualTo 42

            dataStoreManager.setValue("test_key", 100)
            awaitItem() shouldBeEqualTo 100
        }
    }

    @Test
    fun `GIVEN int value WHEN getValue THEN should return int`() = runTest {
        // GIVEN
        dataStoreManager.setValue("int_key", 25)

        // WHEN
        val result = dataStoreManager.getValue("int_key", 0)

        // THEN
        result shouldBeEqualTo 25
    }

    @Test
    fun `GIVEN boolean value WHEN getValue THEN should return boolean`() = runTest {
        // GIVEN
        dataStoreManager.setValue("bool_key", true)

        // WHEN
        val result = dataStoreManager.getValue("bool_key", false)

        // THEN
        result shouldBeEqualTo true
    }

    @Test
    fun `GIVEN value WHEN removeValue THEN should remove it`() = runTest {
        // GIVEN
        dataStoreManager.setValue("remove_key", "value")
        dataStoreManager.getValue("remove_key", "") shouldBeEqualTo "value"

        // WHEN
        dataStoreManager.removeValue("remove_key")

        // THEN
        dataStoreManager.getValue("remove_key", "default") shouldBeEqualTo "default"
    }

    @Test
    fun `GIVEN values WHEN clear THEN should remove all`() = runTest {
        // GIVEN
        dataStoreManager.setValue("key1", "value1")
        dataStoreManager.setValue("key2", "value2")

        // WHEN
        dataStoreManager.clear()

        // THEN
        dataStoreManager.getValue("key1", "default1") shouldBeEqualTo "default1"
        dataStoreManager.getValue("key2", "default2") shouldBeEqualTo "default2"
    }

    @Test
    fun `GIVEN string value WHEN removeValue THEN should return default`() = runTest {
        // GIVEN
        val key = "sample_string"
        dataStoreManager.setValue(key, "stored")

        // WHEN
        dataStoreManager.removeValue(key)

        // THEN
        dataStoreManager.getValue(key, "default") shouldBeEqualTo "default"
    }

    @Test
    fun `GIVEN string set WHEN removeValue THEN should return default`() = runTest {
        // GIVEN
        val key = "sample_set"
        dataStoreManager.setValue(key, setOf("id1", "id2"))

        // WHEN
        dataStoreManager.removeValue(key)

        // THEN
        val result = dataStoreManager.getValue(key, emptySet<String>())
        result shouldBeEqualTo emptySet<String>()
    }

    @Test
    fun `GIVEN multiple keys WHEN removeValue one THEN others should remain`() = runTest {
        // GIVEN
        val setKey = "set_key"
        val stringKey = "string_key"
        dataStoreManager.setValue(setKey, setOf("id1"))
        dataStoreManager.setValue(stringKey, "string_value")

        // WHEN
        dataStoreManager.removeValue(setKey)

        // THEN
        dataStoreManager.getValue(setKey, emptySet<String>()) shouldBeEqualTo emptySet<String>()
        dataStoreManager.getValue(stringKey, "") shouldBeEqualTo "string_value"
    }
}
