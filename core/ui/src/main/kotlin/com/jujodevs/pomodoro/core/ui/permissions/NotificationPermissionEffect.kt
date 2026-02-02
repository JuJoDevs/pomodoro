package com.jujodevs.pomodoro.core.ui.permissions

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
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
    onPermissionResult: (Boolean) -> Unit = {}
) {
    val permissionString = remember { permissionManager.getNotificationPermissionString() }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        onPermissionResult(isGranted)
    }

    LaunchedEffect(Unit) {
        if (permissionString != null) {
            if (!permissionManager.hasNotificationPermission()) {
                launcher.launch(permissionString)
            } else {
                onPermissionResult(true)
            }
        } else {
            // Permission not required for this API level
            onPermissionResult(true)
        }
    }
}
