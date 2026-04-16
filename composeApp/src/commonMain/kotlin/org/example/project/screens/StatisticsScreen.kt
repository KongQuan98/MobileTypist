package org.example.project.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import mobiletypist.composeapp.generated.resources.Res
import mobiletypist.composeapp.generated.resources.statistics_title
import org.example.project.MobileTypistTheme
import org.example.project.data.model.TypingMode
import org.example.project.data.model.TypingTestResult
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

data class StatisticsScreenState(
    val results: List<TypingTestResult> = emptyList(),
    val bestWpm: Int = 0,
    val totalTests: Int = 0
)

@Composable
fun StatisticsScreen(
    statisticsScreenState: StatisticsScreenState,
    modifier: Modifier = Modifier
) {
    val results = statisticsScreenState.results
    val bestWpm = statisticsScreenState.bestWpm
    val totalTests = statisticsScreenState.totalTests

    val avgWpm = if (results.isNotEmpty()) results.map { it.wpm }.average().toInt() else 0
    val totalSeconds = results.sumOf { it.duration }
    val hoursTyped = totalSeconds / 3600
    val minutesTyped = (totalSeconds % 3600) / 60

    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
                .padding(top = 40.dp)
        ) {
            Text(
                text = stringResource(Res.string.statistics_title),
                style = TextStyle(
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )
            )

            Spacer(modifier = Modifier.height(40.dp))

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
            ) {
                // Summary Stats Grid
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            StatCard(
                                label = "AVG WPM",
                                value = avgWpm.toString(),
                                textColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.weight(1f),
                            )
                            StatCard(
                                label = "BEST WPM",
                                value = bestWpm.toString(),
                                textColor = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.weight(1f),
                            )
                        }
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            StatCard(
                                label = "TIME TYPED",
                                value = "${hoursTyped}h ${minutesTyped}m",
                                textColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.weight(1f),
                            )
                            StatCard(
                                label = "TESTS",
                                value = totalTests.toString(),
                                textColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.weight(1f),
                            )
                        }
                    }
                    Spacer(Modifier.height(40.dp))
                }

                // WPM History Graph
                item {
                    StatSectionLabel("WPM HISTORY (LAST 10)")
                    Spacer(Modifier.height(16.dp))
                    WpmHistoryGraph(
                        results.takeLast(10).map { it.wpm },
                        MaterialTheme.colorScheme.primary,
                    )
                    Spacer(Modifier.height(40.dp))
                }

                // Accuracy Distribution
                item {
                    StatSectionLabel("ACCURACY DISTRIBUTION")
                    Spacer(Modifier.height(16.dp))
                    AccuracyDistribution(results)
                    Spacer(Modifier.height(40.dp))
                }

                // Activity Heatmap
                item {
                    StatSectionLabel("ACTIVITY (30 DAYS)")
                    Spacer(Modifier.height(16.dp))
                    ActivityHeatmap()
                    Spacer(Modifier.height(40.dp))
                }
            }
        }
    }
}

@Composable
private fun StatCard(
    label: String,
    value: String,
    textColor: Color,
    modifier: Modifier,
) {
    Column(
        modifier = modifier
            .background(
                MaterialTheme.colorScheme.surface.copy(alpha = 0.5f),
                RoundedCornerShape(12.dp)
            )
            .padding(20.dp)
    ) {
        Text(
            text = label,
            style = TextStyle(
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontFamily = FontFamily.Monospace
            )
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = value,
            style = TextStyle(
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = textColor,
                fontFamily = FontFamily.Monospace
            )
        )
    }
}

@Composable
private fun WpmHistoryGraph(data: List<Int>, yellow: Color) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp)
            .background(
                MaterialTheme.colorScheme.surface.copy(alpha = 0.5f),
                RoundedCornerShape(12.dp)
            )
            .padding(20.dp)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            if (data.size < 2) return@Canvas
            val max = data.maxOrNull()?.coerceAtLeast(1) ?: 1
            val stepX = size.width / (data.size - 1)

            val path = Path().apply {
                data.forEachIndexed { index, value ->
                    val x = index * stepX
                    val y = size.height - (value.toFloat() / max * size.height)
                    if (index == 0) moveTo(x, y) else lineTo(x, y)
                }
            }
            drawPath(path = path, color = yellow, style = Stroke(width = 3.dp.toPx()))

            // Draw points
            data.forEachIndexed { index, value ->
                val x = index * stepX
                val y = size.height - (value.toFloat() / max * size.height)
                drawCircle(
                    color = yellow,
                    radius = 4.dp.toPx(),
                    center = androidx.compose.ui.geometry.Offset(x, y)
                )
            }
        }
    }
}

