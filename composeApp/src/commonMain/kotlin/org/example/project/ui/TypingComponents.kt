package org.example.project.ui

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
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
    input: String,
    enabled: Boolean,
    charStatuses: List<CharStatus>,
    modifier: Modifier = Modifier
) {
    val textMeasurer = rememberTextMeasurer()
    val scrollState = rememberScrollState()

    val pendingColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
    val correctColor = MaterialTheme.colorScheme.onSurface
    val errorColor = Color(0xFFCA4754)
    val caretColor = Color.Transparent

    val currentCharIndex = charStatuses.count { it != CharStatus.Pending }

    val annotatedString = buildAnnotatedString {
        targetText.forEachIndexed { index, char ->
            val color = when {
                index < charStatuses.size -> when (charStatuses[index]) {
                    CharStatus.Correct -> correctColor
                    CharStatus.Incorrect -> errorColor
                    CharStatus.Pending -> pendingColor
                }

                else -> pendingColor
            }
            withStyle(style = SpanStyle(color = color)) {
                append(char)
            }
        }
    }

    val textStyle = TextStyle(
        fontSize = 24.sp,
        fontFamily = FontFamily.Monospace,
        fontWeight = FontWeight.Medium,
        lineHeight = 36.sp,
        letterSpacing = 0.5.sp
    )

    Box(modifier = modifier) {
        val layoutResult = textMeasurer.measure(
            text = annotatedString,
            style = textStyle,
            constraints = Constraints(maxWidth = 1200)
        )

        // Caret Blink
        val infiniteTransition = rememberInfiniteTransition()
        val caretAlpha by infiniteTransition.animateFloat(
            initialValue = 1f,
            targetValue = 0f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 500, easing = LinearEasing),
                repeatMode = RepeatMode.Reverse
            )
        )

        // Smooth Caret Motion
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

        val animatedCaretX by animateFloatAsState(
            targetValue = caretOffset.left,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioNoBouncy,
                stiffness = Spring.StiffnessLow
            )
        )
        val animatedCaretY by animateFloatAsState(
            targetValue = caretOffset.top,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioNoBouncy,
                stiffness = Spring.StiffnessLow
            )
        )

        // Auto-scroll logic: Keep caret visible
        LaunchedEffect(animatedCaretY) {
            scrollState.animateScrollTo(animatedCaretY.toInt().coerceAtLeast(0))
        }

        Box(modifier = Modifier.fillMaxWidth().verticalScroll(scrollState)) {
            Text(
                text = annotatedString,
                style = textStyle,
                modifier = Modifier.fillMaxWidth()
            )

            if (enabled) {
                Canvas(modifier = Modifier.matchParentSize()) {
                    drawRect(
                        color = caretColor,
                        topLeft = Offset(animatedCaretX, animatedCaretY + 4.dp.toPx()),
                        size = androidx.compose.ui.geometry.Size(2.5.dp.toPx(), 24.sp.toPx()),
                        alpha = caretAlpha
                    )
                }
            }
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
