package com.jujodevs.pomodoro.features.settings.domain.usecase

import com.jujodevs.pomodoro.libs.permissions.PermissionManager

class GetCanScheduleExactAlarmsUseCase(
    private val permissionManager: PermissionManager,
) {
    operator fun invoke(): Boolean = permissionManager.canScheduleExactAlarms()
}
