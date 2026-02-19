package com.jujodevs.pomodoro.core.designsystem.components.input

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import com.jujodevs.pomodoro.core.designsystem.theme.LocalSpacing
import com.jujodevs.pomodoro.core.designsystem.theme.PomodoroTheme

/**
 * Pomodoro Dropdown Component
 *
 * Dropdown menu with custom styling
 *
 * @param selectedText Currently selected text
 * @param options List of available options
 * @param onOptionSelect Callback when option is selected
 * @param modifier Modifier for styling
 */
@Composable
fun PomodoroDropdown(
    selectedText: String,
    options: List<String>,
    onOptionSelect: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val spacing = LocalSpacing.current
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = modifier) {
        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .clip(MaterialTheme.shapes.medium)
                    .background(MaterialTheme.colorScheme.surface)
                    .clickable { expanded = true }
                    .padding(horizontal = spacing.spaceM, vertical = spacing.spaceS),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = selectedText,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.weight(1f),
            )
            Spacer(modifier = Modifier.width(spacing.spaceXS))
            Icon(
                imageVector = Icons.Default.KeyboardArrowDown,
                contentDescription = "Expand",
                tint = MaterialTheme.colorScheme.onSurface,
            )
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier =
                Modifier
                    .background(MaterialTheme.colorScheme.surface),
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = {
                        Text(
                            text = option,
                            style = MaterialTheme.typography.bodyLarge,
                        )
                    },
                    onClick = {
                        onOptionSelect(option)
                        expanded = false
                    },
                )
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF1C2834)
@Composable
private fun PomodoroDropdownPreview() {
    PomodoroTheme {
        PomodoroDropdown(
            selectedText = "Digital Beep (Default)",
            options =
                listOf(
                    "Digital Beep (Default)",
                    "Classic Bell",
                    "Chime",
                    "Soft Tone",
                ),
            onOptionSelect = {},
            modifier = Modifier.fillMaxWidth(),
        )
    }
}
