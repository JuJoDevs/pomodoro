package com.jujodevs.pomodoro.libs.datastore.impl.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.jujodevs.pomodoro.libs.datastore.DataStoreManager
import com.jujodevs.pomodoro.libs.datastore.impl.DataStoreManagerImpl
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val dataStoreModule =
    module {
        single<DataStore<Preferences>> {
            androidContext().dataStore
        }

        single<DataStoreManager> {
            DataStoreManagerImpl(get())
        }
    }

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
    name = "pomodoro_preferences",
)
