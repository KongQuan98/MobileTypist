package org.example.project.utils

import platform.UIKit.UIImpactFeedbackGenerator
import platform.UIKit.UIImpactFeedbackStyle
import platform.UIKit.UINotificationFeedbackGenerator
import platform.UIKit.UINotificationFeedbackType
import platform.UIKit.UISelectionFeedbackGenerator

actual class TypingHapticFeedback actual constructor(
    private val isEnabled: () -> Boolean,
) : Haptics {
    actual override fun buttonClick() {
        if (!isEnabled()) return

        val generator = UIImpactFeedbackGenerator(UIImpactFeedbackStyle.UIImpactFeedbackStyleMedium)
        generator.prepare()
        generator.impactOccurred()
    }

    actual override fun typingKey() {
        if (!isEnabled()) return

        val generator = UIImpactFeedbackGenerator(UIImpactFeedbackStyle.UIImpactFeedbackStyleLight)
        generator.prepare()
        generator.impactOccurred()
    }

    actual override fun notificationSuccess() {
        if (!isEnabled()) return

        val generator = UINotificationFeedbackGenerator()
        generator.prepare()
        generator.notificationOccurred(UINotificationFeedbackType.UINotificationFeedbackTypeSuccess) // 0 = success
    }

    actual override fun selectionChange() {
        if (!isEnabled()) return

        val generator = UISelectionFeedbackGenerator()
        generator.prepare()
        generator.selectionChanged()
    }
}