package org.example.project.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color
import kotlinx.serialization.Serializable
import mobiletypist.composeapp.generated.resources.Res
import mobiletypist.composeapp.generated.resources.heatmap_fire_icon
import mobiletypist.composeapp.generated.resources.heatmap_lavender_icon
import mobiletypist.composeapp.generated.resources.heatmap_ocean_icon
import mobiletypist.composeapp.generated.resources.heatmap_rose_icon
import mobiletypist.composeapp.generated.resources.heatmap_tree_icon
import mobiletypist.composeapp.generated.resources.theme_classic
import mobiletypist.composeapp.generated.resources.theme_forest
import mobiletypist.composeapp.generated.resources.theme_lavender
import mobiletypist.composeapp.generated.resources.theme_ocean
import mobiletypist.composeapp.generated.resources.theme_rose
import org.jetbrains.compose.resources.StringResource

@Serializable
enum class AppColorTheme {
    Classic,
    Ocean,
    Forest,
    Rose,
    Lavender;

    val labelRes: StringResource
        get() = when (this) {
            Classic -> Res.string.theme_classic
            Ocean -> Res.string.theme_ocean
            Forest -> Res.string.theme_forest
            Rose -> Res.string.theme_rose
            Lavender -> Res.string.theme_lavender
        }

    val iconRes: StringResource
        get() = when (this) {
            Classic -> Res.string.heatmap_fire_icon
            Ocean -> Res.string.heatmap_ocean_icon
            Forest -> Res.string.heatmap_tree_icon
            Rose -> Res.string.heatmap_rose_icon
            Lavender -> Res.string.heatmap_lavender_icon
        }

    val previewColor: Color
        get() = when (this) {
            Classic -> Color(0xFFE2B714)
            Ocean -> Color(0xFF56B6C2)
            Forest -> Color(0xFF7EBC59)
            Rose -> Color(0xFFE06C75)
            Lavender -> Color(0xFFA78BFA)
        }

    fun colorScheme(darkTheme: Boolean): ColorScheme = when (this) {
        Classic -> if (darkTheme) ClassicDark else ClassicLight
        Ocean -> if (darkTheme) OceanDark else OceanLight
        Forest -> if (darkTheme) ForestDark else ForestLight
        Rose -> if (darkTheme) RoseDark else RoseLight
        Lavender -> if (darkTheme) LavenderDark else LavenderLight
    }

    fun shimmerColor(darkTheme: Boolean): Color = when (this) {
        Classic -> if (darkTheme) Color(0xFF444444) else Color(0xFFCCCCCC)
        Ocean -> if (darkTheme) Color(0xFF2A3A42) else Color(0xFFB8D4DC)
        Forest -> if (darkTheme) Color(0xFF2A3528) else Color(0xFFC5D4BC)
        Rose -> if (darkTheme) Color(0xFF3A2A2A) else Color(0xFFD4BCBC)
        Lavender -> if (darkTheme) Color(0xFF322A3A) else Color(0xFFC8BCD4)
    }

    val heatmapColors: List<Color>
        get() = when (this) {
            Classic -> listOf(
                Color(0xFFFF8C42), // Ember
                Color(0xFFFF6B35), // Orange
                Color(0xFFFF4500), // Blaze
                Color(0xFFE63946)  // Inferno
            )

            Ocean -> listOf(
                Color(0xFFB8D4DC),
                Color(0xFF86B9C6),
                Color(0xFF56B6C2),
                Color(0xFF328A97)
            )

            Forest -> listOf(
                Color(0xFFC5D4BC),
                Color(0xFFA2BC8E),
                Color(0xFF7EBC59),
                Color(0xFF5A8E40)
            )

            Rose -> listOf(
                Color(0xFFD4BCBC),
                Color(0xFFE08E8E),
                Color(0xFFE06C75),
                Color(0xFFB8404A)
            )

            Lavender -> listOf(
                Color(0xFFC8BCD4),
                Color(0xFFB3A2CC),
                Color(0xFFA78BFA),
                Color(0xFF7C3AED)
            )
        }
}

private val ClassicLight = lightColorScheme(
    primary = Color(0xFFE2B714),
    onPrimary = Color(0xFF323437),
    primaryContainer = Color(0xFFFFF4D1),
    onPrimaryContainer = Color(0xFF241C00),
    background = Color(0xFFF2F2F2),
    surface = Color(0xFFFFFFFF),
    onBackground = Color(0xFF323437),
    onSurface = Color(0xFF323437),
    surfaceVariant = Color(0xFFE1E1E1),
    onSurfaceVariant = Color(0xFF646669),
    outline = Color(0xFFBDBDBD),
    error = Color(0xFFCA4754),
)

private val ClassicDark = darkColorScheme(
    primary = Color(0xFFE2B714),
    onPrimary = Color(0xFF323437),
    primaryContainer = Color(0xFF2C2E31),
    onPrimaryContainer = Color(0xFFE2B714),
    background = Color(0xFF1B1D1F),
    surface = Color(0xFF242629),
    onBackground = Color(0xFFD1D0C5),
    onSurface = Color(0xFFD1D0C5),
    surfaceVariant = Color(0xFF2C2E31),
    onSurfaceVariant = Color(0xFF646669),
    outline = Color(0xFF44474A),
    error = Color(0xFFCA4754),
)

