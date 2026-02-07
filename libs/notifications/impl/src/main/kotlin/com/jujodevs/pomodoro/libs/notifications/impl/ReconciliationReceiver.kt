package com.jujodevs.pomodoro.libs.notifications.impl

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.jujodevs.pomodoro.libs.logger.Logger
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

/**
 * BroadcastReceiver that handles system events to reconcile scheduled notifications.
 * Handles events like BOOT_COMPLETED, TIME_SET, and TIMEZONE_CHANGED.
 */
class ReconciliationReceiver : BroadcastReceiver(), KoinComponent {

    private val logger: Logger by inject()

    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action ?: return
        logger.d(TAG, "Received system event: $action")

        when (action) {
            Intent.ACTION_BOOT_COMPLETED,
            Intent.ACTION_TIME_CHANGED,
            Intent.ACTION_TIMEZONE_CHANGED -> {
                // Here we would ideally trigger a reconciliation process.
                // In this architecture, we might want to notify the feature modules
                // or have a general NotificationReconciler service.
                // For v1, we'll keep it simple and assume the app will reconcile on next start,
                // but we could also start a WorkManager task here.
            }
        }
    }

    companion object {
        private const val TAG = "ReconciliationReceiver"
    }
}
