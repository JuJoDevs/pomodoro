package com.jujodevs.pomodoro.features.onboarding.presentation

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
class OnboardingScreenSnapshotTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun onboardingScreen_slide1_snapshot() {
        renderAndCapture(
            state = OnboardingState(currentSlideIndex = 0, isLoading = false),
        )
    }

    @Test
    fun onboardingScreen_slide2_snapshot() {
        renderAndCapture(
            state = OnboardingState(currentSlideIndex = 1, isLoading = false),
        )
    }

    @Test
    fun onboardingScreen_slide3_consentUnchecked_snapshot() {
        renderAndCapture(
            state =
                OnboardingState(
                    currentSlideIndex = 2,
                    analyticsConsentChecked = false,
                    isLoading = false,
                ),
        )
    }

    @Test
    fun onboardingScreen_slide3_consentChecked_snapshot() {
        renderAndCapture(
            state =
                OnboardingState(
                    currentSlideIndex = 2,
                    analyticsConsentChecked = true,
                    isLoading = false,
                ),
        )
    }

    private fun renderAndCapture(state: OnboardingState) {
        composeTestRule.setContent {
            PomodoroTheme(darkTheme = true) {
                Surface {
                    OnboardingScreen(
                        state = state,
                        onAction = {},
                    )
                }
            }
        }

        composeTestRule.onRoot().captureRoboImage()
    }
}
