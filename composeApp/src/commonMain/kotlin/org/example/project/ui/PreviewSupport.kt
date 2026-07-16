package org.example.project.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import com.russhwolf.settings.MapSettings
import org.example.project.data.storage.StorageManager
import org.example.project.utils.AudioPlayerApi
import org.example.project.utils.Haptics
import org.example.project.utils.SoundEffect

/** No-op haptics for Compose previews. */
object PreviewHaptics : Haptics {
    override fun buttonClick() {}
    override fun typingKey() {}
    override fun notificationSuccess() {}
    override fun selectionChange() {}
}

/**
 * No-op audio for Compose previews.
 * Must not subclass [org.example.project.utils.AudioPlayer] — Android's implementation
 * references SoundPool, which is unavailable in the preview classloader.
 */
object PreviewAudioPlayer : AudioPlayerApi {
    override fun play(sound: SoundEffect) {}
    override fun preload() {}
    override fun release() {}
}

fun previewStorageManager(): StorageManager =
    StorageManager(settings = MapSettings())

@Composable
fun PreviewCompositionLocals(
    content: @Composable () -> Unit,
) {
    CompositionLocalProvider(
        LocalHaptics provides PreviewHaptics,
        LocalAudioPlayer provides PreviewAudioPlayer,
        content = content,
    )
}
