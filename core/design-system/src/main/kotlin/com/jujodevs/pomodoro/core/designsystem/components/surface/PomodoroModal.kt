package com.jujodevs.pomodoro.core.designsystem.components.surface

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.jujodevs.pomodoro.core.designsystem.theme.DividerColor
import com.jujodevs.pomodoro.core.designsystem.theme.LocalSpacing
import com.jujodevs.pomodoro.core.designsystem.theme.PomodoroTheme

/**
 * Pomodoro Modal Component
 *
 * Bottom sheet modal with custom styling
 *
 * @param modifier Modifier for styling
 * @param content Modal content
 */
@Composable
fun PomodoroModal(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    val spacing = LocalSpacing.current

    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 8.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(spacing.spaceXL)
        ) {
            // Top indicator bar
            Box(
                modifier = Modifier
                    .width(40.dp)
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(DividerColor)
                    .align(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.height(spacing.spaceL))

            content()
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF1C2834)
@Composable
private fun PomodoroModalPreview() {
    PomodoroTheme {
        PomodoroModal {
            Text(
                text = "Settings",
                style = MaterialTheme.typography.headlineMedium
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "This is a modal bottom sheet",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}
