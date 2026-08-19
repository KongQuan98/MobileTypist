package org.example.project

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import org.example.project.theme.AppColorTheme

data class CustomColors(
    val shimmerEffect: Color,
)

val LocalCustomColors = staticCompositionLocalOf {
    CustomColors(
        shimmerEffect = Color.Unspecified,
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
    )

    CompositionLocalProvider(LocalCustomColors provides customColors) {
        MaterialTheme(
            colorScheme = colors,
            content = content,
        )
    }
}
