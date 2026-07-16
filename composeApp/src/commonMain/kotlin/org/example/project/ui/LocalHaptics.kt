package org.example.project.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import org.example.project.utils.AudioPlayerApi
import org.example.project.utils.Haptics
import org.example.project.utils.SoundEffect

val LocalHaptics = staticCompositionLocalOf<Haptics> {
    error("No Haptics provided")
}

val LocalAudioPlayer = staticCompositionLocalOf<AudioPlayerApi> {
    error("No AudioPlayer provided")
}

@Composable
fun Modifier.hapticClickable(
    interactionSource: MutableInteractionSource? = null,
    onClick: () -> Unit,
): Modifier = composed {
    val haptics = LocalHaptics.current
    val audioPlayer = LocalAudioPlayer.current

    clickable(
        interactionSource = interactionSource
    ) {
        haptics.buttonClick()
        audioPlayer.play(SoundEffect.BUTTON_CLICK)
        onClick()
    }
}

fun Haptics.wrap(
    audioPlayer: AudioPlayerApi?,
    action: () -> Unit
) {
    buttonClick()
    audioPlayer?.play(SoundEffect.BUTTON_CLICK)
    action()
}
