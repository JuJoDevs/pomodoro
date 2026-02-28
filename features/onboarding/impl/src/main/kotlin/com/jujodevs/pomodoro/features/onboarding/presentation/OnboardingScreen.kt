package com.jujodevs.pomodoro.features.onboarding.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.jujodevs.pomodoro.core.designsystem.components.button.PomodoroButton
import com.jujodevs.pomodoro.core.designsystem.components.icons.PomodoroIcons
import com.jujodevs.pomodoro.core.designsystem.theme.LocalSpacing
import com.jujodevs.pomodoro.core.designsystem.theme.PomodoroTheme
import com.jujodevs.pomodoro.core.resources.R
import com.jujodevs.pomodoro.features.onboarding.presentation.components.OnboardingFinalSlideContent
import com.jujodevs.pomodoro.features.onboarding.presentation.components.OnboardingPagerIndicator
import com.jujodevs.pomodoro.features.onboarding.presentation.components.OnboardingSlideContent
import com.jujodevs.pomodoro.features.onboarding.presentation.model.OnboardingSlide
import kotlinx.coroutines.flow.distinctUntilChanged

private val ONBOARDING_SLIDES =
    listOf(
        OnboardingSlide(
            titleResId = R.string.onboarding_slide1_title,
            bodyResId = R.string.onboarding_slide1_body,
            icon = PomodoroIcons.Timer,
        ),
        OnboardingSlide(
            titleResId = R.string.onboarding_slide2_title,
            bodyResId = R.string.onboarding_slide2_body,
            icon = PomodoroIcons.Stats,
        ),
        OnboardingSlide(
            titleResId = R.string.onboarding_slide3_title,
            bodyResId = R.string.onboarding_slide3_body,
            icon = PomodoroIcons.Check,
        ),
    )

@Composable
@Suppress("LongMethod")
fun OnboardingScreen(
    state: OnboardingState,
    onAction: (OnboardingAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    val spacing = LocalSpacing.current
    val onActionState by rememberUpdatedState(newValue = onAction)

    if (state.isLoading) {
        Column(
            modifier = modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            CircularProgressIndicator()
        }
        return
    }

    val pagerState =
        rememberPagerState(
            initialPage = state.currentSlideIndex,
            pageCount = { ONBOARDING_SLIDES.size },
        )
    val isLastSlide = state.currentSlideIndex == ONBOARDING_SLIDES.lastIndex

    LaunchedEffect(state.currentSlideIndex) {
        if (pagerState.currentPage != state.currentSlideIndex) {
            pagerState.scrollToPage(state.currentSlideIndex)
        }
    }

    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.currentPage }
            .distinctUntilChanged()
            .collect { page ->
                onActionState(OnboardingAction.UpdateCurrentSlide(page))
            }
    }

    Column(
        modifier =
            modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(horizontal = spacing.spaceXL),
    ) {
        HorizontalPager(
            state = pagerState,
            modifier =
                Modifier
                    .fillMaxWidth()
                    .weight(1f),
        ) { page ->
            val slide = ONBOARDING_SLIDES[page]
            val isFinalSlide = page == ONBOARDING_SLIDES.lastIndex

            Column(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState()),
            ) {
                Spacer(modifier = Modifier.height(spacing.spaceXXXL))

                OnboardingSlideContent(
                    slide = slide,
                    modifier = Modifier.fillMaxWidth(),
                )

                if (isFinalSlide) {
                    Spacer(modifier = Modifier.height(spacing.spaceXL))
                    OnboardingFinalSlideContent(
                        analyticsConsentChecked = state.analyticsConsentChecked,
                        onAction = onAction,
                    )
                }

                Spacer(modifier = Modifier.height(spacing.spaceXL))
            }
        }

        OnboardingPagerIndicator(
            totalPages = ONBOARDING_SLIDES.size,
            currentPage = state.currentSlideIndex,
            modifier = Modifier.align(Alignment.CenterHorizontally),
        )

        Spacer(modifier = Modifier.height(spacing.spaceM))

        PomodoroButton(
            text =
                if (isLastSlide) {
                    stringResource(R.string.onboarding_action_get_started)
                } else {
                    stringResource(R.string.onboarding_action_next)
                },
            onClick =
                if (isLastSlide) {
                    { onAction(OnboardingAction.CompleteOnboarding) }
                } else {
                    { onAction(OnboardingAction.NextSlide) }
                },
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(modifier = Modifier.height(spacing.spaceXL))
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF1C2834)
@Composable
private fun OnboardingScreenSlide1Preview() {
    PomodoroTheme(darkTheme = true) {
        OnboardingScreen(
            state = OnboardingState(currentSlideIndex = 0, isLoading = false),
            onAction = {},
            modifier = Modifier.padding(16.dp),
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF1C2834)
@Composable
private fun OnboardingScreenSlide2Preview() {
    PomodoroTheme(darkTheme = true) {
        OnboardingScreen(
            state = OnboardingState(currentSlideIndex = 1, isLoading = false),
            onAction = {},
            modifier = Modifier.padding(16.dp),
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF1C2834)
@Composable
private fun OnboardingScreenSlide3UncheckedPreview() {
    PomodoroTheme(darkTheme = true) {
        OnboardingScreen(
            state = OnboardingState(currentSlideIndex = 2, analyticsConsentChecked = false, isLoading = false),
            onAction = {},
            modifier = Modifier.padding(16.dp),
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF1C2834)
@Composable
private fun OnboardingScreenSlide3CheckedPreview() {
    PomodoroTheme(darkTheme = true) {
        OnboardingScreen(
            state = OnboardingState(currentSlideIndex = 2, analyticsConsentChecked = true, isLoading = false),
            onAction = {},
            modifier = Modifier.padding(16.dp),
        )
    }
}
