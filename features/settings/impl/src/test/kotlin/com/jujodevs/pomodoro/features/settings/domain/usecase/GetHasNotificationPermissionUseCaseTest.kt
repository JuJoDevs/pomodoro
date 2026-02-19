package com.jujodevs.pomodoro.features.settings.domain.usecase

import com.jujodevs.pomodoro.libs.permissions.PermissionManager
import io.mockk.every
import io.mockk.mockk
import org.amshove.kluent.shouldBeEqualTo
import org.junit.jupiter.api.Test

class GetHasNotificationPermissionUseCaseTest {
    private val permissionManager: PermissionManager = mockk()
    private val useCase = GetHasNotificationPermissionUseCase(permissionManager)

    @Test
    fun `GIVEN notification permission granted WHEN invoking use case THEN return true`() {
        every { permissionManager.hasNotificationPermission() } returns true

        val hasPermission = useCase()

        hasPermission shouldBeEqualTo true
    }
}
