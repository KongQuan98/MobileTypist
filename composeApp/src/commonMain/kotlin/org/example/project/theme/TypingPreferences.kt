package org.example.project.theme

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import kotlinx.serialization.Serializable
import mobiletypist.composeapp.generated.resources.Res
import mobiletypist.composeapp.generated.resources.font_family_monospace
import mobiletypist.composeapp.generated.resources.font_family_sans_serif
import mobiletypist.composeapp.generated.resources.font_family_serif
import mobiletypist.composeapp.generated.resources.font_size_large
import mobiletypist.composeapp.generated.resources.font_size_medium
import mobiletypist.composeapp.generated.resources.font_size_small
import org.example.project.data.model.AppSettings
import org.jetbrains.compose.resources.StringResource

@Serializable
enum class TypingFontSize {
    Small,
    Medium,
    Large;

    val labelRes: StringResource
        get() = when (this) {
            Small -> Res.string.font_size_small
            Medium -> Res.string.font_size_medium
            Large -> Res.string.font_size_large
        }

    fun baseFontSize(isQuoteMode: Boolean): Int = when (this) {
        Small -> if (isQuoteMode) 18 else 20
        Medium -> if (isQuoteMode) 22 else 24
        Large -> if (isQuoteMode) 26 else 28
    }

    fun lineHeight(isQuoteMode: Boolean): Int = when (this) {
        Small -> if (isQuoteMode) 28 else 30
        Medium -> if (isQuoteMode) 34 else 36
        Large -> if (isQuoteMode) 40 else 42
    }
}

@Serializable
enum class TypingFontFamily {
    Monospace,
    SansSerif,
    Serif;

    val labelRes: StringResource
        get() = when (this) {
            Monospace -> Res.string.font_family_monospace
            SansSerif -> Res.string.font_family_sans_serif
            Serif -> Res.string.font_family_serif
        }

    fun toFontFamily(): FontFamily = when (this) {
        Monospace -> FontFamily.Monospace
        SansSerif -> FontFamily.SansSerif
        Serif -> FontFamily.Serif
    }
}

data class TypingTextPreferences(
    val fontSize: TypingFontSize = TypingFontSize.Medium,
    val fontFamily: TypingFontFamily = TypingFontFamily.Monospace,
)

val LocalTypingTextPreferences = staticCompositionLocalOf { TypingTextPreferences() }

fun AppSettings.toTypingTextPreferences(): TypingTextPreferences = TypingTextPreferences(
    fontSize = typingFontSize,
    fontFamily = typingFontFamily,
)

fun TypingTextPreferences.toTextStyle(isQuoteMode: Boolean): TextStyle {
    val sizeSp = fontSize.baseFontSize(isQuoteMode)
    return TextStyle(
        fontSize = sizeSp.sp,
        fontFamily = fontFamily.toFontFamily(),
        fontWeight = FontWeight.Medium,
        fontStyle = if (isQuoteMode) FontStyle.Italic else FontStyle.Normal,
        lineHeight = fontSize.lineHeight(isQuoteMode).sp,
        letterSpacing = 0.5.sp,
    )
}
