package org.example.project.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import compose.icons.FeatherIcons
import compose.icons.feathericons.Share2
import mobiletypist.composeapp.generated.resources.Res
import mobiletypist.composeapp.generated.resources.back
import mobiletypist.composeapp.generated.resources.result_accuracy
import mobiletypist.composeapp.generated.resources.result_correct
import mobiletypist.composeapp.generated.resources.result_errors
import mobiletypist.composeapp.generated.resources.result_keystrokes
import mobiletypist.composeapp.generated.resources.result_share
import mobiletypist.composeapp.generated.resources.result_words_per_minute
import org.example.project.MobileTypistTheme
import org.example.project.data.model.TypingMode
import org.example.project.data.model.TypingTestResult
import org.example.project.data.model.UserProfile
import org.example.project.utils.MainButton
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun ResultBottomSheet(
    visible: Boolean,
    result: TypingTestResult,
    wpmHistory: List<Int>,
    userProfile: UserProfile,
    streak: Int,
    onBack: () -> Unit = {},
) {
    val yellow = MaterialTheme.colorScheme.primary
    val keystrokes = result.correctChars + result.errorCount
    var showShareScreen by remember { mutableStateOf(false) }

    // Prevention of accidental fast dismissal
    var interactionEnabled by remember { mutableStateOf(false) }
    LaunchedEffect(visible) {
        if (visible) {
            kotlinx.coroutines.delay(1000)
            interactionEnabled = true
        } else {
            interactionEnabled = false
        }
    }

    if (showShareScreen) {
        ShareResultScreen(
            visible = true,
            result = result,
            userProfile = userProfile,
            streak = streak,
            onDismiss = { showShareScreen = false }
        )
    }

    AnimatedVisibility(
        visible = visible,
        enter = slideInVertically(
            initialOffsetY = { it },
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioLowBouncy,
                stiffness = Spring.StiffnessLow
            )
        ) + fadeIn(),
        exit = slideOutVertically(
            targetOffsetY = { it },
            animationSpec = tween(300)
        ) + fadeOut()
    ) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.background
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    modifier = Modifier.widthIn(max = 500.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    val scale = remember { Animatable(0.5f) }
                    LaunchedEffect(visible) {
                        if (visible) {
                            scale.animateTo(
                                1f, spring(
                                    dampingRatio = Spring.DampingRatioMediumBouncy,
                                    stiffness = Spring.StiffnessLow
                                )
                            )
                        }
                    }

                    Text(
                        text = result.wpm.toString(),
                        modifier = Modifier.scale(scale.value),
                        style = TextStyle(
                            fontSize = 80.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.primary,
                            fontFamily = FontFamily.Monospace
                        )
                    )
                    Text(
                        text = stringResource(Res.string.result_words_per_minute),
                        style = TextStyle(
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontFamily = FontFamily.Monospace,
                            letterSpacing = 2.sp
                        )
                    )

                    Spacer(Modifier.height(32.dp))

                    // Animated Chart Box
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(150.dp)
                            .padding(horizontal = 16.dp)
                    ) {
                        val chartProgress = remember { Animatable(0f) }
                        LaunchedEffect(visible) {
                            if (visible) {
                                chartProgress.animateTo(
                                    1f,
                                    tween(1500, easing = FastOutSlowInEasing)
                                )
                            }
                        }

                        Canvas(modifier = Modifier.fillMaxSize()) {
                            if (wpmHistory.isNotEmpty()) {
                                val maxWpm = wpmHistory.maxOrNull()?.coerceAtLeast(1) ?: 1
                                val stepX = size.width / (wpmHistory.size - 1).coerceAtLeast(1)

                                val path = Path().apply {
                                    wpmHistory.forEachIndexed { index, value ->
                                        val x = index * stepX
                                        val y =
                                            size.height - (value.toFloat() / maxWpm * size.height)
                                        if (index == 0) moveTo(x, y) else lineTo(x, y)
                                    }
                                }

                                drawPath(
                                    path = path,
                                    color = yellow,
                                    style = Stroke(
                                        width = 4.dp.toPx(),
                                        cap = StrokeCap.Round,
                                        join = StrokeJoin.Round
                                    )
                                )

                                val fillPath = Path().apply {
                                    addPath(path)
                                    lineTo(size.width, size.height)
                                    lineTo(0f, size.height)
                                    close()
                                }
                                drawPath(
                                    path = fillPath,
                                    brush = Brush.verticalGradient(
                                        colors = listOf(
                                            yellow.copy(alpha = 0.3f),
                                            Color.Transparent
                                        ),
                                        startY = 0f,
                                        endY = size.height
                                    ),
                                    alpha = chartProgress.value
                                )
                            }
                        }
                    }

                    Spacer(Modifier.height(32.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        AnimatedStatItem(
                            label = stringResource(Res.string.result_accuracy),
                            value = "${result.accuracy}%",
                            color = MaterialTheme.colorScheme.onSurface,
                            delay = 200
                        )
                        AnimatedStatItem(
                            label = stringResource(Res.string.result_correct),
                            value = result.correctChars.toString(),
                            color = MaterialTheme.colorScheme.primary,
                            delay = 400
                        )
                        AnimatedStatItem(
                            label = stringResource(Res.string.result_errors),
                            value = result.errorCount.toString(),
                            color = MaterialTheme.colorScheme.error,
                            delay = 600
                        )
                    }

                    Spacer(Modifier.height(24.dp))

                    Text(
                        text = stringResource(Res.string.result_keystrokes, keystrokes),
                        style = TextStyle(
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontFamily = FontFamily.Monospace
                        )
                    )

                    Spacer(Modifier.height(48.dp))

                    MainButton(
                        onClick = { if (interactionEnabled) showShareScreen = true },
                        enabled = interactionEnabled,
                        icon = FeatherIcons.Share2,
                        text = stringResource(Res.string.result_share)
                    )

                    Spacer(Modifier.height(12.dp))

                    MainButton(
                        onClick = { if (interactionEnabled) onBack() },
                        enabled = interactionEnabled,
                        text = stringResource(Res.string.back)
                    )
                }
            }
        }
    }
}

