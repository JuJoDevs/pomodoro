package com.jujodevs.pomodoro.core.ui

/**
 * Configuration for the centralized scaffold.
 *
 * Screens can update this state via events without knowing the Scaffold implementation
 * or accessing Material directly.
 */
data class ScaffoldConfig(
    val topBar: TopBarState? = null,
    val bottomBar: BottomBarState? = null,
)

/**
 * State for the top app bar.
 */
data class TopBarState(
    val title: String,
    val showBackButton: Boolean = false,
    val onBackClick: (() -> Unit)? = null,
    val actions: List<TopBarAction> = emptyList(),
)

/**
 * Action button in the top app bar.
 */
data class TopBarAction(
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val contentDescription: String,
    val onClick: () -> Unit,
)

/**
 * State for the bottom navigation bar.
 */
data class BottomBarState(
    val items: List<BottomBarItem>,
)

/**
 * Item in the bottom navigation bar.
 */
data class BottomBarItem(
    val label: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val selected: Boolean,
    val onClick: () -> Unit,
)
