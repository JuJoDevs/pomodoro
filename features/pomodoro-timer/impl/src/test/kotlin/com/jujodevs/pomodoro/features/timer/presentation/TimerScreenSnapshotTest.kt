package com.jujodevs.pomodoro.features.timer.presentation

import androidx.compose.material3.Surface
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import com.github.takahirom.roborazzi.captureRoboImage
import com.jujodevs.pomodoro.core.designsystem.theme.PomodoroTheme
import com.jujodevs.pomodoro.features.timer.domain.model.PomodoroPhase
import com.jujodevs.pomodoro.features.timer.domain.model.PomodoroStatus
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(qualifiers = "w400dp-h800dp-normal-long-notround-any-420dpi-keyshidden-nonav")
class TimerScreenSnapshotTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun timerScreen_idle_snapshot() {
        renderAndCapture(
            state = TimerState(
                phase = PomodoroPhase.WORK,
                status = PomodoroStatus.IDLE,
                remainingTimeText = "25:00",
                completedSessions = 0
            )
        )
    }

    @Test
    fun timerScreen_runningWork_snapshot() {
        renderAndCapture(
            state = TimerState(
                phase = PomodoroPhase.WORK,
                status = PomodoroStatus.RUNNING,
                remainingTimeText = "12:34",
                progress = 0.5f,
                completedSessions = 2
            )
        )
    }

    @Test
    fun timerScreen_pausedWithWarning_snapshot() {
        renderAndCapture(
            state = TimerState(
                phase = PomodoroPhase.SHORT_BREAK,
                status = PomodoroStatus.PAUSED,
                remainingTimeText = "03:21",
                completedSessions = 1,
                isExactAlarmPermissionMissing = true
            )
        )
    }

    @Test
    fun timerScreen_stopConfirmation_snapshot() {
        renderAndCapture(
            state = TimerState(
                phase = PomodoroPhase.WORK,
                status = PomodoroStatus.RUNNING,
                remainingTimeText = "09:59",
                completedSessions = 3,
                showStopConfirmation = true
            )
        )
    }

    private fun renderAndCapture(state: TimerState) {
        composeTestRule.setContent {
            PomodoroTheme(darkTheme = true) {
                Surface {
                    TimerScreen(
                        state = state,
                        onAction = {}
                    )
                }
            }
        }

        composeTestRule.onRoot().captureRoboImage()
    }
}
