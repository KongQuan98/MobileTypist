package org.example.project.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import mobiletypist.composeapp.generated.resources.*
import org.example.project.MobileTypistTheme
import org.example.project.data.model.TypingMode
import org.example.project.data.model.TypingTestResult
import org.example.project.ui.ActivityHeatmap
import org.example.project.ui.shimmerEffect
import org.example.project.viewModel.StatisticsUiState
import org.example.project.viewModel.StatisticsViewModel
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun StatisticsScreen(
    viewModel: StatisticsViewModel,
    modifier: Modifier = Modifier,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.refreshData()
    }

    StatisticsScreenContent(
        uiState = uiState,
        modifier = modifier
    )
}

@Composable
fun StatisticsScreenContent(
    uiState: StatisticsUiState,
    modifier: Modifier = Modifier,
) {
    val results = uiState.results
    val bestWpm = uiState.bestWpm
    val totalTests = uiState.totalTests
    val dailyActivity = uiState.dailyActivity
    val isLoading = uiState.isLoading

    val avgWpm = if (results.isNotEmpty()) results.map { it.wpm }.average().toInt() else 0
    val totalSeconds = results.sumOf { it.duration }
    val minutesTyped = (totalSeconds % 3600) / 60
    val secondsTyped = totalSeconds % 60

    // Animation trigger
    var startAnimation by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        startAnimation = true
    }

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

            Spacer(Modifier.height(20.dp))

            HorizontalDivider(
                thickness = 1.dp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
            )

            val listState = rememberLazyListState()
            val isHeatmapVisible by remember {
                derivedStateOf {
                    listState.layoutInfo.visibleItemsInfo.any { it.key == "activity_heatmap" }
                }
            }

            LazyColumn(
                state = listState,
                modifier = Modifier.fillMaxSize(),
            ) {
                // Activity Heatmap
                item(key = "activity_heatmap") {
                    Spacer(Modifier.height(20.dp))
                    StatSectionLabel(stringResource(Res.string.statistics_activity_title).uppercase())
                    Spacer(Modifier.height(16.dp))
                    if (isLoading) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(280.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .shimmerEffect(),
                        )
                    } else {
                        ActivityHeatmap(
                            dailyActivity = dailyActivity,
                            isVisible = isHeatmapVisible,
                        )
                    }
                    Spacer(Modifier.height(40.dp))
                }

                // Summary Stats Grid
                item {
                    val animatedAvgWpm by animateFloatAsState(
                        targetValue = if (startAnimation) avgWpm.toFloat() else 0f,
                        animationSpec = tween(
                            durationMillis = 1000,
                            delayMillis = 0,
                            easing = FastOutSlowInEasing
                        )
                    )
                    val animatedBestWpm by animateFloatAsState(
                        targetValue = if (startAnimation) bestWpm.toFloat() else 0f,
                        animationSpec = tween(
                            durationMillis = 1000,
                            delayMillis = 0,
                            easing = FastOutSlowInEasing
                        )
                    )
                    val animatedTotalTests by animateFloatAsState(
                        targetValue = if (startAnimation) totalTests.toFloat() else 0f,
                        animationSpec = tween(
                            durationMillis = 1000,
                            delayMillis = 0,
                            easing = FastOutSlowInEasing
                        )
                    )
                    val animatedMinutesTyped by animateFloatAsState(
                        targetValue = if (startAnimation) minutesTyped.toFloat() else 0f,
                        animationSpec = tween(
                            durationMillis = 1000,
                            delayMillis = 0,
                            easing = FastOutSlowInEasing
                        )
                    )
                    val animatedSecondsTyped by animateFloatAsState(
                        targetValue = if (startAnimation) secondsTyped.toFloat() else 0f,
                        animationSpec = tween(
                            durationMillis = 1000,
                            delayMillis = 0,
                            easing = FastOutSlowInEasing
                        )
                    )

                    StatSectionLabel(stringResource(Res.string.statistics_summary_title).uppercase())
                    Spacer(Modifier.height(16.dp))
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            StatCard(
                                label = stringResource(Res.string.statistics_avg_wpm),
                                value = animatedAvgWpm.toInt().toString(),
                                textColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.weight(1f),
                                isLoading = isLoading
                            )
                            StatCard(
                                label = stringResource(Res.string.statistics_best_wpm),
                                value = animatedBestWpm.toInt().toString(),
                                textColor = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.weight(1f),
                                isLoading = isLoading
                            )
                        }
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            StatCard(
                                label = stringResource(Res.string.statistics_time_typed),
                                value = stringResource(
                                    Res.string.statistics_time_typed_value,
                                    animatedMinutesTyped.toInt(),
                                    animatedSecondsTyped.toInt()
                                ),
                                textColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.weight(1f),
                                isLoading = isLoading
                            )
                            StatCard(
                                label = stringResource(Res.string.statistics_tests),
                                value = animatedTotalTests.toInt().toString(),
                                textColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.weight(1f),
                                isLoading = isLoading
                            )
                        }
                    }
                    Spacer(Modifier.height(40.dp))
                }

                // WPM History Graph
                item {
                    StatSectionLabel(stringResource(Res.string.statistics_wpm_history))
                    Spacer(Modifier.height(16.dp))
                    WpmHistoryGraph(
                        results.takeLast(10).map { it.wpm },
                        MaterialTheme.colorScheme.primary,
                    )
                    Spacer(Modifier.height(40.dp))
                }

                // Accuracy Distribution
                item {
                    StatSectionLabel(stringResource(Res.string.statistics_accuracy_distribution))
                    Spacer(Modifier.height(16.dp))
                    AccuracyDistribution(
                        results = results,
                        startAnimation = startAnimation,
                    )
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
    isLoading: Boolean = false,
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
        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.6f)
                    .height(28.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .shimmerEffect()
            )
        } else {
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
}

