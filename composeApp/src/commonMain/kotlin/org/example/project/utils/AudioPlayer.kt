package org.example.project.utils

expect open class AudioPlayer(
    isEnabled: () -> Boolean,
) {
    fun play(sound: SoundEffect)
    fun preload()
    fun release()
}