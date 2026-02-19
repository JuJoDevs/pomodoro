package com.jujodevs.pomodoro.core.designsystem.components.button

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.jujodevs.pomodoro.core.designsystem.theme.PomodoroTheme
import com.jujodevs.pomodoro.core.designsystem.theme.SurfaceVariantDark

/**
 * Pomodoro Icon Button Component
 *
 * Circular or rounded square icon button
 *
 * @param icon Icon to display
 * @param onClick Click handler
 * @param modifier Modifier for styling
 * @param size Button size (Standard 48dp or Compact 40dp)
 * @param backgroundColor Background color (null for transparent)
 * @param contentDescription Accessibility description
 */
@Composable
fun PomodoroIconButton(
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    size: IconButtonSize = IconButtonSize.Standard,
    backgroundColor: Color? = SurfaceVariantDark,
    contentDescription: String? = null,
) {
    val buttonSize: Dp =
        when (size) {
            IconButtonSize.Standard -> 48.dp
            IconButtonSize.Compact -> 40.dp
        }

    val iconSize: Dp =
        when (size) {
            IconButtonSize.Standard -> 24.dp
            IconButtonSize.Compact -> 20.dp
        }

    Box(
        modifier =
            modifier
                .size(buttonSize)
                .clip(MaterialTheme.shapes.small)
                .then(
                    if (backgroundColor != null) {
                        Modifier.background(backgroundColor)
                    } else {
                        Modifier
                    },
                ).clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = MaterialTheme.colorScheme.onPrimary,
            modifier = Modifier.size(iconSize),
        )
    }
}

enum class IconButtonSize {
    Standard,
    Compact,
}

@Preview(showBackground = true, backgroundColor = 0xFF1C2834)
@Composable
private fun PomodoroIconButtonPreview() {
    PomodoroTheme {
        PomodoroIconButton(
            icon = Icons.Default.Settings,
            onClick = {},
            backgroundColor = SurfaceVariantDark,
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF1C2834)
@Composable
private fun PomodoroIconButtonTransparentPreview() {
    PomodoroTheme {
        PomodoroIconButton(
            icon = Icons.Default.Settings,
            onClick = {},
            backgroundColor = null,
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF1C2834)
@Composable
private fun PomodoroIconButtonCompactPreview() {
    PomodoroTheme {
        PomodoroIconButton(
            icon = Icons.Default.Settings,
            onClick = {},
            size = IconButtonSize.Compact,
        )
    }
}
