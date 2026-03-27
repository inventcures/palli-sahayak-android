package com.pallisahayak.core.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColorScheme = lightColorScheme(
    primary = PrimaryGreen,
    onPrimary = SurfaceWhite,
    primaryContainer = PrimaryGreenLight,
    secondary = SecondaryBlue,
    error = ErrorRed,
    background = BackgroundLight,
    surface = SurfaceWhite,
)

@Composable
fun PalliSahayakTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        typography = PalliSahayakTypography,
        content = content,
    )
}
