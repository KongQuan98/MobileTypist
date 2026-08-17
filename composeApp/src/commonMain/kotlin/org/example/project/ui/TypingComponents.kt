package org.example.project.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.example.project.screens.CharStatus

@Composable
fun CleanTypingArea(
    targetText: String,
    charStatuses: List<CharStatus>,
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
            isQuoteMode = isQuoteMode,
            modifier = modifier,
        )
    }
}

@Composable
private fun CleanTypingAreaContent(
    targetText: String,
    charStatuses: List<CharStatus>,
    isQuoteMode: Boolean,
    modifier: Modifier = Modifier
) {
    val textMeasurer = rememberTextMeasurer()
    val scrollState = rememberScrollState()

    val primaryColor = MaterialTheme.colorScheme.primary
    val pendingColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
    val correctColor = MaterialTheme.colorScheme.onSurface
    val errorColor = Color(0xFFCA4754)

    val currentCharIndex = charStatuses.count { it != CharStatus.Pending }

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
        targetText.forEachIndexed { index, char ->
            val isCurrentChar = index == currentCharIndex
            val isInCurrentWord = index in currentWordRange

            val textColor = when {
                index < charStatuses.size -> when (charStatuses[index]) {
                    CharStatus.Correct -> correctColor
                    CharStatus.Incorrect -> errorColor
                    CharStatus.Pending -> if (isCurrentChar) {
                        primaryColor
                    } else {
                        pendingColor
                    }
                }
                else -> pendingColor
            }

            val backgroundColor = when {
                isCurrentChar && char == ' ' -> primaryColor.copy(alpha = 0.7f)
                isInCurrentWord && charStatuses.getOrNull(index) == CharStatus.Pending -> MaterialTheme.colorScheme.onSurface.copy(
                    alpha = 0.05f
                )

                else -> Color.Transparent
            }

            withStyle(
                style = SpanStyle(
                    color = textColor,
                    background = backgroundColor
                )
            ) {
                append(char)
            }
        }
    }

    val textStyle = TextStyle(
        fontSize = if (isQuoteMode) 22.sp else 24.sp,
        fontFamily = FontFamily.Monospace,
        fontWeight = FontWeight.Medium,
        fontStyle = if (isQuoteMode) FontStyle.Italic else FontStyle.Normal,
        lineHeight = if (isQuoteMode) 34.sp else 36.sp,
        letterSpacing = 0.5.sp
    )

    Box(modifier = modifier) {
        val layoutResult = textMeasurer.measure(
            text = annotatedString,
            style = textStyle,
            constraints = Constraints(maxWidth = 1200)
        )

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

        val animatedCaretY by animateFloatAsState(
            targetValue = caretOffset.top,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioNoBouncy,
                stiffness = Spring.StiffnessLow
            )
        )

        // Auto-scroll logic: Keep typing position visible
        LaunchedEffect(animatedCaretY) {
            scrollState.animateScrollTo(animatedCaretY.toInt().coerceAtLeast(0))
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
