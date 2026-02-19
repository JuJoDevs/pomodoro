package com.jujodevs.pomodoro.core.designsystem.snapshot

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.unit.dp
import com.github.takahirom.roborazzi.captureRoboImage
import com.jujodevs.pomodoro.core.designsystem.components.button.ButtonVariant
import com.jujodevs.pomodoro.core.designsystem.components.button.PomodoroButton
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
class ButtonSnapshotTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun pomodoroButton_primary_snapshot() {
        composeTestRule.setContent {
            PomodoroTheme {
                Box(
                    modifier =
                        Modifier
                            .background(MaterialTheme.colorScheme.background)
                            .padding(16.dp),
                ) {
                    PomodoroButton(
                        text = "START",
                        onClick = {},
                        variant = ButtonVariant.Primary,
                    )
                }
            }
        }
        composeTestRule.onRoot().captureRoboImage()
    }

    @Test
    fun pomodoroButton_primary_withIcon_snapshot() {
        composeTestRule.setContent {
            PomodoroTheme {
                Box(
                    modifier =
                        Modifier
                            .background(MaterialTheme.colorScheme.background)
                            .padding(16.dp),
                ) {
                    PomodoroButton(
                        text = "START",
                        onClick = {},
                        variant = ButtonVariant.Primary,
                        icon = Icons.Default.PlayArrow,
                    )
                }
            }
        }
        composeTestRule.onRoot().captureRoboImage()
    }

    @Test
    fun pomodoroButton_secondary_snapshot() {
        composeTestRule.setContent {
            PomodoroTheme {
                Box(
                    modifier =
                        Modifier
                            .background(MaterialTheme.colorScheme.background)
                            .padding(16.dp),
                ) {
                    PomodoroButton(
                        text = "Reset",
                        onClick = {},
                        variant = ButtonVariant.Secondary,
                    )
                }
            }
        }
        composeTestRule.onRoot().captureRoboImage()
    }

    @Test
    fun pomodoroButton_text_snapshot() {
        composeTestRule.setContent {
            PomodoroTheme {
                Box(
                    modifier =
                        Modifier
                            .background(MaterialTheme.colorScheme.background)
                            .padding(16.dp),
                ) {
                    PomodoroButton(
                        text = "Skip",
                        onClick = {},
                        variant = ButtonVariant.Text,
                    )
                }
            }
        }
        composeTestRule.onRoot().captureRoboImage()
    }

    @Test
    fun pomodoroButton_disabled_snapshot() {
        composeTestRule.setContent {
            PomodoroTheme {
                Box(
                    modifier =
                        Modifier
                            .background(MaterialTheme.colorScheme.background)
                            .padding(16.dp),
                ) {
                    PomodoroButton(
                        text = "START",
                        onClick = {},
                        variant = ButtonVariant.Primary,
                        enabled = false,
                    )
                }
            }
        }
        composeTestRule.onRoot().captureRoboImage()
    }
}
