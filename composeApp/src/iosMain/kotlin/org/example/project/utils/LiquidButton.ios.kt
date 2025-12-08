package org.example.project.utils

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector

@Composable
actual fun LiquidButton(
    icon: ImageVector,
    title: String,
    description: String,
    onClick: () -> Unit,
) { }
