package org.example.project.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import org.example.project.utils.Haptics

val LocalHaptics = staticCompositionLocalOf<Haptics> {
    error("No Haptics provided")
}

@Composable
fun Modifier.hapticClickable(
    interactionSource: MutableInteractionSource? = null,
    onClick: () -> Unit,
): Modifier = composed {
    val haptics = LocalHaptics.current

    clickable(
        interactionSource = interactionSource
    ) {
        haptics.buttonClick()
        onClick()
    }
}

fun Haptics.wrap(action: () -> Unit) {
    buttonClick()
    action()
}
