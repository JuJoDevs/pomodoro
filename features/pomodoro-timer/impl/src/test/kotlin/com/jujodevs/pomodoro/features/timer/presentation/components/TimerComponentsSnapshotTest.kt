package com.jujodevs.pomodoro.features.timer.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.unit.dp
import com.github.takahirom.roborazzi.captureRoboImage
import com.jujodevs.pomodoro.core.designsystem.theme.PomodoroTheme
import com.jujodevs.pomodoro.features.timer.domain.model.PomodoroPhase
import com.jujodevs.pomodoro.features.timer.domain.model.PomodoroStatus
import com.jujodevs.pomodoro.features.timer.presentation.TimerState
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(qualifiers = "w400dp-h800dp-normal-long-notround-any-420dpi-keyshidden-nonav")
class TimerComponentsSnapshotTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun timerDisplay_running_snapshot() {
        composeTestRule.setContent {
            PomodoroTheme(darkTheme = true) {
                Surface {
                    timerDisplay(
                        remainingTimeText = "12:34",
                        status = PomodoroStatus.RUNNING,
                        phase = PomodoroPhase.WORK,
                    )
                }
            }
        }

        composeTestRule
            .onRoot()
            .captureRoboImage()
    }

    @Test
    fun exactAlarmWarningBanner_snapshot() {
        composeTestRule.setContent {
            PomodoroTheme(darkTheme = true) {
                Surface {
                    Box(modifier = Modifier.padding(16.dp)) {
                        exactAlarmWarningBanner(
                            onDismiss = {},
                            onRequestPermission = {},
                        )
                    }
                }
            }
        }

        composeTestRule
            .onRoot()
            .captureRoboImage()
    }

    @Test
    fun configSection_idle_snapshot() {
        composeTestRule.setContent {
            PomodoroTheme(darkTheme = true) {
                Surface {
                    Box(modifier = Modifier.padding(16.dp)) {
                        configSection(
                            state =
                                TimerState(
                                    status = PomodoroStatus.IDLE,
                                    selectedWorkMinutes = 25,
                                    selectedShortBreakMinutes = 5,
                                ),
                            onAction = {},
                        )
                    }
                }
            }
        }

        composeTestRule
            .onRoot()
            .captureRoboImage()
    }

    @Test
    fun bottomSection_running_snapshot() {
        composeTestRule.setContent {
            PomodoroTheme(darkTheme = true) {
                Surface {
                    bottomSection(
                        state =
                            TimerState(
                                status = PomodoroStatus.RUNNING,
                                completedSessions = 2,
                                totalSessions = 4,
                            ),
                        onAction = {},
                    )
                }
            }
        }

        composeTestRule
            .onRoot()
            .captureRoboImage()
    }

    @Test
    fun modals_stopConfirmation_snapshot() {
        composeTestRule.setContent {
            PomodoroTheme(darkTheme = true) {
                Surface {
                    Box(
                        modifier =
                            Modifier
                                .fillMaxSize()
                                .background(androidx.compose.material3.MaterialTheme.colorScheme.background),
                    ) {
                        handleModals(
                            state = TimerState(showStopConfirmation = true),
                            onAction = {},
                        )
                    }
                }
            }
        }

        composeTestRule
            .onRoot()
            .captureRoboImage()
    }

    @Test
    fun modals_resetConfirmation_snapshot() {
        composeTestRule.setContent {
            PomodoroTheme(darkTheme = true) {
                Surface {
                    Box(
                        modifier =
                            Modifier
                                .fillMaxSize()
                                .background(androidx.compose.material3.MaterialTheme.colorScheme.background),
                    ) {
                        handleModals(
                            state = TimerState(showResetConfirmation = true),
                            onAction = {},
                        )
                    }
                }
            }
        }

        composeTestRule
            .onRoot()
            .captureRoboImage()
    }
}
