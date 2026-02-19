package com.jujodevs.pomodoro.core.designsystem.snapshot

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.unit.dp
import com.github.takahirom.roborazzi.captureRoboImage
import com.jujodevs.pomodoro.core.designsystem.components.input.PomodoroChip
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
class ChipSnapshotTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun pomodoroChip_selected_snapshot() {
        composeTestRule.setContent {
            PomodoroTheme {
                Box(
                    modifier =
                        Modifier
                            .background(MaterialTheme.colorScheme.background)
                            .padding(16.dp),
                ) {
                    PomodoroChip(
                        text = "25",
                        selected = true,
                        onClick = {},
                    )
                }
            }
        }
        composeTestRule.onRoot().captureRoboImage()
    }

    @Test
    fun pomodoroChip_unselected_snapshot() {
        composeTestRule.setContent {
            PomodoroTheme {
                Box(
                    modifier =
                        Modifier
                            .background(MaterialTheme.colorScheme.background)
                            .padding(16.dp),
                ) {
                    PomodoroChip(
                        text = "15",
                        selected = false,
                        onClick = {},
                    )
                }
            }
        }
        composeTestRule.onRoot().captureRoboImage()
    }

    @Test
    fun pomodoroChip_group_snapshot() {
        composeTestRule.setContent {
            PomodoroTheme {
                Box(
                    modifier =
                        Modifier
                            .background(MaterialTheme.colorScheme.background)
                            .padding(16.dp),
                ) {
                    Row {
                        PomodoroChip(text = "15", selected = false, onClick = {})
                        Spacer(modifier = Modifier.width(8.dp))
                        PomodoroChip(text = "20", selected = false, onClick = {})
                        Spacer(modifier = Modifier.width(8.dp))
                        PomodoroChip(text = "25", selected = true, onClick = {})
                        Spacer(modifier = Modifier.width(8.dp))
                        PomodoroChip(text = "30", selected = false, onClick = {})
                    }
                }
            }
        }
        composeTestRule.onRoot().captureRoboImage()
    }
}
