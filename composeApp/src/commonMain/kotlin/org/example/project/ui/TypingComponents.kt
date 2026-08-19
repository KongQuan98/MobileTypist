package org.example.project.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.example.project.screens.CharStatus
import org.example.project.theme.LocalTypingTextPreferences
import org.example.project.theme.toTextStyle

@Composable
fun CleanTypingArea(
    targetText: String,
    charStatuses: List<CharStatus>,
    currentCharIndex: Int,
    isQuoteMode: Boolean = false,
    modifier: Modifier = Modifier
) {
    AnimatedContent(
        targetState = targetText,
        transitionSpec = {
            fadeIn(animationSpec = tween(220)) togetherWith fadeOut(animationSpec = tween(180))
        },
        label = "typingTextTransition",
    ) { animatedTargetText ->
        CleanTypingAreaContent(
            targetText = animatedTargetText,
            charStatuses = charStatuses,
            currentCharIndex = currentCharIndex,
            isQuoteMode = isQuoteMode,
            modifier = modifier,
        )
    }
}

@Composable
private fun CleanTypingAreaContent(
    targetText: String,
    charStatuses: List<CharStatus>,
    currentCharIndex: Int,
    isQuoteMode: Boolean,
    modifier: Modifier = Modifier
) {
    val textMeasurer = rememberTextMeasurer()
    val scrollState = rememberScrollState()

    val primaryColor = MaterialTheme.colorScheme.primary
    val pendingColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
    val correctColor = MaterialTheme.colorScheme.onSurface
    val currentWordBgColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f)
    val errorColor = MaterialTheme.colorScheme.error

    // Find current word range for highlighting
    val currentWordRange = remember(targetText, currentCharIndex) {
        var start = currentCharIndex
        while (start > 0 && targetText[start - 1] != ' ') {
            start--
        }
        var end = currentCharIndex
        while (end < targetText.length && targetText[end] != ' ') {
            end++
        }
        start until end
    }

    val annotatedString = buildAnnotatedString {
        if (targetText.isEmpty()) return@buildAnnotatedString

        var i = 0
        while (i < targetText.length) {
            val start = i
            val char = targetText[i]

            val isCurrentChar = i == currentCharIndex
            val isInCurrentWord = i in currentWordRange

            val textColor = when {
                i < charStatuses.size -> when (charStatuses[i]) {
                    CharStatus.Correct -> correctColor
                    CharStatus.Incorrect -> errorColor
                    CharStatus.Pending -> if (isCurrentChar) primaryColor else pendingColor
                }
                else -> pendingColor
            }

            val backgroundColor = when {
                isCurrentChar && char == ' ' -> primaryColor.copy(alpha = 0.7f)
                isInCurrentWord && (charStatuses.getOrNull(i)
                    ?: CharStatus.Pending) == CharStatus.Pending -> currentWordBgColor

                else -> Color.Transparent
            }

            i++
            while (i < targetText.length) {
                val nextChar = targetText[i]
                val nextIsCurrentChar = i == currentCharIndex
                val nextIsInCurrentWord = i in currentWordRange

                val nextTextColor = when {
                    i < charStatuses.size -> when (charStatuses[i]) {
                        CharStatus.Correct -> correctColor
                        CharStatus.Incorrect -> errorColor
                        CharStatus.Pending -> if (nextIsCurrentChar) primaryColor else pendingColor
                    }

                    else -> pendingColor
                }

                val nextBackgroundColor = when {
                    nextIsCurrentChar && nextChar == ' ' -> primaryColor.copy(alpha = 0.7f)
                    nextIsInCurrentWord && (charStatuses.getOrNull(i)
                        ?: CharStatus.Pending) == CharStatus.Pending -> currentWordBgColor

                    else -> Color.Transparent
                }

                if (nextTextColor == textColor && nextBackgroundColor == backgroundColor) {
                    i++
                } else {
                    break
                }
            }

            withStyle(SpanStyle(color = textColor, background = backgroundColor)) {
                append(targetText.substring(start, i))
            }
        }
    }

    val typingPreferences = LocalTypingTextPreferences.current
    val textStyle = remember(isQuoteMode, typingPreferences) {
        typingPreferences.toTextStyle(isQuoteMode)
    }

    Box(modifier = modifier) {
        val layoutResult = remember(annotatedString, textStyle) {
            textMeasurer.measure(
                text = annotatedString,
                style = textStyle,
                constraints = Constraints(maxWidth = 1200)
            )
        }

        // Smooth Caret Motion calculation
        val caretOffset = remember(currentCharIndex, layoutResult) {
            when {
                currentCharIndex < targetText.length -> layoutResult.getCursorRect(currentCharIndex)
                targetText.isEmpty() -> {
                    layoutResult.getCursorRect(0).let {
                        it.copy(left = it.right)
                    }
                }

                else -> {
                    layoutResult.getCursorRect(targetText.length - 1).let {
                        it.copy(left = it.right)
                    }
                }
            }
        }

        // Auto-scroll logic: Keep typing position visible
        LaunchedEffect(caretOffset.top) {
            scrollState.animateScrollTo(caretOffset.top.toInt().coerceAtLeast(0))
        }

        Box(modifier = Modifier.fillMaxWidth().verticalScroll(scrollState)) {
            Text(
                text = annotatedString,
                style = textStyle,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
fun GlobalHiddenInputOverlay(
    value: String,
    onValueChange: (String) -> Unit,
    enabled: Boolean,
    focusRequester: FocusRequester,
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.BottomCenter
    ) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            enabled = enabled,
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .windowInsetsPadding(WindowInsets.ime)
                .focusRequester(focusRequester),
            textStyle = TextStyle(fontSize = 1.sp, color = Color.Transparent),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color.Transparent,
                unfocusedBorderColor = Color.Transparent,
                cursorColor = Color.Transparent,
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent
            )
        )
    }
}
