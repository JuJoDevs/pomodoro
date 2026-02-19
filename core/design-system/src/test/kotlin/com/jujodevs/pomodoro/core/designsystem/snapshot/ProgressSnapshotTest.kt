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
import com.jujodevs.pomodoro.core.designsystem.components.progress.PomodoroProgressBar
import com.jujodevs.pomodoro.core.designsystem.components.progress.PomodoroProgressIndicator
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
class ProgressSnapshotTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun pomodoroProgressBar_75percent_snapshot() {
        composeTestRule.setContent {
            PomodoroTheme {
                Box(
                    modifier =
                        Modifier
                            .background(MaterialTheme.colorScheme.background)
                            .padding(16.dp),
                ) {
                    PomodoroProgressBar(progress = 0.75f)
                }
            }
        }
        composeTestRule.onRoot().captureRoboImage()
    }

    @Test
    fun pomodoroProgressBar_50percent_snapshot() {
        composeTestRule.setContent {
            PomodoroTheme {
                Box(
                    modifier =
                        Modifier
                            .background(MaterialTheme.colorScheme.background)
                            .padding(16.dp),
                ) {
                    PomodoroProgressBar(progress = 0.5f)
                }
            }
        }
        composeTestRule.onRoot().captureRoboImage()
    }

    @Test
    fun pomodoroProgressIndicator_75percent_snapshot() {
        composeTestRule.setContent {
            PomodoroTheme {
                Box(
                    modifier =
                        Modifier
                            .background(MaterialTheme.colorScheme.background)
                            .padding(16.dp),
                ) {
                    PomodoroProgressIndicator(progress = 0.75f)
                }
            }
        }
        composeTestRule.onRoot().captureRoboImage()
    }
}
