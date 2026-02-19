package com.jujodevs.pomodoro.core.ui.permissions

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import com.jujodevs.pomodoro.libs.permissions.PermissionManager
import org.koin.compose.koinInject

/**
 * A Composable effect that checks and requests notification permissions.
 *
 * @param onPermissionResult Callback invoked with the result of the permission request.
 */
@Composable
fun NotificationPermissionEffect(
    permissionManager: PermissionManager = koinInject(),
    onPermissionResult: (Boolean) -> Unit = {},
) {
    RequestNotificationPermissionOnTrigger(
        trigger = true,
        permissionManager = permissionManager,
        onPermissionResult = onPermissionResult,
    )
}

/**
 * A Composable that requests notification permission when [trigger] becomes true.
 *
 * Use this for user-triggered permission flows (e.g. from Settings screen).
 *
 * @param trigger When true, launches the permission request. Caller should reset after.
 * @param onPermissionResult Callback invoked with the result.
 */
@Composable
fun RequestNotificationPermissionOnTrigger(
    trigger: Boolean,
    permissionManager: PermissionManager = koinInject(),
    onPermissionResult: (Boolean) -> Unit = {},
) {
    val permissionString = remember { permissionManager.getNotificationPermissionString() }
    val onPermissionResultState by rememberUpdatedState(newValue = onPermissionResult)

    val launcher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestPermission(),
        ) { isGranted ->
            onPermissionResultState(isGranted)
        }

    LaunchedEffect(trigger) {
        if (!trigger) return@LaunchedEffect
        if (permissionString != null) {
            if (!permissionManager.hasNotificationPermission()) {
                launcher.launch(permissionString)
            } else {
                onPermissionResultState(true)
            }
        } else {
            onPermissionResultState(true)
        }
    }
}
