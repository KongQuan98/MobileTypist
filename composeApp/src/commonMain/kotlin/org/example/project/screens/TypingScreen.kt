package org.example.project.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import compose.icons.FeatherIcons
import compose.icons.feathericons.RefreshCw
import org.example.project.MobileTypistTheme
import org.example.project.ResultBottomSheet
import org.example.project.data.TypingMode
import org.example.project.data.TypingTestResult
import org.example.project.ui.CleanTypingArea
import org.example.project.ui.GlobalHiddenInputOverlay
import org.example.project.viewModel.TypingScreenAction
import org.example.project.viewModel.TypingViewModel
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun TypingScreen(
    mode: TypingMode,
    targetText: String,
    timeOptions: List<Int>? = null,
    wordOptions: List<Int>? = null,
    action: (TypingScreenAction) -> Unit,
    modifier: Modifier = Modifier,
    isStarted: Boolean = false
) {
    val coroutineScope = rememberCoroutineScope()
    val viewModel = remember { TypingViewModel(coroutineScope) }

    LaunchedEffect(mode, targetText, timeOptions, wordOptions) {
        viewModel.initialize(
            mode = mode,
            targetText = targetText,
            selectedTime = timeOptions?.firstOrNull() ?: 0,
            selectedWords = wordOptions?.firstOrNull() ?: 0
        )
    }

    TypingScreenContent(
        viewModel = viewModel,
        onTestComplete = { action(TypingScreenAction.OnTestComplete(it)) },
        onBack = { action(TypingScreenAction.OnNavigateBack) },
        isStarted = isStarted,
        modifier = modifier
    )
}

@Composable
fun TypingScreenContent(
    viewModel: TypingViewModel,
    onTestComplete: (TypingTestResult) -> Unit,
    onBack: () -> Unit,
    isStarted: Boolean,
    modifier: Modifier = Modifier
) {
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(isStarted) {
        if (isStarted) {
            viewModel.startTest()
            focusRequester.requestFocus()
        } else {
            viewModel.resetTest()
        }
    }

    if (viewModel.isFinished) {
        ResultBottomSheet(
            visible = true,
            correctCount = viewModel.correctCount,
            errorCount = viewModel.errorCount,
            selectedTime = 0,
            wpmHistory = viewModel.wpmHistory,
            onReset = { viewModel.resetTest() },
            onSurface = MaterialTheme.colorScheme.onSurface,
            onBg = MaterialTheme.colorScheme.surface,
            primary = MaterialTheme.colorScheme.primary
        )
    }

    Surface(
        modifier = modifier.fillMaxWidth(),
        color = Color.Transparent
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .windowInsetsPadding(WindowInsets.safeDrawing)
                .padding(horizontal = 20.dp, vertical = 16.dp)
        ) {

            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Live HUD
                AnimatedVisibility(
                    visible = viewModel.isRunning,
                    enter = fadeIn() + expandVertically(),
                    exit = fadeOut() + shrinkVertically()
                ) {
                    Row(
                        modifier = Modifier.padding(bottom = 32.dp),
                        horizontalArrangement = Arrangement.spacedBy(24.dp)
                    ) {
                        StatDisplay(label = "wpm", value = viewModel.currentWpm.toString())
                        StatDisplay(label = "acc", value = "${viewModel.currentAccuracy}%")
                        if (viewModel.timeLeft > 0) {
                            StatDisplay(label = "time", value = "${viewModel.timeLeft}s")
                        }
                    }
                }

                // Monkeytype Area
                CleanTypingArea(
                    targetText = viewModel.targetText,
                    input = viewModel.input,
                    enabled = !viewModel.isFinished,
                    charStatuses = viewModel.charStatuses.toList(),
                    modifier = Modifier.fillMaxWidth(0.9f)
                )

                // Restart button
                Spacer(modifier = Modifier.height(32.dp))
                IconButton(
                    onClick = { viewModel.resetTest() },
                    modifier = Modifier.size(48.dp)
                ) {
                    Icon(
                        imageVector = FeatherIcons.RefreshCw,
                        contentDescription = "Restart",
                        tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    )
                }
            }
        }

        GlobalHiddenInputOverlay(
            value = viewModel.input,
            onValueChange = { viewModel.onInputChanged(it) },
            enabled = !viewModel.isFinished,
            focusRequester = focusRequester,
        )
    }
}

@Composable
fun StatDisplay(label: String, value: String) {
    Column(horizontalAlignment = Alignment.Start) {
        Text(
            text = label,
            style = TextStyle(
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                fontFamily = FontFamily.Monospace
            )
        )
        Text(
            text = value,
            style = TextStyle(
                fontSize = 24.sp,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace
            )
        )
    }
}

@Preview
@Composable
private fun QuotesModeScreenPreview() {
    MobileTypistTheme(darkTheme = false) {
        TypingScreen(
            mode = TypingMode.QUOTES,
            targetText = "something like this",
            action = {},
            modifier = Modifier,
        )
    }
}

@Preview
@Composable
private fun QuotesModeScreenDarkModePreview() {
    MobileTypistTheme(darkTheme = true) {
        TypingScreen(
            mode = TypingMode.WORDS,
            targetText = "something like this",
            action = {},
            modifier = Modifier,
        )
    }
}
