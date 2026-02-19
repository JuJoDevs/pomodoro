package com.jujodevs.pomodoro.features.settings.domain.usecase

import com.jujodevs.pomodoro.libs.permissions.PermissionManager

class GetHasNotificationPermissionUseCase(
    private val permissionManager: PermissionManager,
) {
    operator fun invoke(): Boolean = permissionManager.hasNotificationPermission()
}
