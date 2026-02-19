package com.jujodevs.pomodoro.features.settings.presentation.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.jujodevs.pomodoro.core.designsystem.theme.LocalSpacing
import com.jujodevs.pomodoro.core.designsystem.theme.PomodoroTheme

@Composable
fun versionRow(
    versionText: String,
    modifier: Modifier = Modifier,
) {
    val spacing = LocalSpacing.current

    Box(
        modifier =
            modifier
                .fillMaxWidth()
                .padding(vertical = spacing.spaceXL),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = versionText,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF1C2834)
@Composable
private fun versionRowPreview() {
    PomodoroTheme(darkTheme = true) {
        versionRow(
            versionText = "Version 1.0.0 (1)",
            modifier = Modifier.padding(16.dp),
        )
    }
}