@Composable
private fun WpmHistoryGraph(data: List<Int>, yellow: Color) {
    val animationProgress = remember { Animatable(0f) }

    LaunchedEffect(data) {
        animationProgress.snapTo(0f)
        animationProgress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 1500, easing = FastOutSlowInEasing)
        )
    }

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

            clipRect(right = size.width * animationProgress.value) {
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
}

@Composable
private fun AccuracyDistribution(
    results: List<TypingTestResult>,
    startAnimation: Boolean,
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
            val targetPercent = counts[index].toFloat() / total
            val animatedPercent by animateFloatAsState(
                targetValue = if (startAnimation) targetPercent else 0f,
                animationSpec = tween(
                    durationMillis = 1000,
                    delayMillis = index * 100,
                    easing = FastOutSlowInEasing
                )
            )

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
                        modifier = Modifier.fillMaxWidth(animatedPercent).fillMaxHeight()
                            .background(
                                MaterialTheme.colorScheme.primary,
                                RoundedCornerShape(4.dp)
                            )
                    )
                }
                Text(
                    text = "${(targetPercent * 100).toInt()}%",
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
private fun StatSectionLabel(text: String) {
    Text(
        text = text,
        style = TextStyle(
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontFamily = FontFamily.Monospace,
            letterSpacing = 1.sp
        )
    )
}

@Preview(heightDp = 1400)
@Composable
private fun StatisticsScreenPreview() {
    val dummyState = StatisticsUiState(
        results = listOf(
            TypingTestResult(
                wpm = 65,
                accuracy = 98,
                duration = 30,
                mode = TypingMode.TIME,
                correctChars = 100,
                errorCount = 2
            ),
            TypingTestResult(
                wpm = 72,
                accuracy = 95,
                duration = 30,
                mode = TypingMode.TIME,
                correctChars = 110,
                errorCount = 5
            ),
            TypingTestResult(
                wpm = 80,
                accuracy = 99,
                duration = 30,
                mode = TypingMode.TIME,
                correctChars = 120,
                errorCount = 1
            ),
        ),
        bestWpm = 80,
        totalTests = 3,
        dailyActivity = mapOf("2024-01-01" to 5),
        isLoading = false
    )

    MobileTypistTheme(darkTheme = false) {
        StatisticsScreenContent(uiState = dummyState)
    }
}

@Preview(heightDp = 1400)
@Composable
private fun StatisticsScreenDarkPreview() {
    val dummyState = StatisticsUiState(
        results = listOf(
            TypingTestResult(
                wpm = 65,
                accuracy = 98,
                duration = 30,
                mode = TypingMode.TIME,
                correctChars = 100,
                errorCount = 2
            ),
            TypingTestResult(
                wpm = 72,
                accuracy = 95,
                duration = 30,
                mode = TypingMode.TIME,
                correctChars = 110,
                errorCount = 5
            ),
            TypingTestResult(
                wpm = 80,
                accuracy = 99,
                duration = 30,
                mode = TypingMode.TIME,
                correctChars = 120,
                errorCount = 1
            ),
        ),
        bestWpm = 80,
        totalTests = 3,
        dailyActivity = mapOf("2024-01-01" to 5),
        isLoading = false
    )

    MobileTypistTheme(darkTheme = true) {
        StatisticsScreenContent(uiState = dummyState)
    }
}
