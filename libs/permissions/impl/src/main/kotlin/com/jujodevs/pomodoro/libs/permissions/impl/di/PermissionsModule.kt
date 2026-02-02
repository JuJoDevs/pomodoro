package com.jujodevs.pomodoro.libs.permissions.impl.di

import com.jujodevs.pomodoro.libs.permissions.PermissionManager
import com.jujodevs.pomodoro.libs.permissions.impl.PermissionManagerImpl
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val permissionsModule = module {
    single<PermissionManager> { PermissionManagerImpl(androidContext()) }
}
