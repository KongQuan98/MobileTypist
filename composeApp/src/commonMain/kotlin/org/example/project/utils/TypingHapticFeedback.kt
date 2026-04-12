package org.example.project.utils

interface Haptics {
    fun buttonClick()
    fun typingKey()
    fun notificationSuccess()
    fun selectionChange()
}

// Factory to provide platform-specific implementation
expect class TypingHapticFeedback(
    isEnabled: () -> Boolean,
) : Haptics {
    override fun buttonClick()
    override fun typingKey()
    override fun notificationSuccess()
    override fun selectionChange()
}