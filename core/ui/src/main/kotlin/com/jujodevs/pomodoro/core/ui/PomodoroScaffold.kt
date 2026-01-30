package com.jujodevs.pomodoro.core.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.jujodevs.pomodoro.core.designsystem.components.navigation.PomodoroBottomNavigation
import com.jujodevs.pomodoro.core.designsystem.components.navigation.PomodoroNavigationItem
import com.jujodevs.pomodoro.core.designsystem.components.navigation.PomodoroTopBar

/**
 * Centralized scaffold for the entire app.
 *
 * This scaffold manages the top bar and bottom navigation bar dynamically based on
 * the [ScaffoldConfig]. The scaffold is decoupled from navigation implementation,
 * using only callbacks provided through the configuration.
 *
 * @param scaffoldConfig Configuration for top bar and bottom bar
 * @param modifier Modifier for styling
 * @param content Content to display inside the scaffold
 */
@Composable
fun PomodoroScaffold(
    modifier: Modifier = Modifier,
    scaffoldConfig: ScaffoldConfig = ScaffoldConfig(),
    content: @Composable () -> Unit
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            scaffoldConfig.topBar?.let { topBarState ->
                PomodoroTopBar(
                    title = topBarState.title,
                    navigationIcon = if (topBarState.showBackButton) {
                        Icons.AutoMirrored.Filled.ArrowBack
                    } else {
                        null
                    },
                    onNavigationClick = topBarState.onBackClick ?: {},
                    actions = {
                        topBarState.actions.forEach { action ->
                            IconButton(onClick = action.onClick) {
                                Icon(
                                    imageVector = action.icon,
                                    contentDescription = action.contentDescription
                                )
                            }
                        }
                    }
                )
            }
        },
        bottomBar = {
            scaffoldConfig.bottomBar?.let { bottomBarState ->
                PomodoroBottomNavigation(
                    items = bottomBarState.items.map { item ->
                        PomodoroNavigationItem(
                            label = item.label,
                            icon = item.icon,
                            selected = item.selected,
                            onClick = item.onClick
                        )
                    }
                )
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            content()
        }
    }
}
