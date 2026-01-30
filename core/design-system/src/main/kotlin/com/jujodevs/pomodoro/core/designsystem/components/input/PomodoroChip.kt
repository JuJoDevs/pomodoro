package com.jujodevs.pomodoro.core.designsystem.components.input

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.jujodevs.pomodoro.core.designsystem.theme.PomodoroTheme
import com.jujodevs.pomodoro.core.designsystem.theme.TextSecondary

/**
 * Pomodoro Chip Component
 *
 * Chip for duration selection with selected/unselected states
 *
 * @param text Chip text
 * @param selected Whether chip is selected
 * @param onClick Click handler
 * @param modifier Modifier for styling
 */
@Composable
fun PomodoroChip(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val backgroundColor = if (selected) {
        MaterialTheme.colorScheme.primary
    } else {
        Color.Transparent
    }

    val textColor = if (selected) {
        MaterialTheme.colorScheme.onPrimary
    } else {
        TextSecondary
    }

    Box(
        modifier = modifier
            .width(60.dp)
            .height(48.dp)
            .clip(MaterialTheme.shapes.medium)
            .background(backgroundColor)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.titleMedium,
            color = textColor
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF1C2834)
@Composable
private fun PomodoroChipSelectedPreview() {
    PomodoroTheme {
        PomodoroChip(
            text = "25",
            selected = true,
            onClick = {}
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF1C2834)
@Composable
private fun PomodoroChipUnselectedPreview() {
    PomodoroTheme {
        PomodoroChip(
            text = "15",
            selected = false,
            onClick = {}
        )
    }
}