@Composable
private fun AnimatedStatItem(label: String, value: String, color: Color, delay: Int) {
    var startAnim by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        kotlinx.coroutines.delay(delay.toLong())
        startAnim = true
    }

    val alpha by animateFloatAsState(
        targetValue = if (startAnim) 1f else 0f,
        animationSpec = tween(500)
    )
    val slide by animateDpAsState(
        targetValue = if (startAnim) 0.dp else 20.dp,
        animationSpec = spring(Spring.DampingRatioMediumBouncy)
    )

    Column(
        modifier = Modifier
            .graphicsLayer { this.alpha = alpha }
            .offset(y = slide),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = value,
            style = TextStyle(
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = color,
                fontFamily = FontFamily.Monospace
            )
        )
        Text(
            text = label,
            style = TextStyle(
                fontSize = 10.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontFamily = FontFamily.Monospace,
                letterSpacing = 1.sp
            )
        )
    }
}

@Preview
@Composable
private fun PreviewRealGraphDarkTheme() {
    MobileTypistTheme(darkTheme = true) {
        ResultBottomSheet(
            visible = true,
            result = TypingTestResult(
                mode = TypingMode.TIME,
                wpm = 75,
                accuracy = 95,
                correctChars = 100,
                errorCount = 5,
                timestamp = 1_700_000_000_000L,
                duration = 60,
            ),
            wpmHistory = listOf(30, 45, 40, 55, 60, 58, 65, 70, 68, 75),
            userProfile = UserProfile(),
            streak = 12,
        )
    }
}

@Preview
@Composable
private fun PreviewRealGraph() {
    MobileTypistTheme(darkTheme = false) {
        ResultBottomSheet(
            visible = true,
            result = TypingTestResult(
                mode = TypingMode.TIME,
                wpm = 75,
                accuracy = 95,
                correctChars = 100,
                errorCount = 5,
                timestamp = 1_700_000_000_000L,
                duration = 60,
            ),
            wpmHistory = listOf(30, 45, 40, 55, 60, 58, 65, 70, 68, 75),
            userProfile = UserProfile(),
            streak = 12,
        )
    }
}
