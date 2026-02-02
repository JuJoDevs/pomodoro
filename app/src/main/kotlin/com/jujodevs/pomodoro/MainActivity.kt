package com.jujodevs.pomodoro

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.jujodevs.pomodoro.core.designsystem.theme.PomodoroTheme
import com.jujodevs.pomodoro.core.navigation.MainNavKey
import com.jujodevs.pomodoro.core.navigation.goBack
import com.jujodevs.pomodoro.core.ui.PomodoroScaffold
import com.jujodevs.pomodoro.core.ui.ScaffoldConfig
import com.jujodevs.pomodoro.core.ui.TopBarState
import com.jujodevs.pomodoro.core.ui.permissions.ExactAlarmPermissionEffect
import com.jujodevs.pomodoro.core.ui.permissions.NotificationPermissionEffect

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PomodoroTheme {
                PomodoroApp()
            }
        }
    }
}

@Composable
private fun PomodoroApp() {
    // Create and remember the navigation back stack
    val backStack = rememberNavBackStack(MainNavKey.Home)

    PomodoroScaffold(
        scaffoldConfig = ScaffoldConfig(
            topBar = TopBarState(
                title = "Pomodoro",
                showBackButton = backStack.size > 1,
                onBackClick = { backStack.goBack() }
            )
        )
    ) {
        NavDisplay(
            backStack = backStack,
            entryProvider = entryProvider {
                // Define navigation entries
                entry<MainNavKey.Home> {
                    HomeScreen()
                }

                entry<MainNavKey.Settings> {
                    SettingsScreen()
                }

                entry<MainNavKey.Statistics> {
                    StatisticsScreen()
                }
            }
        )
    }
}

@Composable
private fun HomeScreen() {
    // Request permissions on app launch/home screen for now
    NotificationPermissionEffect()
    ExactAlarmPermissionEffect()

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(text = "Welcome to Pomodoro!")
    }
}

@Composable
private fun SettingsScreen() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(text = "Settings")
    }
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

@Preview(showBackground = true)
@Composable
private fun HomeScreenPreview() {
    PomodoroTheme {
        HomeScreen()
    }
}
