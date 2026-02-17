package org.example.project

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColors: ColorScheme = lightColorScheme(
    primary = Color(0xFFE2B714),       // Yellow accent (Classic Monkeytype)
    onPrimary = Color(0xFF323437),     // Dark text on yellow
    primaryContainer = Color(0xFFFFF4D1),
    onPrimaryContainer = Color(0xFF241C00),
    background = Color(0xFFF2F2F2),    // Off-white/paper background
    surface = Color(0xFFFFFFFF),
    onBackground = Color(0xFF323437),  // Muted dark grey text
    onSurface = Color(0xFF323437),
    surfaceVariant = Color(0xFFE1E1E1),
    onSurfaceVariant = Color(0xFF646669), // Low contrast for untyped text
    outline = Color(0xFFBDBDBD)
)

private val DarkColors: ColorScheme = darkColorScheme(
    primary = Color(0xFFE2B714),       // Yellow accent
    onPrimary = Color(0xFF323437),
    primaryContainer = Color(0xFF2C2E31),
    onPrimaryContainer = Color(0xFFE2B714),
    background = Color(0xFF1B1D1F),    // Deep charcoal (slightly bluer than #323437)
    surface = Color(0xFF242629),       // Card/Panel background
    onBackground = Color(0xFFD1D0C5),  // Off-white typed text
    onSurface = Color(0xFFD1D0C5),
    surfaceVariant = Color(0xFF2C2E31),
    onSurfaceVariant = Color(0xFF646669), // Grey for untyped text (Crucial for typing feel)
    outline = Color(0xFF44474A)
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