package org.example.project.utils

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector

@Composable
expect fun LiquidBottomTabs(
    content: @Composable () -> Unit
)

data class TabListSettings(
    val title: String,
    val icon: ImageVector,
    val onClick: (Int) -> Unit,
)