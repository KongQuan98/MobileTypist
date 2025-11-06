package org.example.project

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColors: ColorScheme = lightColorScheme(
    background = Color(0xFFF5F7FA),
    surface = Color(0xFFF5F7FA),
    onBackground = Color(0xFF2C3E50),
    onSurface = Color(0xFF2C3E50),
    primary = Color(0xFF4A90E2),
    onPrimary = Color(0xFFFFFFFF),
    secondary = Color(0xFF50C9A8),
    onSecondary = Color(0xFF0E2A23),
    tertiary = Color(0xFFFFB84D),
    onTertiary = Color(0xFF3A2A0F),
    outline = Color(0xFFE0E6ED)
)

private val DarkColors: ColorScheme = darkColorScheme(
    background = Color(0xFF1C1C1E),
    surface = Color(0xFF1C1C1E),
    onBackground = Color(0xFFEAEAEA),
    onSurface = Color(0xFFEAEAEA),
    primary = Color(0xFF4A90E2),
    onPrimary = Color(0xFF0B1B2A),
    secondary = Color(0xFF50C9A8),
    onSecondary = Color(0xFF00150F),
    tertiary = Color(0xFFFFB84D),
    onTertiary = Color(0xFF190F02),
    outline = Color(0xFF2C2C2E)
)

@Composable
fun MobileTypistTheme(
    darkTheme: Boolean,
    content: @Composable () -> Unit,
) {
    val colors = if (darkTheme) DarkColors else LightColors
    MaterialTheme(
        colorScheme = colors,
        content = content
    )
}



