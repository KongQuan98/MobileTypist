package org.example.project.utils

import androidx.compose.runtime.Composable

@Composable
expect fun LiquidToggle(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
)


