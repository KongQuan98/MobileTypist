package org.example.project.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun ResultBottomSheet(
    visible: Boolean,
    correctCount: Int,
    errorCount: Int,
    selectedTime: Int,
    wpmHistory: List<Int>,
    onReset: () -> Unit,
    onBack: () -> Unit = {},
) {
    // drawPath need Color
    val yellow = Color(0xFFe2b714)

    val wpm = calculateWpm(correctCount, selectedTime)
    val accuracy = calculateAccuracy(correctCount, errorCount)
    val keystrokes = correctCount + errorCount

    AnimatedVisibility(
        visible = visible,
        enter = fadeIn() + scaleIn(initialScale = 0.9f),
        exit = fadeOut() + scaleOut(targetScale = 0.9f)
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
                    Text(
                        text = wpm.toString(), style = TextStyle(
                            fontSize = 60.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            fontFamily = FontFamily.Monospace
                        )
                    )
                    Text(
                        text = "WORDS PER MINUTE", style = TextStyle(
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontFamily = FontFamily.Monospace,
                            letterSpacing = 2.sp
                        )
                    )

                    Spacer(Modifier.height(16.dp))

                    // Dynamic Performance Graph
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp)
                            .padding(horizontal = 16.dp)
                    ) {
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
                                    path = path, color = yellow, style = Stroke(width = 3.dp.toPx())
                                )

                                val fillPath = Path().apply {
                                    addPath(path)
                                    lineTo(size.width, size.height)
                                    lineTo(0f, size.height)
                                    close()
                                }
                                drawPath(
                                    path = fillPath, brush = Brush.verticalGradient(
                                        colors = listOf(
                                            yellow.copy(alpha = 0.2f), Color.Transparent
                                        ), startY = 0f, endY = size.height
                                    )
                                )
                            }
                        }
                    }

                    Spacer(Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        StatItem(
                            label = "ACCURACY",
                            value = "$accuracy%",
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        StatItem(
                            label = "CORRECT",
                            value = correctCount.toString(),
                            color = MaterialTheme.colorScheme.primary
                        )
                        StatItem(
                            label = "ERRORS",
                            value = errorCount.toString(),
                            color = MaterialTheme.colorScheme.error
                        )
                    }

                    Spacer(Modifier.height(16.dp))

                    Text(
                        text = "$keystrokes keystrokes", style = TextStyle(
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontFamily = FontFamily.Monospace
                        )
                    )

                    Spacer(Modifier.height(32.dp))

                    Button(
                        onClick = onReset,
                        modifier = Modifier
                            .fillMaxWidth(0.85f)
                            .border(
                                2.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(12.dp)
                            ),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Transparent,
                            contentColor = MaterialTheme.colorScheme.primary
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = "restart test", style = TextStyle(
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                        )
                    }

                    Button(
                        onClick = onBack,
                        modifier = Modifier
                            .fillMaxWidth(0.85f)
                            .padding(top = 16.dp)
                            .border(
                                2.dp,
                                MaterialTheme.colorScheme.primary,
                                RoundedCornerShape(12.dp)
                            ),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Transparent,
                            contentColor = MaterialTheme.colorScheme.primary
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = "back",
                            style = TextStyle(
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun StatItem(label: String, value: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value, style = TextStyle(
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = color,
                fontFamily = FontFamily.Monospace
            )
        )
        Text(
            text = label, style = TextStyle(
                fontSize = 8.sp,
                color = Color(0xFF646669),
                fontFamily = FontFamily.Monospace,
                letterSpacing = 1.sp
            )
        )
    }
}

private fun calculateWpm(correctChars: Int, elapsedSeconds: Int): Int {
    if (elapsedSeconds <= 0) return 0
    val words = correctChars / 5.0
    val minutes = elapsedSeconds / 60.0
    return kotlin.math.round(words / minutes).toInt()
}

private fun calculateAccuracy(correct: Int, errors: Int): Int {
    val total = correct + errors
    if (total == 0) return 100
    return kotlin.math.round((correct.toDouble() / total.toDouble()) * 100.0).toInt()
}

@Preview
@Composable
private fun PreviewRealGraphDarkTheme() {
    _root_ide_package_.org.example.project.MobileTypistTheme(darkTheme = true) {
        ResultBottomSheet(
            visible = true,
            correctCount = 100,
            errorCount = 5,
            selectedTime = 10,
            wpmHistory = listOf(30, 45, 40, 55, 60, 58, 65, 70, 68, 75),
            onReset = { },
        )
    }
}

@Preview
@Composable
private fun PreviewRealGraph() {
    _root_ide_package_.org.example.project.MobileTypistTheme(darkTheme = false) {
        ResultBottomSheet(
            visible = true,
            correctCount = 100,
            errorCount = 5,
            selectedTime = 10,
            wpmHistory = listOf(30, 45, 40, 55, 60, 58, 65, 70, 68, 75),
            onReset = { },
        )
    }
}
