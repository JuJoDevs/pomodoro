package com.jujodevs.pomodoro.features.onboarding.presentation.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import com.github.takahirom.roborazzi.captureRoboImage
import com.jujodevs.pomodoro.core.designsystem.components.icons.PomodoroIcons
import com.jujodevs.pomodoro.core.designsystem.theme.PomodoroTheme
import com.jujodevs.pomodoro.core.resources.R
import com.jujodevs.pomodoro.features.onboarding.presentation.model.OnboardingSlide
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(qualifiers = "w400dp-h800dp-normal-long-notround-any-420dpi-keyshidden-nonav")
class OnboardingComponentsSnapshotTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun onboardingSlideContent_snapshot() {
        composeTestRule.setContent {
            PomodoroTheme(darkTheme = true) {
                Surface {
                    OnboardingSlideContent(
                        slide =
                            OnboardingSlide(
                                titleResId = R.string.onboarding_slide1_title,
                                bodyResId = R.string.onboarding_slide1_body,
                                icon = PomodoroIcons.Timer,
                            ),
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            }
        }

        composeTestRule.onRoot().captureRoboImage()
    }

    @Test
    fun onboardingConsentSection_unchecked_snapshot() {
        composeTestRule.setContent {
            PomodoroTheme(darkTheme = true) {
                Surface {
                    OnboardingConsentSection(
                        analyticsConsentChecked = false,
                        onConsentChange = {},
                    )
                }
            }
        }

        composeTestRule.onRoot().captureRoboImage()
    }

    @Test
    fun onboardingConsentSection_checked_snapshot() {
        composeTestRule.setContent {
            PomodoroTheme(darkTheme = true) {
                Surface {
                    OnboardingConsentSection(
                        analyticsConsentChecked = true,
                        onConsentChange = {},
                    )
                }
            }
        }

        composeTestRule.onRoot().captureRoboImage()
    }
}
