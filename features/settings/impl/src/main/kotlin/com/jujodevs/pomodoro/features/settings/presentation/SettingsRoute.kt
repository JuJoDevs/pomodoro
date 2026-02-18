package com.jujodevs.pomodoro.features.settings.presentation

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.provider.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jujodevs.pomodoro.core.resources.R
import com.jujodevs.pomodoro.core.ui.ObserveAsEvents
import com.jujodevs.pomodoro.core.ui.permissions.ExactAlarmPermissionEffect
import com.jujodevs.pomodoro.core.ui.permissions.RequestNotificationPermissionOnTrigger
import com.jujodevs.pomodoro.libs.notifications.NotificationChannel
import org.koin.androidx.compose.koinViewModel

@Composable
fun SettingsRoute(viewModel: SettingsViewModel = koinViewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current
    var requestExactAlarmPermission by remember { mutableStateOf(false) }
    var requestNotificationPermission by remember { mutableStateOf(false) }
    val versionText = remember { getVersionText(context) }

    ExactAlarmPermissionEffect(
        requestOnMissingPermission = requestExactAlarmPermission
    ) {
        requestExactAlarmPermission = false
        viewModel.refreshPermissionAndAlarmState()
    }

    RequestNotificationPermissionOnTrigger(
        trigger = requestNotificationPermission
    ) {
        requestNotificationPermission = false
        viewModel.refreshPermissionAndAlarmState()
    }

    ObserveAsEvents(viewModel.effects) { effect ->
        when (effect) {
            is SettingsEffect.OpenNotificationChannelSettings -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    val intent = Intent(Settings.ACTION_CHANNEL_NOTIFICATION_SETTINGS).apply {
                        putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
                        putExtra(Settings.EXTRA_CHANNEL_ID, NotificationChannel.PomodoroSession.id)
                    }
                    context.startActivity(intent)
                    viewModel.refreshPermissionAndAlarmState()
                }
            }
            is SettingsEffect.GrantExactAlarmPermission -> requestExactAlarmPermission = true
            is SettingsEffect.RequestNotificationPermission -> requestNotificationPermission = true
        }
    }

    LaunchedEffect(Unit) {
        viewModel.refreshPermissionAndAlarmState()
    }

    SettingsScreen(
        state = state,
        versionText = versionText,
        onAction = viewModel::onAction
    )
}

private fun getVersionText(context: Context): String {
    val packageInfo = try {
        context.packageManager.getPackageInfo(context.packageName, 0)
    } catch (_: PackageManager.NameNotFoundException) {
        null
    }
    val versionName = packageInfo?.versionName ?: "?"
    val versionCode = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
        packageInfo?.longVersionCode?.toString() ?: "?"
    } else {
        @Suppress("DEPRECATION")
        (packageInfo?.versionCode ?: 0).toString()
    }
    return context.getString(R.string.settings_version_format, versionName, versionCode)
}
