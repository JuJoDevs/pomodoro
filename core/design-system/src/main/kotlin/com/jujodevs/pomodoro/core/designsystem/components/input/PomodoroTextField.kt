package com.jujodevs.pomodoro.core.designsystem.components.input

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.jujodevs.pomodoro.core.designsystem.theme.PomodoroTheme
import com.jujodevs.pomodoro.core.designsystem.theme.TextSecondary

/**
 * Pomodoro Text Field Component
 *
 * Text input field with custom styling
 *
 * @param value Current value
 * @param onValueChange Callback when value changes
 * @param modifier Modifier for styling
 * @param label Optional label text
 * @param placeholder Optional placeholder text
 * @param enabled Whether field is enabled
 * @param singleLine Whether field is single line
 */
@Composable
fun PomodoroTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: String? = null,
    placeholder: String? = null,
    enabled: Boolean = true,
    singleLine: Boolean = true
) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        enabled = enabled,
        label = if (label != null) {
            { Text(label) }
        } else {
            null
        },
        placeholder = if (placeholder != null) {
            { Text(placeholder) }
        } else {
            null
        },
        singleLine = singleLine,
        shape = MaterialTheme.shapes.medium,
        colors = TextFieldDefaults.colors(
            focusedContainerColor = MaterialTheme.colorScheme.surface,
            unfocusedContainerColor = MaterialTheme.colorScheme.surface,
            disabledContainerColor = MaterialTheme.colorScheme.surface,
            focusedTextColor = MaterialTheme.colorScheme.onSurface,
            unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
            focusedLabelColor = TextSecondary,
            unfocusedLabelColor = TextSecondary,
            focusedPlaceholderColor = TextSecondary,
            unfocusedPlaceholderColor = TextSecondary,
            focusedIndicatorColor = MaterialTheme.colorScheme.primary,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent,
        )
    )
}

@Preview(showBackground = true, backgroundColor = 0xFF1C2834)
@Composable
private fun PomodoroTextFieldPreview() {
    PomodoroTheme {
        PomodoroTextField(
            value = "John Doe",
            onValueChange = {},
            label = "Name",
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF1C2834)
@Composable
private fun PomodoroTextFieldEmptyPreview() {
    PomodoroTheme {
        PomodoroTextField(
            value = "",
            onValueChange = {},
            placeholder = "Enter your name",
            modifier = Modifier.fillMaxWidth()
        )
    }
}
