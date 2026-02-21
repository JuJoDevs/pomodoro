package com.jujodevs.pomodoro.features.statistics.presentation.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.unit.dp
import com.github.takahirom.roborazzi.captureRoboImage
import com.jujodevs.pomodoro.core.designsystem.theme.PomodoroTheme
import com.jujodevs.pomodoro.features.statistics.domain.usecase.ActivityChartEntry
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(qualifiers = "w400dp-h800dp-normal-long-notround-any-420dpi-keyshidden-nonav")
class StatisticsComponentsSnapshotTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun statisticsKpiCard_snapshot() {
        composeTestRule.setContent {
            PomodoroTheme(darkTheme = true) {
                Surface {
                    Box(Modifier.padding(16.dp)) {
                        StatisticsKpiCard(
                            value = "12",
                            label = "SESSIONS",
                            icon = com.jujodevs.pomodoro.core.designsystem.components.icons.PomodoroIcons.Check,
                        )
                    }
                }
            }
        }
        composeTestRule.onRoot().captureRoboImage()
    }

    @Test
    fun statisticsWeeklyActivityCard_snapshot() {
        composeTestRule.setContent {
            PomodoroTheme(darkTheme = true) {
                Surface {
                    Box(Modifier.padding(16.dp)) {
                        StatisticsActivityCard(
                            title = "Weekly Activity",
                            subtitle = "Current Week",
                            activity =
                                listOf(
                                    ActivityChartEntry("M", 45 * 60 * 1000L),
                                    ActivityChartEntry("T", 30 * 60 * 1000L),
                                    ActivityChartEntry("W", 60 * 60 * 1000L),
                                    ActivityChartEntry("T", 0L),
                                    ActivityChartEntry("F", 90 * 60 * 1000L),
                                    ActivityChartEntry("S", 20 * 60 * 1000L),
                                    ActivityChartEntry("S", 15 * 60 * 1000L),
                                ),
                        )
                    }
                }
            }
        }
        composeTestRule.onRoot().captureRoboImage()
    }

    @Test
    fun statisticsShareButton_snapshot() {
        composeTestRule.setContent {
            PomodoroTheme(darkTheme = true) {
                Surface {
                    Box(Modifier.padding(16.dp)) {
                        StatisticsShareButton(
                            onClick = {},
                            enabled = true,
                        )
                    }
                }
            }
        }
        composeTestRule.onRoot().captureRoboImage()
    }
}
