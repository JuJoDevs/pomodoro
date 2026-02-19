package com.jujodevs.pomodoro.features.settings.domain.usecase

import com.jujodevs.pomodoro.libs.notifications.AlarmSoundLabelProvider

class GetCompletionAlarmSoundLabelUseCase(
    private val alarmSoundLabelProvider: AlarmSoundLabelProvider,
) {
    operator fun invoke(): String =
        runCatching {
            alarmSoundLabelProvider.getCompletionChannelSoundLabel()
        }.getOrDefault("Unknown")
}
