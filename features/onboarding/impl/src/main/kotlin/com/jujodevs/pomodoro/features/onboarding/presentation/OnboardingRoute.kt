package com.jujodevs.pomodoro.features.onboarding.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jujodevs.pomodoro.core.ui.ObserveAsEvents
import com.jujodevs.pomodoro.core.ui.permissions.ExactAlarmPermissionEffect
import com.jujodevs.pomodoro.core.ui.permissions.RequestNotificationPermissionOnTrigger
import org.koin.androidx.compose.koinViewModel

@Composable
fun OnboardingRoute(
    onNavigateToHome: () -> Unit,
    viewModel: OnboardingViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val onNavigateToHomeState by rememberUpdatedState(newValue = onNavigateToHome)
    var requestExactAlarmPermission by remember { mutableStateOf(false) }
    var requestNotificationPermission by remember { mutableStateOf(false) }
    var exactAlarmPermissionResult by remember { mutableStateOf<Boolean?>(null) }
    var notificationPermissionResult by remember { mutableStateOf<Boolean?>(null) }
    var shouldNavigateAfterPermissions by remember { mutableStateOf(false) }

    ExactAlarmPermissionEffect(
        requestOnMissingPermission = requestExactAlarmPermission,
        onPermissionResult = { isGranted ->
            exactAlarmPermissionResult = isGranted
            requestExactAlarmPermission = false
        },
    )

    RequestNotificationPermissionOnTrigger(
        trigger = requestNotificationPermission,
    ) { isGranted ->
        notificationPermissionResult = isGranted
        requestNotificationPermission = false
    }

    LaunchedEffect(
        shouldNavigateAfterPermissions,
        notificationPermissionResult,
        exactAlarmPermissionResult,
    ) {
        val hasNotificationResult = notificationPermissionResult != null
        val hasExactAlarmResult = exactAlarmPermissionResult != null
        if (shouldNavigateAfterPermissions && hasNotificationResult && hasExactAlarmResult) {
            shouldNavigateAfterPermissions = false
            onNavigateToHomeState()
        }
    }

    ObserveAsEvents(viewModel.effects) { effect ->
        when (effect) {
            OnboardingEffect.RequestPermissionsAndNavigateToHome -> {
                notificationPermissionResult = null
                exactAlarmPermissionResult = null
                shouldNavigateAfterPermissions = true
                requestNotificationPermission = true
                requestExactAlarmPermission = true
            }
        }
    }

    OnboardingScreen(
        state = state,
        onAction = viewModel::onAction,
    )
}
