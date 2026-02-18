package com.jujodevs.pomodoro.features.settings.domain.usecase

import com.jujodevs.pomodoro.libs.notifications.AlarmSoundLabelProvider
import io.mockk.every
import io.mockk.mockk
import org.amshove.kluent.shouldBeEqualTo
import org.junit.jupiter.api.Test

class GetCompletionAlarmSoundLabelUseCaseTest {

    private val alarmSoundLabelProvider: AlarmSoundLabelProvider = mockk()
    private val useCase = GetCompletionAlarmSoundLabelUseCase(alarmSoundLabelProvider)

    @Test
    fun `GIVEN provider returns a label WHEN invoking use case THEN return provider label`() {
        every { alarmSoundLabelProvider.getCompletionChannelSoundLabel() } returns "Digital Beep (Default)"

        val label = useCase()

        label shouldBeEqualTo "Digital Beep (Default)"
    }

    @Test
    fun `GIVEN provider throws exception WHEN invoking use case THEN return unknown`() {
        every { alarmSoundLabelProvider.getCompletionChannelSoundLabel() } throws IllegalStateException("boom")

        val label = useCase()

        label shouldBeEqualTo "Unknown"
    }
}
