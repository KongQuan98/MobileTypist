package org.example.project

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import org.example.project.theme.AppColorTheme
import org.jetbrains.compose.resources.StringResource

data class CustomColors(
    val shimmerEffect: Color,
    val heatmapColors: List<Color>,
    val heatmapIcon: StringResource,
)

val LocalCustomColors = staticCompositionLocalOf {
    CustomColors(
        shimmerEffect = Color.Unspecified,
        heatmapColors = emptyList(),
        heatmapIcon = AppColorTheme.Classic.iconRes,
    )
}

object MobileTypistTheme {
    val customColors: CustomColors
        @Composable
        get() = LocalCustomColors.current
}

@Composable
fun MobileTypistTheme(
    darkTheme: Boolean,
    colorTheme: AppColorTheme = AppColorTheme.Classic,
    content: @Composable () -> Unit,
) {
    val colors = colorTheme.colorScheme(darkTheme)
    val customColors = CustomColors(
        shimmerEffect = colorTheme.shimmerColor(darkTheme),
        heatmapColors = colorTheme.heatmapColors,
        heatmapIcon = colorTheme.iconRes,
    )

    CompositionLocalProvider(LocalCustomColors provides customColors) {
        MaterialTheme(
            colorScheme = colors,
            content = content,
        )
    }
}
