package com.jujodevs.pomodoro.features.timer.presentation.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.jujodevs.pomodoro.core.designsystem.components.button.ButtonVariant
import com.jujodevs.pomodoro.core.designsystem.components.button.PomodoroButton
import com.jujodevs.pomodoro.core.designsystem.components.surface.PomodoroModal
import com.jujodevs.pomodoro.core.designsystem.theme.LocalSpacing
import com.jujodevs.pomodoro.core.designsystem.theme.PomodoroTheme
import com.jujodevs.pomodoro.core.resources.R
import com.jujodevs.pomodoro.features.timer.presentation.TimerAction
import com.jujodevs.pomodoro.features.timer.presentation.TimerState

@Composable
internal fun HandleModals(
    state: TimerState,
    onAction: (TimerAction) -> Unit,
) {
    ConfirmationModal(
        visible = state.showStopConfirmation,
        title = stringResource(R.string.dialog_stop_title),
        message = stringResource(R.string.dialog_stop_message),
        confirmText = stringResource(R.string.action_stop),
        onConfirm = { onAction(TimerAction.ConfirmStop) },
        onDismiss = { onAction(TimerAction.DismissDialog) },
    )

    ConfirmationModal(
        visible = state.showResetConfirmation,
        title = stringResource(R.string.dialog_reset_title),
        message = stringResource(R.string.dialog_reset_message),
        confirmText = stringResource(R.string.action_ok),
        onConfirm = { onAction(TimerAction.ConfirmReset) },
        onDismiss = { onAction(TimerAction.DismissDialog) },
    )
}

@Composable
private fun ConfirmationModal(
    visible: Boolean,
    title: String,
    message: String,
    confirmText: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    val interactionSource = remember { MutableInteractionSource() }

    Box(modifier = Modifier.fillMaxSize()) {
        AnimatedVisibility(
            visible = visible,
            enter = fadeIn(animationSpec = tween(MODAL_ANIMATION_DURATION_MS)),
            exit = fadeOut(animationSpec = tween(MODAL_ANIMATION_DURATION_MS)),
        ) {
            Box(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.scrim.copy(alpha = MODAL_SCRIM_ALPHA))
                        .clickable(
                            interactionSource = interactionSource,
                            indication = null,
                            onClick = onDismiss,
                        ),
            )
        }

        AnimatedVisibility(
            visible = visible,
            modifier = Modifier.align(Alignment.BottomCenter),
            enter =
                slideInVertically(
                    animationSpec = tween(MODAL_ANIMATION_DURATION_MS),
                    initialOffsetY = { fullHeight -> fullHeight },
                ) + fadeIn(animationSpec = tween(MODAL_ANIMATION_DURATION_MS)),
            exit =
                slideOutVertically(
                    animationSpec = tween(MODAL_ANIMATION_DURATION_MS),
                    targetOffsetY = { fullHeight -> fullHeight },
                ) + fadeOut(animationSpec = tween(MODAL_ANIMATION_DURATION_MS)),
        ) {
            ConfirmationModalContent(
                title = title,
                message = message,
                confirmText = confirmText,
                onConfirm = onConfirm,
                onDismiss = onDismiss,
            )
        }
    }
}

@Composable
private fun ConfirmationModalContent(
    title: String,
    message: String,
    confirmText: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    val spacing = LocalSpacing.current

    PomodoroModal {
        Text(
            text = title,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
        )
        Spacer(modifier = Modifier.height(spacing.spaceM))
        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge,
        )
        Spacer(modifier = Modifier.height(spacing.spaceXL))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End,
        ) {
            PomodoroButton(
                text = stringResource(R.string.action_cancel),
                onClick = onDismiss,
                variant = ButtonVariant.Text,
            )
            Spacer(modifier = Modifier.width(spacing.spaceXS))
            PomodoroButton(
                text = confirmText,
                onClick = onConfirm,
            )
        }
    }
}

private const val MODAL_SCRIM_ALPHA = 0.5f
private const val MODAL_ANIMATION_DURATION_MS = 250

@Preview(showBackground = true, backgroundColor = 0xFF1C2834)
@Composable
private fun HandleModalsStopPreview() {
    PomodoroTheme(darkTheme = true) {
        HandleModals(
            state = TimerState(showStopConfirmation = true),
            onAction = {},
        )
    }
}
