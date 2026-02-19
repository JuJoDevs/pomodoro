package com.jujodevs.pomodoro.features.settings.domain.usecase

import com.jujodevs.pomodoro.libs.permissions.PermissionManager
import io.mockk.every
import io.mockk.mockk
import org.amshove.kluent.shouldBeEqualTo
import org.junit.jupiter.api.Test

class GetCanScheduleExactAlarmsUseCaseTest {

    private val permissionManager: PermissionManager = mockk()
    private val useCase = GetCanScheduleExactAlarmsUseCase(permissionManager)

    @Test
    fun `GIVEN exact alarm permission granted WHEN invoking use case THEN return true`() {
        every { permissionManager.canScheduleExactAlarms() } returns true

        val canScheduleExactAlarms = useCase()

        canScheduleExactAlarms shouldBeEqualTo true
    }
}
