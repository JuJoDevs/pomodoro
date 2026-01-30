package com.jujodevs.pomodoro.core.designsystem.snapshot

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.unit.dp
import com.github.takahirom.roborazzi.captureRoboImage
import com.jujodevs.pomodoro.core.designsystem.components.card.PomodoroCard
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
class CardSnapshotTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun pomodoroCard_withContent_snapshot() {
        composeTestRule.setContent {
            PomodoroTheme {
                Box(
                    modifier = Modifier
                        .background(MaterialTheme.colorScheme.background)
                        .padding(16.dp)
                ) {
                    PomodoroCard {
                        Text(
                            text = "Work Duration",
                            style = MaterialTheme.typography.titleLarge
                        )
                        Text(
                            text = "25 minutes",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
        composeTestRule.onRoot().captureRoboImage()
    }
}
