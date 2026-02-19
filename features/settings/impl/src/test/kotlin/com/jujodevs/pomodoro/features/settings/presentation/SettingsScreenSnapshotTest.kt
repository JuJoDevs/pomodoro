package com.jujodevs.pomodoro.features.settings.presentation

import androidx.compose.material3.Surface
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import com.github.takahirom.roborazzi.captureRoboImage
import com.jujodevs.pomodoro.core.designsystem.theme.PomodoroTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(qualifiers = "w400dp-h800dp-normal-long-notround-any-420dpi-keyshidden-nonav")
class SettingsScreenSnapshotTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun settingsScreen_default_snapshot() {
        renderAndCapture(
            state =
                SettingsState(
                    alarmSoundLabel = "Digital Beep (Default)",
                    canScheduleExactAlarms = true,
                    hasNotificationPermission = true,
                    isLoading = false,
                ),
            versionText = "Version 1.0.0 (1)",
        )
    }

    @Test
    fun settingsScreen_permissionsMissing_snapshot() {
        renderAndCapture(
            state =
                SettingsState(
                    alarmSoundLabel = "Default",
                    canScheduleExactAlarms = false,
                    hasNotificationPermission = false,
                    isLoading = false,
                ),
            versionText = "Version 1.0.0 (1)",
        )
    }

    @Test
    fun settingsScreen_analyticsEnabled_snapshot() {
        renderAndCapture(
            state =
                SettingsState(
                    alarmSoundLabel = "Digital Beep (Default)",
                    analyticsCollectionEnabled = true,
                    canScheduleExactAlarms = true,
                    hasNotificationPermission = true,
                    isLoading = false,
                ),
            versionText = "Version 1.0.0 (1)",
        )
    }

    private fun renderAndCapture(
        state: SettingsState,
        versionText: String,
    ) {
        composeTestRule.setContent {
            PomodoroTheme(darkTheme = true) {
                Surface {
                    settingsScreen(
                        state = state,
                        versionText = versionText,
                        onAction = {},
                    )
                }
            }
        }

        composeTestRule.onRoot().captureRoboImage()
    }
}
