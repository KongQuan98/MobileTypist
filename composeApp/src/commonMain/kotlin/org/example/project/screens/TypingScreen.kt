package org.example.project.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import compose.icons.FeatherIcons
import compose.icons.feathericons.RefreshCw
import org.example.project.CleanTypingArea
import org.example.project.GlobalHiddenInputOverlay
import org.example.project.ResultBottomSheet
import org.example.project.data.TypingMode
import org.example.project.data.TypingTestResult
import org.example.project.viewModel.TypingViewModel
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun TypingScreen(
    mode: TypingMode,
    targetText: String,
    timeOptions: List<Int>? = null,
    wordOptions: List<Int>? = null,
    onTestComplete: (TypingTestResult) -> Unit,
    onBack: () -> Unit,
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
        onTestComplete = onTestComplete,
        onBack = onBack,
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
    val background = Color(0xFF111827)
    val onSurface = Color(0xFFF9FAFB)

    LaunchedEffect(isStarted) {
        if (isStarted) {
            viewModel.startTest()
        } else {
            viewModel.resetTest()
        }
    }

    Surface(
        modifier = modifier.fillMaxSize(),
        color = background
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.safeDrawing)
                .padding(horizontal = 20.dp, vertical = 16.dp)
        ) {
            if (!isStarted) { // Only show internal start menus if not started from outside
                // Start menus are not part of this composable anymore
            }

            androidx.compose.animation.AnimatedVisibility(
                visible = viewModel.isRunning || viewModel.isFinished,
                enter = androidx.compose.animation.slideInVertically(
                    initialOffsetY = { it },
                    animationSpec = androidx.compose.animation.core.tween(500)
                ) + androidx.compose.animation.fadeIn(animationSpec = androidx.compose.animation.core.tween(500)),
                exit = androidx.compose.animation.slideOutVertically(
                    targetOffsetY = { -it },
                    animationSpec = androidx.compose.animation.core.tween(500)
                ) + androidx.compose.animation.fadeOut(animationSpec = androidx.compose.animation.core.tween(500))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 40.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        androidx.compose.material3.TextButton(
                            onClick = { onBack() }
                        ) {
                            Text(
                                text = "← Back",
                                style = TextStyle(
                                    fontSize = 16.sp,
                                    color = onSurface
                                )
                            )
                        }
                        // Top bar content based on mode
                    }

                    Box(
                        contentAlignment = Alignment.Center
                    ) {
                        CleanTypingArea(
                            targetText = viewModel.targetText,
                            cumulativeInput = "", // Not available in viewModel
                            input = viewModel.input,
                            onInputChange = { viewModel.onInputChanged(it) },
                            enabled = !viewModel.isFinished,
                            charStatuses = viewModel.charStatuses.toList(),
                        )
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        FloatingActionButton(
                            onClick = { viewModel.resetTest() },
                            modifier = Modifier.size(56.dp),
                            containerColor = background,
                            contentColor = onSurface
                        ) {
                            Icon(
                                imageVector = FeatherIcons.RefreshCw,
                                contentDescription = "Reset",
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                contentAlignment = Alignment.BottomCenter
            ) {
                ResultBottomSheet(
                    visible = viewModel.isFinished,
                    correctCount = viewModel.correctCount,
                    errorCount = viewModel.errorCount,
                    selectedTime = viewModel.timeLeft,
                    onReset = { viewModel.resetTest() },
                    onSurface = onSurface,
                    onBg = Color.Transparent,
                    primary = Color.Transparent
                )
            }
        }

        GlobalHiddenInputOverlay(
            value = viewModel.input,
            onValueChange = { viewModel.onInputChanged(it) },
            enabled = !viewModel.isFinished,
            focusRequester = FocusRequester(), // Needs to be managed by viewModel
        )
    }
}

@Preview
@Composable
private fun TypingScreenPreview() {
    val coroutineScope = rememberCoroutineScope()
    val viewModel = TypingViewModel(coroutineScope)
    org.example.project.MobileTypistTheme(darkTheme = true) {
        TypingScreenContent(
            viewModel = viewModel,
            onTestComplete = {},
            onBack = {},
            isStarted = true
        )
    }
}
