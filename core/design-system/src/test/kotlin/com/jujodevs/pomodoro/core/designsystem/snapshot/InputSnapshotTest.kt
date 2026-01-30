package com.jujodevs.pomodoro.core.designsystem.snapshot

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.unit.dp
import com.github.takahirom.roborazzi.captureRoboImage
import com.jujodevs.pomodoro.core.designsystem.components.input.PomodoroSlider
import com.jujodevs.pomodoro.core.designsystem.components.input.PomodoroSwitch
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
class InputSnapshotTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun pomodoroSwitch_checked_snapshot() {
        composeTestRule.setContent {
            PomodoroTheme {
                Box(
                    modifier = Modifier
                        .background(MaterialTheme.colorScheme.background)
                        .padding(16.dp)
                ) {
                    PomodoroSwitch(
                        checked = true,
                        onCheckedChange = {}
                    )
                }
            }
        }
        composeTestRule.onRoot().captureRoboImage()
    }

    @Test
    fun pomodoroSwitch_unchecked_snapshot() {
        composeTestRule.setContent {
            PomodoroTheme {
                Box(
                    modifier = Modifier
                        .background(MaterialTheme.colorScheme.background)
                        .padding(16.dp)
                ) {
                    PomodoroSwitch(
                        checked = false,
                        onCheckedChange = {}
                    )
                }
            }
        }
        composeTestRule.onRoot().captureRoboImage()
    }

    @Test
    fun pomodoroSlider_75percent_snapshot() {
        composeTestRule.setContent {
            PomodoroTheme {
                Box(
                    modifier = Modifier
                        .background(MaterialTheme.colorScheme.background)
                        .padding(16.dp)
                ) {
                    PomodoroSlider(
                        value = 0.75f,
                        onValueChange = {}
                    )
                }
            }
        }
        composeTestRule.onRoot().captureRoboImage()
    }
}
