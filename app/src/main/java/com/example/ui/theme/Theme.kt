package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val GamesDarkColorScheme = darkColorScheme(
    primary = ArcadeRed,
    onPrimary = Color.White,
    primaryContainer = Color(0xFF3B1522),
    onPrimaryContainer = Color(0xFFFFDADE),
    secondary = AccentBlue,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFF142945),
    onSecondaryContainer = Color(0xFFCCE5FF),
    tertiary = AccentCyan,
    background = BackgroundDark,
    onBackground = TextPrimary,
    surface = SurfaceDark,
    onSurface = TextPrimary,
    surfaceVariant = SurfaceCardDark,
    onSurfaceVariant = TextSecondary,
    outline = FrostedGlassBorder
)

@Composable
fun MyApplicationTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = GamesDarkColorScheme,
        typography = Typography,
        content = content
    )
}