@Composable
private fun AccuracyDistribution(
    results: List<TypingTestResult>,
) {
    val brackets = listOf("98-100%", "95-98%", "90-95%", "< 90%")
    val counts = brackets.map { 0 }.toMutableList()

    results.forEach {
        when {
            it.accuracy >= 98 -> counts[0]++
            it.accuracy >= 95 -> counts[1]++
            it.accuracy >= 90 -> counts[2]++
            else -> counts[3]++
        }
    }

    val total = results.size.coerceAtLeast(1)

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        brackets.forEachIndexed { index, label ->
            val percent = (counts[index].toFloat() / total * 100).toInt()
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = label,
                    modifier = Modifier.width(70.dp),
                    style = TextStyle(
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontFamily = FontFamily.Monospace
                    )
                )
                Box(
                    modifier = Modifier.weight(1f).height(8.dp)
                        .background(
                            MaterialTheme.colorScheme.surface.copy(alpha = 0.5f),
                            RoundedCornerShape(4.dp)
                        )
                ) {
                    Box(
                        modifier = Modifier.fillMaxWidth(percent / 100f).fillMaxHeight()
                            .background(
                                MaterialTheme.colorScheme.primary,
                                RoundedCornerShape(4.dp)
                            )
                    )
                }
                Text(
                    text = "$percent%",
                    modifier = Modifier.padding(start = 12.dp),
                    style = TextStyle(
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontFamily = FontFamily.Monospace
                    )
                )
            }
        }
    }
}

@Composable
private fun ActivityHeatmap() {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        repeat(4) { rowIndex ->
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                repeat(7) { colIndex ->
                    val intensity = (0..10).random() // Mock intensity
                    val color = when {
                        intensity > 8 -> MaterialTheme.colorScheme.primary
                        intensity > 5 -> MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)
                        intensity > 2 -> MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
                        else -> MaterialTheme.colorScheme.surface.copy(alpha = 0.5f)
                    }
                    Box(
                        modifier = Modifier.weight(1f).aspectRatio(1f)
                            .clip(RoundedCornerShape(4.dp)).background(color)
                    )
                }
            }
        }
    }
}

@Composable
private fun StatSectionLabel(text: String) {
    Text(
        text = text,
        style = TextStyle(
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF646669),
            fontFamily = FontFamily.Monospace,
            letterSpacing = 1.sp
        )
    )
}

@Preview
@Composable
private fun StatisticsScreenPreview() {
    val dummyProfileScreenState = StatisticsScreenState(
        results = listOf(
            TypingTestResult(
                id = "",
                mode = TypingMode.WORDS,
                wpm = 100,
                accuracy = 70,
                timestamp = 1000L,
                correctChars = 0,
                errorCount = 0,
                duration = 0,
            ),
            TypingTestResult(
                id = "",
                mode = TypingMode.WORDS,
                wpm = 100,
                accuracy = 90,
                timestamp = 1000L,
                correctChars = 0,
                errorCount = 0,
                duration = 0,
            ),
            TypingTestResult(
                id = "",
                mode = TypingMode.WORDS,
                wpm = 100,
                accuracy = 80,
                timestamp = 1000L,
                correctChars = 0,
                errorCount = 0,
                duration = 0,
            )
        ),
        bestWpm = 50,
        totalTests = 25,
    )

    MobileTypistTheme(darkTheme = false) {
        StatisticsScreen(
            statisticsScreenState = dummyProfileScreenState
        )
    }
}

@Preview
@Composable
private fun StatisticsScreenPreviewDarkTheme() {
    MobileTypistTheme(darkTheme = true) {
        StatisticsScreen(
            statisticsScreenState = StatisticsScreenState()
        )
    }
}
