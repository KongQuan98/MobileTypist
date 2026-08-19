package org.example.project.data.model

import kotlinx.serialization.Serializable
import org.example.project.theme.AppColorTheme
import org.example.project.theme.TypingFontFamily
import org.example.project.theme.TypingFontSize

@Serializable
data class AppSettings(
    val darkTheme: Boolean = true,
    val colorTheme: AppColorTheme = AppColorTheme.Classic,
    val typingFontSize: TypingFontSize = TypingFontSize.Medium,
    val typingFontFamily: TypingFontFamily = TypingFontFamily.Monospace,
    val soundEnabled: Boolean = true,
    val vibrationEnabled: Boolean = true,
    val showStatistics: Boolean = true,
)
