package org.example.project.utils

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator

actual class TypingHapticFeedback actual constructor(
    private val isEnabled: () -> Boolean,
) : Haptics {
    private val vibrator: Vibrator by lazy {
        val context = AppContextProvider.context
        context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    }

    actual override fun buttonClick() {
        if (!isEnabled()) return

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createOneShot(60, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            vibrator.vibrate(50)
        }
    }


    actual override fun typingKey() {
        if (!isEnabled()) return

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createOneShot(10, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            vibrator.vibrate(50)
        }
    }

    actual override fun notificationSuccess() {
        if (!isEnabled()) return

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createOneShot(30, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            vibrator.vibrate(50)
        }
    }

    actual override fun selectionChange() {
        if (!isEnabled()) return

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createOneShot(15, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            vibrator.vibrate(50)
        }
    }
}