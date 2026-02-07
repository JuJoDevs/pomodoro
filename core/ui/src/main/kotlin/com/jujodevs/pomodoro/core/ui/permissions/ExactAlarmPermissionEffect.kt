package com.jujodevs.pomodoro.core.ui.permissions

import android.content.Intent
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.core.net.toUri
import com.jujodevs.pomodoro.libs.permissions.PermissionManager
import org.koin.compose.koinInject

/**
 * A Composable effect that checks and helps requesting exact alarm permissions.
 *
 * @param requestOnMissingPermission Whether to launch exact alarm settings if permission is missing.
 * @param onPermissionResult Callback invoked with current status.
 */
@Composable
fun ExactAlarmPermissionEffect(
    permissionManager: PermissionManager = koinInject(),
    requestOnMissingPermission: Boolean = true,
    onPermissionResult: (Boolean) -> Unit = {},
) {
    val context = LocalContext.current

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) {
        onPermissionResult(permissionManager.canScheduleExactAlarms())
    }

    LaunchedEffect(requestOnMissingPermission) {
        if (!permissionManager.canScheduleExactAlarms()) {
            if (requestOnMissingPermission && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val intent = Intent(
                    Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM,
                    "package:${context.packageName}".toUri()
                )
                launcher.launch(intent)
            } else {
                onPermissionResult(false)
            }
        } else {
            onPermissionResult(true)
        }
    }
}
