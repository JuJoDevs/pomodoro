package com.jujodevs.pomodoro.libs.datastore

/**
 * Keys for internal system state that are not directly configurable by the user.
 */
object InternalStateKeys {
    const val SCHEDULED_NOTIFICATION_IDS = "scheduled_notification_ids"
    const val ACTIVE_NOTIFICATION_TOKEN = "active_notification_token"
    const val HAS_COMPLETED_ONBOARDING = "has_completed_onboarding"
}
