package com.jujodevs.pomodoro.ui

import android.graphics.Color
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable

@Suppress("MagicNumber")
private val DefaultLightSystemBarScrim = Color.argb(0xe6, 0xff, 0xff, 0xff)

@Suppress("MagicNumber")
private val DefaultDarkSystemBarScrim = Color.argb(0x80, 0x1b, 0x1b, 0x1b)

@Composable
fun ComponentActivity.ConfigureSystemBars(darkTheme: Boolean) {
    enableEdgeToEdge(
        statusBarStyle =
            SystemBarStyle.auto(
                lightScrim = Color.TRANSPARENT,
                darkScrim = Color.TRANSPARENT,
                detectDarkMode = { darkTheme },
            ),
        navigationBarStyle =
            SystemBarStyle.auto(
                lightScrim = DefaultLightSystemBarScrim,
                darkScrim = DefaultDarkSystemBarScrim,
                detectDarkMode = { darkTheme },
            ),
    )
}
