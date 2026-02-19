package com.jujodevs.pomodoro.core.designsystem.components.navigation

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import com.jujodevs.pomodoro.core.designsystem.components.button.PomodoroIconButton
import com.jujodevs.pomodoro.core.designsystem.theme.PomodoroTheme

/**
 * Pomodoro Top Bar Component
 *
 * Top app bar with title and optional navigation/action buttons
 *
 * @param title Title text
 * @param modifier Modifier for styling
 * @param navigationIcon Optional leading navigation icon
 * @param onNavigationClick Navigation icon click handler
 * @param actions Optional trailing actions
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PomodoroTopBar(
    title: String,
    modifier: Modifier = Modifier,
    navigationIcon: ImageVector? = null,
    onNavigationClick: () -> Unit = {},
    actions: @Composable RowScope.() -> Unit = {},
) {
    CenterAlignedTopAppBar(
        title = {
            Text(
                text = title,
                style = MaterialTheme.typography.headlineMedium,
            )
        },
        modifier = modifier,
        navigationIcon = {
            if (navigationIcon != null) {
                PomodoroIconButton(
                    icon = navigationIcon,
                    onClick = onNavigationClick,
                    backgroundColor = null,
                )
            }
        },
        actions = actions,
        colors =
            TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.background,
                scrolledContainerColor = MaterialTheme.colorScheme.background,
                navigationIconContentColor = MaterialTheme.colorScheme.onBackground,
                titleContentColor = MaterialTheme.colorScheme.onBackground,
                actionIconContentColor = MaterialTheme.colorScheme.onBackground,
            ),
    )
}

@Preview(showBackground = true, backgroundColor = 0xFF1C2834)
@Composable
private fun PomodoroTopBarPreview() {
    PomodoroTheme {
        PomodoroTopBar(
            title = "Focus",
            navigationIcon = Icons.Default.Settings,
            onNavigationClick = {},
            actions = {
                IconButton(onClick = {}) {
                    androidx.compose.material3.Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "More",
                    )
                }
            },
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF1C2834)
@Composable
private fun PomodoroTopBarWithBackPreview() {
    PomodoroTheme {
        PomodoroTopBar(
            title = "Statistics",
            navigationIcon = Icons.AutoMirrored.Filled.ArrowBack,
            onNavigationClick = {},
        )
    }
}
