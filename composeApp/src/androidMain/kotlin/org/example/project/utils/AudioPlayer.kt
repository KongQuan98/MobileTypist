package org.example.project.utils

import android.media.SoundPool
import org.example.project.R

actual open class AudioPlayer actual constructor(
    private val isEnabled: () -> Boolean,
) {

    val context = AppContextProvider.context

    private val soundPool = SoundPool.Builder()
        .setMaxStreams(10)
        .build()

    private val sounds = mutableMapOf<SoundEffect, Int>()

    actual fun preload() {
        sounds[SoundEffect.BUTTON_CLICK] =
            soundPool.load(context, R.raw.button_switch, 1)

        sounds[SoundEffect.KEY_PRESS_1] =
            soundPool.load(context, R.raw.keyboard_click, 1)

        sounds[SoundEffect.NEW_RECORD] =
            soundPool.load(context, R.raw.new_record_wow, 1)

        sounds[SoundEffect.GAME_FINISH] =
            soundPool.load(context, R.raw.game_finish, 1)
    }

    actual fun play(sound: SoundEffect) {
        if (!isEnabled()) return

        if (sounds[sound] == null) {
            return
        }

        sounds[sound]?.let {
            soundPool.play(it, 1f, 1f, 1, 0, 1f)
        }
    }

    actual fun release() {
        soundPool.release()
    }
}