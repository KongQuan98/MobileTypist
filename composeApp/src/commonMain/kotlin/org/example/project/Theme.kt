package org.example.project

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColors: ColorScheme = lightColorScheme(
    primary = Color(0xFF1E3A8A),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFD1E9FF),
    onPrimaryContainer = Color(0xFF001C3B),
    background = Color(0xFFF5F7FA),
    surface = Color(0xFFFFFFFF),
    onBackground = Color(0xFF111827),
    onSurface = Color(0xFF111827),
    surfaceVariant = Color(0xFFE5E7EB),
    onSurfaceVariant = Color(0xFF374151),
    outline = Color(0xFFD1D5DB)
)

private val DarkColors: ColorScheme = darkColorScheme(
    primary = Color(0xFF4A90E2),
    onPrimary = Color(0xFF001C3B),
    primaryContainer = Color(0xFF1E3A8A),
    onPrimaryContainer = Color(0xFFD1E9FF),
    background = Color(0xFF07080A),
    surface = Color(0xFF1F2937),
    onBackground = Color(0xFFF9FAFB),
    onSurface = Color(0xFFF9FAFB),
    surfaceVariant = Color(0xFF374151),
    onSurfaceVariant = Color(0xFFE5E7EB),
    outline = Color(0xFF4B5563)
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
