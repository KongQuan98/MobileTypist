package org.example.project.utils

import android.media.SoundPool
import org.example.project.R

private class AndroidAudioPlayer(
    private val isEnabled: () -> Boolean,
) : AudioPlayerApi {

    private val context by lazy { AppContextProvider.context }

    private val soundPool by lazy {
        SoundPool.Builder()
            .setMaxStreams(10)
            .build()
    }

    private val sounds = mutableMapOf<SoundEffect, Int>()

    override fun preload() {
        sounds[SoundEffect.BUTTON_CLICK] =
            soundPool.load(context, R.raw.button_switch, 1)

        sounds[SoundEffect.KEY_PRESS_1] =
            soundPool.load(context, R.raw.keyboard_click, 1)

        sounds[SoundEffect.NEW_RECORD] =
            soundPool.load(context, R.raw.new_record_wow, 1)

        sounds[SoundEffect.GAME_FINISH] =
            soundPool.load(context, R.raw.game_finish, 1)
    }

    override fun play(sound: SoundEffect) {
        if (!isEnabled()) return

        if (sounds[sound] == null) {
            return
        }

        sounds[sound]?.let {
            soundPool.play(it, 1f, 1f, 1, 0, 1f)
        }
    }

    override fun release() {
        if (sounds.isEmpty()) return
        soundPool.release()
    }
}

actual fun createAudioPlayer(isEnabled: () -> Boolean): AudioPlayerApi =
    AndroidAudioPlayer(isEnabled)
