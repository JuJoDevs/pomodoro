package com.jujodevs.pomodoro.features.statistics.presentation

import androidx.compose.material3.Surface
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
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
class StatisticsScreenSnapshotTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun statisticsScreen_loading_snapshot() {
        composeTestRule.setContent {
            PomodoroTheme(darkTheme = true) {
                Surface {
                    StatisticsScreen(
                        state = StatisticsState(isLoading = true),
                        onAction = {},
                    )
                }
            }
        }
        composeTestRule.onRoot().captureRoboImage()
    }

    @Test
    fun statisticsScreen_success_snapshot() {
        composeTestRule.setContent {
            PomodoroTheme(darkTheme = true) {
                Surface {
                    StatisticsScreen(
                        state =
                            StatisticsState(
                                isLoading = false,
                                error = null,
                                totalFocusTimeFormatted = "04:30",
                                sessionsCompleted = 12,
                                dayStreak = 5,
                                weeklyActivity =
                                    listOf(
                                        ActivityChartEntry("M", 45 * 60 * 1000L),
                                        ActivityChartEntry("T", 30 * 60 * 1000L),
                                        ActivityChartEntry("W", 60 * 60 * 1000L),
                                        ActivityChartEntry("T", 0L),
                                        ActivityChartEntry("F", 90 * 60 * 1000L),
                                        ActivityChartEntry("S", 20 * 60 * 1000L),
                                        ActivityChartEntry("S", 15 * 60 * 1000L),
                                    ),
                                monthlyActivity =
                                    listOf(
                                        ActivityChartEntry("Jan", 0L),
                                        ActivityChartEntry("Feb", 0L),
                                        ActivityChartEntry("Mar", 0L),
                                        ActivityChartEntry("Apr", 0L),
                                        ActivityChartEntry("May", 0L),
                                        ActivityChartEntry("Jun", 0L),
                                        ActivityChartEntry("Jul", 0L),
                                        ActivityChartEntry("Aug", 0L),
                                        ActivityChartEntry("Sep", 0L),
                                        ActivityChartEntry("Oct", 0L),
                                        ActivityChartEntry("Nov", 0L),
                                        ActivityChartEntry("Dec", 0L),
                                    ),
                                canShare = true,
                            ),
                        onAction = {},
                    )
                }
            }
        }
        composeTestRule.onRoot().captureRoboImage()
    }

    @Test
    fun statisticsScreen_empty_snapshot() {
        composeTestRule.setContent {
            PomodoroTheme(darkTheme = true) {
                Surface {
                    StatisticsScreen(
                        state =
                            StatisticsState(
                                isLoading = false,
                                error = null,
                                totalFocusTimeFormatted = "00:00",
                                sessionsCompleted = 0,
                                dayStreak = 0,
                                weeklyActivity = emptyList(),
                                monthlyActivity = emptyList(),
                                canShare = false,
                            ),
                        onAction = {},
                    )
                }
            }
        }
        composeTestRule.onRoot().captureRoboImage()
    }

    @Test
    fun statisticsScreen_error_snapshot() {
        composeTestRule.setContent {
            PomodoroTheme(darkTheme = true) {
                Surface {
                    StatisticsScreen(
                        state =
                            StatisticsState(
                                isLoading = false,
                                error = StatisticsError.GENERIC,
                            ),
                        onAction = {},
                    )
                }
            }
        }
        composeTestRule.onRoot().captureRoboImage()
    }
}
