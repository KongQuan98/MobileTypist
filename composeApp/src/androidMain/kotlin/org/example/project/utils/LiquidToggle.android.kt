package org.example.project.utils

import androidx.compose.runtime.Composable
import com.kyant.backdrop.backdrops.rememberLayerBackdrop
import org.example.project.liquidglasssetup.LiquidToggle

@Composable
actual fun LiquidToggle(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
    val backdrop = rememberLayerBackdrop()

    LiquidToggle(
        selected = { checked },
        onSelect = {
            onCheckedChange(it)
        },
        backdrop = backdrop,
    )
}


