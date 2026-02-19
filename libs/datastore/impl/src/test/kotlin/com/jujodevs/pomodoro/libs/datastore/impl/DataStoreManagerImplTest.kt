package com.jujodevs.pomodoro.libs.datastore.impl

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import app.cash.turbine.test
import com.jujodevs.pomodoro.core.domain.util.Result
import com.jujodevs.pomodoro.core.domain.util.isSuccess
import com.jujodevs.pomodoro.libs.datastore.DataStoreManager
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.amshove.kluent.shouldBe
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

        dataStore =
            PreferenceDataStoreFactory.create(
                scope = testScope,
                produceFile = { File.createTempFile("test_datastore", ".preferences_pb") },
            )

        dataStoreManager = DataStoreManagerImpl(dataStore)
    }

    @Test
    fun `GIVEN key with value WHEN getValue THEN should return stored value`() =
        runTest {
            // GIVEN
            dataStoreManager.setValue("test_key", "test_value").isSuccess shouldBe true

            // WHEN
            val result = dataStoreManager.getValue("test_key", "")

            // THEN
            result shouldBeEqualTo Result.Success("test_value")
        }

    @Test
    fun `GIVEN no value WHEN getValue THEN should return default value`() =
        runTest {
            // WHEN
            val result = dataStoreManager.getValue("non_existent", "default")

            // THEN
            result shouldBeEqualTo Result.Success("default")
        }

    @Test
    fun `GIVEN value set WHEN observeValue THEN should emit changes`() =
        runTest {
            // WHEN
            val flow = dataStoreManager.observeValue("test_key", 0)

            // THEN
            flow.test {
                awaitItem() shouldBeEqualTo Result.Success(0)

                dataStoreManager.setValue("test_key", 42).isSuccess shouldBe true
                awaitItem() shouldBeEqualTo Result.Success(42)

                dataStoreManager.setValue("test_key", 100).isSuccess shouldBe true
                awaitItem() shouldBeEqualTo Result.Success(100)
            }
        }

    @Test
    fun `GIVEN int value WHEN getValue THEN should return int`() =
        runTest {
            // GIVEN
            dataStoreManager.setValue("int_key", 25).isSuccess shouldBe true

            // WHEN
            val result = dataStoreManager.getValue("int_key", 0)

            // THEN
            result shouldBeEqualTo Result.Success(25)
        }

    @Test
    fun `GIVEN boolean value WHEN getValue THEN should return boolean`() =
        runTest {
            // GIVEN
            dataStoreManager.setValue("bool_key", true).isSuccess shouldBe true

            // WHEN
            val result = dataStoreManager.getValue("bool_key", false)

            // THEN
            result shouldBeEqualTo Result.Success(true)
        }

    @Test
    fun `GIVEN value WHEN removeValue THEN should remove it`() =
        runTest {
            // GIVEN
            dataStoreManager.setValue("remove_key", "value").isSuccess shouldBe true
            dataStoreManager.getValue("remove_key", "") shouldBeEqualTo Result.Success("value")

            // WHEN
            dataStoreManager.removeValue("remove_key").isSuccess shouldBe true

            // THEN
            dataStoreManager.getValue("remove_key", "default") shouldBeEqualTo Result.Success("default")
        }

    @Test
    fun `GIVEN values WHEN clear THEN should remove all`() =
        runTest {
            // GIVEN
            dataStoreManager.setValue("key1", "value1").isSuccess shouldBe true
            dataStoreManager.setValue("key2", "value2").isSuccess shouldBe true

            // WHEN
            dataStoreManager.clear().isSuccess shouldBe true

            // THEN
            dataStoreManager.getValue("key1", "default1") shouldBeEqualTo Result.Success("default1")
            dataStoreManager.getValue("key2", "default2") shouldBeEqualTo Result.Success("default2")
        }

    @Test
    fun `GIVEN string value WHEN removeValue THEN should return default`() =
        runTest {
            // GIVEN
            val key = "sample_string"
            dataStoreManager.setValue(key, "stored").isSuccess shouldBe true

            // WHEN
            dataStoreManager.removeValue(key).isSuccess shouldBe true

            // THEN
            dataStoreManager.getValue(key, "default") shouldBeEqualTo Result.Success("default")
        }

    @Test
    fun `GIVEN string set WHEN removeValue THEN should return default`() =
        runTest {
            // GIVEN
            val key = "sample_set"
            dataStoreManager.setValue(key, setOf("id1", "id2")).isSuccess shouldBe true

            // WHEN
            dataStoreManager.removeValue(key).isSuccess shouldBe true

            // THEN
            val result = dataStoreManager.getValue(key, emptySet<String>())
            result shouldBeEqualTo Result.Success(emptySet<String>())
        }

    @Test
    fun `GIVEN multiple keys WHEN removeValue one THEN others should remain`() =
        runTest {
            // GIVEN
            val setKey = "set_key"
            val stringKey = "string_key"
            dataStoreManager.setValue(setKey, setOf("id1")).isSuccess shouldBe true
            dataStoreManager.setValue(stringKey, "string_value").isSuccess shouldBe true

            // WHEN
            dataStoreManager.removeValue(setKey).isSuccess shouldBe true

            // THEN
            dataStoreManager.getValue(setKey, emptySet<String>()) shouldBeEqualTo
                Result.Success(
                    emptySet<String>(),
                )
            dataStoreManager.getValue(stringKey, "") shouldBeEqualTo Result.Success("string_value")
        }
}
