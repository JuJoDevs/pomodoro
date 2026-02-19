package com.jujodevs.pomodoro.core.designsystem.components.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import com.jujodevs.pomodoro.core.designsystem.theme.PomodoroTheme
import com.jujodevs.pomodoro.core.designsystem.theme.TextSecondary

/**
 * Pomodoro Bottom Navigation Component
 *
 * Bottom navigation bar with custom styling
 *
 * @param items List of navigation items
 * @param modifier Modifier for styling
 */
@Composable
fun PomodoroBottomNavigation(
    items: List<PomodoroNavigationItem>,
    modifier: Modifier = Modifier,
) {
    NavigationBar(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.background,
        contentColor = MaterialTheme.colorScheme.onBackground,
    ) {
        items.forEach { item ->
            NavigationBarItem(
                selected = item.selected,
                onClick = item.onClick,
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.label,
                    )
                },
                label = {
                    Text(
                        text = item.label,
                        style = MaterialTheme.typography.labelSmall,
                    )
                },
                colors =
                    NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.primary,
                        selectedTextColor = MaterialTheme.colorScheme.primary,
                        unselectedIconColor = TextSecondary,
                        unselectedTextColor = TextSecondary,
                        indicatorColor = MaterialTheme.colorScheme.background,
                    ),
            )
        }
    }
}

/**
 * Pomodoro Bottom Navigation Item
 *
 * Data class representing a navigation item
 */
data class PomodoroNavigationItem(
    val label: String,
    val icon: ImageVector,
    val selected: Boolean,
    val onClick: () -> Unit,
)

@Preview(showBackground = true, backgroundColor = 0xFF1C2834)
@Composable
private fun PomodoroBottomNavigationPreview() {
    PomodoroTheme {
        PomodoroBottomNavigation(
            items =
                listOf(
                    PomodoroNavigationItem(
                        label = "TIMER",
                        icon = Icons.Default.Timer,
                        selected = true,
                        onClick = {},
                    ),
                    PomodoroNavigationItem(
                        label = "STATS",
                        icon = Icons.Default.BarChart,
                        selected = false,
                        onClick = {},
                    ),
                    PomodoroNavigationItem(
                        label = "HISTORY",
                        icon = Icons.Default.History,
                        selected = false,
                        onClick = {},
                    ),
                ),
        )
    }
}
