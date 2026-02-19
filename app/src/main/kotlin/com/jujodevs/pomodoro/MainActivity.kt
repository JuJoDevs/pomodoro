package com.jujodevs.pomodoro

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.jujodevs.pomodoro.core.designsystem.components.icons.PomodoroIcons
import com.jujodevs.pomodoro.core.designsystem.theme.PomodoroTheme
import com.jujodevs.pomodoro.core.navigation.MainNavKey
import com.jujodevs.pomodoro.core.navigation.goBack
import com.jujodevs.pomodoro.core.navigation.navigateTo
import com.jujodevs.pomodoro.core.resources.R
import com.jujodevs.pomodoro.core.ui.PomodoroScaffold
import com.jujodevs.pomodoro.core.ui.ScaffoldConfig
import com.jujodevs.pomodoro.core.ui.TopBarAction
import com.jujodevs.pomodoro.core.ui.TopBarState
import com.jujodevs.pomodoro.core.ui.permissions.ExactAlarmPermissionEffect
import com.jujodevs.pomodoro.core.ui.permissions.NotificationPermissionEffect
import com.jujodevs.pomodoro.features.settings.presentation.SettingsRoute
import com.jujodevs.pomodoro.features.timer.presentation.TimerRoute
import com.jujodevs.pomodoro.ui.ConfigureSystemBars
import kotlinx.coroutines.launch

private const val DARK_THEME = true

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        setContent {
            ConfigureSystemBars(darkTheme = DARK_THEME)
            PomodoroTheme(darkTheme = DARK_THEME) {
                PomodoroApp()
            }
        }
    }
}

@Composable
private fun PomodoroApp() {
    val backStack = rememberNavBackStack(MainNavKey.Home)
    val snackbarHostState = remember { SnackbarHostState() }
    var phaseTitleResId by remember { mutableIntStateOf(R.string.phase_title_focus) }
    val isOnSettings = backStack.isNotEmpty() && backStack.last() == MainNavKey.Settings
    val topBarTitle = if (isOnSettings) {
        stringResource(R.string.label_settings)
    } else {
        stringResource(phaseTitleResId)
    }
    val topBarActions = if (isOnSettings) {
        emptyList()
    } else {
        listOf(
            TopBarAction(
                icon = PomodoroIcons.Settings,
                contentDescription = stringResource(R.string.label_settings),
                onClick = { backStack.navigateTo(MainNavKey.Settings) }
            ),
            TopBarAction(
                icon = PomodoroIcons.Help,
                contentDescription = "Help",
                onClick = { /* Handle help */ }
            )
        )
    }

    PomodoroScaffold(
        snackbarHostState = snackbarHostState,
        scaffoldConfig = ScaffoldConfig(
            topBar = TopBarState(
                title = topBarTitle,
                showBackButton = backStack.size > 1,
                onBackClick = { backStack.goBack() },
                actions = topBarActions
            )
        )
    ) {
        AppNavigation(
            backStack = backStack,
            snackbarHostState = snackbarHostState,
            onPhaseChanged = { phaseTitleResId = it }
        )
    }
}

@Composable
fun AppNavigation(
    backStack: NavBackStack<NavKey>,
    snackbarHostState: SnackbarHostState,
    onPhaseChanged: (Int) -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    var shouldRequestExactAlarmPermission by remember { mutableStateOf(false) }
    var exactAlarmPermissionGranted by remember { mutableStateOf<Boolean?>(null) }

    NavDisplay(
        backStack = backStack,
        entryProvider = entryProvider {
            // Define navigation entries
            entry<MainNavKey.Home> {
                NotificationPermissionEffect()
                ExactAlarmPermissionEffect(
                    requestOnMissingPermission = shouldRequestExactAlarmPermission
                ) { isGranted ->
                    exactAlarmPermissionGranted = isGranted
                    shouldRequestExactAlarmPermission = false
                }
                TimerRoute(
                    onNavigateToSettings = { backStack.navigateTo(MainNavKey.Settings) },
                    onPhaseChanged = { resId -> onPhaseChanged(resId) },
                    onShowMessage = { message ->
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar(message.asString(context))
                        }
                    },
                    onRequestExactAlarmPermission = {
                        shouldRequestExactAlarmPermission = true
                    },
                    exactAlarmPermissionGranted = exactAlarmPermissionGranted
                )
            }

            entry<MainNavKey.Settings> {
                SettingsRoute()
            }

            entry<MainNavKey.Statistics> {
                StatisticsScreen()
            }
        }
    )
}

@Composable
private fun StatisticsScreen() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(text = "Statistics")
    }
}