private val OceanLight = lightColorScheme(
    primary = Color(0xFF56B6C2),
    onPrimary = Color(0xFF0E2A30),
    primaryContainer = Color(0xFFD4F0F4),
    onPrimaryContainer = Color(0xFF0A2026),
    background = Color(0xFFE8F4F6),
    surface = Color(0xFFF5FAFB),
    onBackground = Color(0xFF1A2B32),
    onSurface = Color(0xFF1A2B32),
    surfaceVariant = Color(0xFFD0E4EA),
    onSurfaceVariant = Color(0xFF4A6670),
    outline = Color(0xFF9DBDC6),
    error = Color(0xFFD45B5B),
)

private val OceanDark = darkColorScheme(
    primary = Color(0xFF56B6C2),
    onPrimary = Color(0xFF0E2A30),
    primaryContainer = Color(0xFF1E3A42),
    onPrimaryContainer = Color(0xFF9DE4EE),
    background = Color(0xFF1A2228),
    surface = Color(0xFF222C34),
    onBackground = Color(0xFFD0E0E6),
    onSurface = Color(0xFFD0E0E6),
    surfaceVariant = Color(0xFF2A3840),
    onSurfaceVariant = Color(0xFF7A949E),
    outline = Color(0xFF3E525A),
    error = Color(0xFFE57373),
)

private val ForestLight = lightColorScheme(
    primary = Color(0xFF7EBC59),
    onPrimary = Color(0xFF1A2E10),
    primaryContainer = Color(0xFFDCEFD0),
    onPrimaryContainer = Color(0xFF142410),
    background = Color(0xFFF0F4ED),
    surface = Color(0xFFF8FAF6),
    onBackground = Color(0xFF1E2A18),
    onSurface = Color(0xFF1E2A18),
    surfaceVariant = Color(0xFFD4E0CC),
    onSurfaceVariant = Color(0xFF5A6E50),
    outline = Color(0xFFA8BC9E),
    error = Color(0xFFCA4754),
)

private val ForestDark = darkColorScheme(
    primary = Color(0xFF7EBC59),
    onPrimary = Color(0xFF1A2E10),
    primaryContainer = Color(0xFF2A4020),
    onPrimaryContainer = Color(0xFFB8E0A0),
    background = Color(0xFF1A2118),
    surface = Color(0xFF222A20),
    onBackground = Color(0xFFD0DCC8),
    onSurface = Color(0xFFD0DCC8),
    surfaceVariant = Color(0xFF2E3A28),
    onSurfaceVariant = Color(0xFF7A8E70),
    outline = Color(0xFF425038),
    error = Color(0xFFE57373),
)

private val RoseLight = lightColorScheme(
    primary = Color(0xFFE06C75),
    onPrimary = Color(0xFF3A1418),
    primaryContainer = Color(0xFFFAD4D8),
    onPrimaryContainer = Color(0xFF3A1014),
    background = Color(0xFFF5F0F0),
    surface = Color(0xFFFCF8F8),
    onBackground = Color(0xFF2E2224),
    onSurface = Color(0xFF2E2224),
    surfaceVariant = Color(0xFFE8D8DA),
    onSurfaceVariant = Color(0xFF7A6064),
    outline = Color(0xFFC4A8AC),
    error = Color(0xFFCA4754),
)

private val RoseDark = darkColorScheme(
    primary = Color(0xFFE06C75),
    onPrimary = Color(0xFF3A1418),
    primaryContainer = Color(0xFF4A2830),
    onPrimaryContainer = Color(0xFFF0B0B8),
    background = Color(0xFF211A1A),
    surface = Color(0xFF2A2222),
    onBackground = Color(0xFFE8D8D8),
    onSurface = Color(0xFFE8D8D8),
    surfaceVariant = Color(0xFF3A3030),
    onSurfaceVariant = Color(0xFF9A8084),
    outline = Color(0xFF524040),
    error = Color(0xFFE57373),
)

private val LavenderLight = lightColorScheme(
    primary = Color(0xFFA78BFA),
    onPrimary = Color(0xFF2A1848),
    primaryContainer = Color(0xFFE8DEFF),
    onPrimaryContainer = Color(0xFF241840),
    background = Color(0xFFF3F0F8),
    surface = Color(0xFFFAF8FC),
    onBackground = Color(0xFF282030),
    onSurface = Color(0xFF282030),
    surfaceVariant = Color(0xFFDDD4EC),
    onSurfaceVariant = Color(0xFF6E6080),
    outline = Color(0xFFB8A8CC),
    error = Color(0xFFCA4754),
)

private val LavenderDark = darkColorScheme(
    primary = Color(0xFFA78BFA),
    onPrimary = Color(0xFF2A1848),
    primaryContainer = Color(0xFF3A2860),
    onPrimaryContainer = Color(0xFFD4C0FF),
    background = Color(0xFF1E1A24),
    surface = Color(0xFF262230),
    onBackground = Color(0xFFE0D8EC),
    onSurface = Color(0xFFE0D8EC),
    surfaceVariant = Color(0xFF342E40),
    onSurfaceVariant = Color(0xFF9080A8),
    outline = Color(0xFF484058),
    error = Color(0xFFE57373),
)
