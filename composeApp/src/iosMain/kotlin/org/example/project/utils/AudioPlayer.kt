package org.example.project.utils

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.alloc
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.ptr
import kotlinx.cinterop.value
import platform.AVFAudio.AVAudioPlayer
import platform.AudioToolbox.AudioServicesCreateSystemSoundID
import platform.AudioToolbox.AudioServicesDisposeSystemSoundID
import platform.AudioToolbox.AudioServicesPlaySystemSound
import platform.CoreFoundation.CFURLRef
import platform.Foundation.NSBundle
import platform.Foundation.NSURL
import platform.darwin.UInt32Var

actual open class AudioPlayer actual constructor(
    private val isEnabled: () -> Boolean,
) {

    private val players = mutableMapOf<SoundEffect, AVAudioPlayer>()
    private val systemSounds = mutableMapOf<SoundEffect, SystemSoundID>()

    actual fun preload() {

        // Ultra short sounds -> SystemSoundID
        loadSystemSound(
            SoundEffect.BUTTON_CLICK,
            "button_click",
            "wav"
        )

        loadSystemSound(
            SoundEffect.KEY_PRESS_1,
            "key1",
            "wav"
        )

        loadSystemSound(
            SoundEffect.KEY_PRESS_2,
            "key2",
            "wav"
        )

        loadSystemSound(
            SoundEffect.KEY_PRESS_3,
            "key3",
            "wav"
        )

        loadSystemSound(
            SoundEffect.TIMER_TICK,
            "tick",
            "wav"
        )

        // Richer sounds -> AVAudioPlayer
        loadPlayer(
            SoundEffect.NEW_RECORD,
            "new_record",
            "mp3"
        )

        loadPlayer(
            SoundEffect.GAME_FINISH,
            "finish",
            "mp3"
        )

        loadPlayer(
            SoundEffect.GAME_FAIL,
            "fail",
            "mp3"
        )
    }

    actual fun play(sound: SoundEffect) {
        if (!isEnabled()) return

        systemSounds[sound]?.let {
            AudioServicesPlaySystemSound(it)
            return
        }

        players[sound]?.let {
            it.currentTime = 0.0
            it.play()
        }
    }

    actual fun release() {
        systemSounds.values.forEach {
            AudioServicesDisposeSystemSoundID(it)
        }

        players.clear()
        systemSounds.clear()
    }

    @OptIn(ExperimentalForeignApi::class)
    private fun loadPlayer(
        effect: SoundEffect,
        fileName: String,
        extension: String
    ) {

        val path = NSBundle.mainBundle.pathForResource(
            name = fileName,
            ofType = extension
        ) ?: return

        val url = NSURL.fileURLWithPath(path)

        val player = AVAudioPlayer(contentsOfURL = url, error = null)

        player?.prepareToPlay()

        if (player != null) {
            players[effect] = player
        }
    }

    @OptIn(ExperimentalForeignApi::class)
    private fun loadSystemSound(
        effect: SoundEffect,
        fileName: String,
        extension: String
    ) {

        val path = NSBundle.mainBundle.pathForResource(fileName, extension)
            ?: return

        val nsUrl = NSURL.fileURLWithPath(path)

        val cfUrl = nsUrl as CFURLRef

        memScoped {

            val soundIdVar = alloc<UInt32Var>()

            AudioServicesCreateSystemSoundID(
                cfUrl,
                soundIdVar.ptr
            )

            systemSounds[effect] = soundIdVar.value
        }
    }
}