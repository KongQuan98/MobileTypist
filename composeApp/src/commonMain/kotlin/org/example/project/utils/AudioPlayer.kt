package org.example.project.utils

interface AudioPlayerApi {
    fun play(sound: SoundEffect)
    fun preload()
    fun release()
}

expect fun createAudioPlayer(isEnabled: () -> Boolean): AudioPlayerApi

/** @deprecated Prefer [createAudioPlayer] / [AudioPlayerApi]. Kept as a typealias for existing call sites. */
typealias AudioPlayer = AudioPlayerApi
