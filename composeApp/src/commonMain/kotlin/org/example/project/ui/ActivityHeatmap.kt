package org.example.project.ui

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import mobiletypist.composeapp.generated.resources.Res
import mobiletypist.composeapp.generated.resources.heatmap_day_fri
import mobiletypist.composeapp.generated.resources.heatmap_day_mon
import mobiletypist.composeapp.generated.resources.heatmap_day_sat
import mobiletypist.composeapp.generated.resources.heatmap_day_sun
import mobiletypist.composeapp.generated.resources.heatmap_day_thu
import mobiletypist.composeapp.generated.resources.heatmap_day_tue
import mobiletypist.composeapp.generated.resources.heatmap_day_wed
import mobiletypist.composeapp.generated.resources.heatmap_detail_placeholder
import mobiletypist.composeapp.generated.resources.heatmap_fire_icon
import mobiletypist.composeapp.generated.resources.heatmap_less
import mobiletypist.composeapp.generated.resources.heatmap_more
import mobiletypist.composeapp.generated.resources.heatmap_no_activity
import mobiletypist.composeapp.generated.resources.heatmap_typed_on
import org.example.project.MobileTypistTheme
import org.example.project.data.repo.ActivityHeatmapRepository
import org.example.project.data.repo.HeatmapCell
import org.example.project.data.repo.MonthHeatmapData
import org.example.project.data.repo.YearMonth
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

private val FireEmber = Color(0xFFFF8C42)
private val FireOrange = Color(0xFFFF6B35)
private val FireBlaze = Color(0xFFFF4500)
private val FireInferno = Color(0xFFE63946)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActivityHeatmap(
    dailyDurations: Map<String, Int>,
    isVisible: Boolean = true,
    modifier: Modifier = Modifier,
) {
    val availableMonths = remember(dailyDurations) {
        ActivityHeatmapRepository.availableMonths(dailyDurations)
    }
    val pagerState = rememberPagerState(
        initialPage = (availableMonths.lastIndex).coerceAtLeast(0),
        pageCount = { availableMonths.size.coerceAtLeast(1) },
    )
    val coroutineScope = rememberCoroutineScope()
    var monthMenuExpanded by remember { mutableStateOf(false) }
    var selectedCell by remember { mutableStateOf<HeatmapCell?>(null) }
    var animationEpoch by remember { mutableIntStateOf(0) }

    val currentMonth = availableMonths.getOrElse(pagerState.currentPage) {
        availableMonths.lastOrNull() ?: YearMonth(2026, 7)
    }

    LaunchedEffect(pagerState.currentPage) {
        selectedCell = null
        animationEpoch++
    }

    LaunchedEffect(isVisible) {
        if (isVisible) {
            animationEpoch++
        }
    }

    val dayHeaderLabels = listOf(
        Res.string.heatmap_day_sun,
        Res.string.heatmap_day_mon,
        Res.string.heatmap_day_tue,
        Res.string.heatmap_day_wed,
        Res.string.heatmap_day_thu,
        Res.string.heatmap_day_fri,
        Res.string.heatmap_day_sat,
    )

    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.65f),
        tonalElevation = 0.dp,
        shadowElevation = 0.dp,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.35f),
                    shape = RoundedCornerShape(16.dp),
                )
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            ExposedDropdownMenuBox(
                expanded = monthMenuExpanded,
                onExpandedChange = { monthMenuExpanded = it },
                modifier = Modifier.fillMaxWidth(),
            ) {
                Surface(
                    onClick = { monthMenuExpanded = true },
                    modifier = Modifier
                        .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable)
                        .fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = heatmapMonthYearLabel(currentMonth),
                            style = TextStyle(
                                fontFamily = FontFamily.Monospace,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface,
                            ),
                        )
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = monthMenuExpanded)
                    }
                }

                ExposedDropdownMenu(
                    expanded = monthMenuExpanded,
                    onDismissRequest = { monthMenuExpanded = false },
                ) {
                    availableMonths.forEachIndexed { index, yearMonth ->
                        DropdownMenuItem(
                            text = {
                                Text(
                                    text = heatmapMonthYearLabel(yearMonth),
                                    fontFamily = FontFamily.Monospace,
                                )
                            },
                            onClick = {
                                monthMenuExpanded = false
                                coroutineScope.launch {
                                    pagerState.animateScrollToPage(index)
                                }
                            },
                        )
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                dayHeaderLabels.forEach { labelRes ->
                    Text(
                        text = stringResource(labelRes),
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Center,
                        style = TextStyle(
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontFamily = FontFamily.Monospace,
                        ),
                    )
                }
            }

            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxWidth(),
                beyondViewportPageCount = 1,
            ) { page ->
                val yearMonth = availableMonths.getOrElse(page) { currentMonth }
                val monthData = remember(yearMonth, dailyDurations) {
                    ActivityHeatmapRepository.buildMonthGrid(
                        year = yearMonth.year,
                        month = yearMonth.month,
                        dailyDurations = dailyDurations,
                    )
                }

                MonthHeatmapGrid(
                    monthData = monthData,
                    selectedCell = if (pagerState.currentPage == page) selectedCell else null,
                    animationEpoch = if (pagerState.currentPage == page && isVisible) animationEpoch else 0,
                    onCellClick = { cell ->
                        if (cell.day != null) {
                            selectedCell = cell
                        }
                    },
                )
            }

            HeatmapLegend()

            val detailText = selectedCell?.let { cell ->
                if (cell.day != null && cell.durationSeconds > 0) {
                    stringResource(
                        Res.string.heatmap_typed_on,
                        heatmapFormatDuration(cell.durationSeconds),
                        heatmapDayLabel(currentMonth.year, currentMonth.month, cell.day),
                    )
                } else if (cell.day != null) {
                    stringResource(
                        Res.string.heatmap_no_activity,
                        heatmapDayLabel(currentMonth.year, currentMonth.month, cell.day),
                    )
                } else {
                    null
                }
            }

            Text(
                text = detailText ?: stringResource(Res.string.heatmap_detail_placeholder),
                style = TextStyle(
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontFamily = FontFamily.Monospace,
                ),
                minLines = 1,
            )
        }
    }
}

@Composable
private fun MonthHeatmapGrid(
    monthData: MonthHeatmapData,
    selectedCell: HeatmapCell?,
    animationEpoch: Int,
    onCellClick: (HeatmapCell) -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        monthData.weeks.forEachIndexed { weekIndex, week ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                week.forEachIndexed { dayIndex, cell ->
                    HeatmapDayCell(
                        cell = cell,
                        maxDurationSeconds = monthData.maxDurationSeconds,
                        isSelected = cell.dateKey != null && cell.dateKey == selectedCell?.dateKey,
                        animationEpoch = animationEpoch,
                        animationIndex = weekIndex * 7 + dayIndex,
                        modifier = Modifier.weight(1f),
                        onClick = { onCellClick(cell) },
                    )
                }
            }
        }
    }
}

@Composable
private fun HeatmapDayCell(
    cell: HeatmapCell,
    maxDurationSeconds: Int,
    isSelected: Boolean,
    animationEpoch: Int,
    animationIndex: Int,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    val fireIcon = stringResource(Res.string.heatmap_fire_icon)
    val intensity = heatmapIntensity(cell.durationSeconds, maxDurationSeconds)
    val cellColor = heatmapCellColor(intensity)
    val fireTint = heatmapFireTint(intensity)
    val showFire = cell.day != null && cell.durationSeconds > 0

    val scale = remember(animationEpoch) { Animatable(0.72f) }
    val alpha = remember(animationEpoch) { Animatable(0f) }

    LaunchedEffect(animationEpoch) {
        if (animationEpoch == 0) {
            scale.snapTo(1f)
            alpha.snapTo(1f)
            return@LaunchedEffect
        }
        scale.snapTo(0.72f)
        alpha.snapTo(0f)
        val delay = (animationIndex * 28).coerceAtMost(560)
        launch {
            scale.animateTo(
                targetValue = 1f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessMediumLow,
                ),
            )
        }
        launch {
            alpha.animateTo(
                targetValue = 1f,
                animationSpec = tween(
                    durationMillis = 320,
                    delayMillis = delay,
                    easing = FastOutSlowInEasing,
                ),
            )
        }
    }

    Box(
        modifier = modifier
            .scale(scale.value)
            .alpha(alpha.value)
            .padding(top = 6.dp, end = 4.dp),
        contentAlignment = Alignment.Center,
    ) {
        if (showFire) {
            Text(
                text = fireIcon,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .offset(x = 6.dp, y = (-10).dp)
                    .scale(0.65f + intensity * 0.55f),
                style = TextStyle(
                    fontSize = (10 + intensity * 8).sp,
                    color = fireTint,
                ),
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(34.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(
                    if (cell.day == null) {
                        Color.Transparent
                    } else {
                        cellColor
                    },
                )
                .then(
                    if (isSelected && cell.day != null) {
                        Modifier.border(
                            width = 1.5.dp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.35f),
                            shape = RoundedCornerShape(10.dp),
                        )
                    } else {
                        Modifier
                    },
                )
                .then(
                    if (cell.day != null) {
                        Modifier.clickable(onClick = onClick)
                    } else {
                        Modifier
                    },
                ),
            contentAlignment = Alignment.Center,
        ) {
            if (cell.day != null) {
                Text(
                    text = cell.day.toString(),
                    style = TextStyle(
                        fontSize = 11.sp,
                        fontWeight = if (cell.durationSeconds > 0) FontWeight.SemiBold else FontWeight.Normal,
                        color = if (cell.durationSeconds > 0) {
                            MaterialTheme.colorScheme.onSurface
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                        },
                        fontFamily = FontFamily.Monospace,
                    ),
                )
            }
        }
    }
}

@Composable
private fun HeatmapLegend() {
    val fireIcon = stringResource(Res.string.heatmap_fire_icon)
    val levels = listOf(0f, 0.2f, 0.45f, 0.7f, 1f)

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = stringResource(Res.string.heatmap_less),
            style = TextStyle(
                fontSize = 10.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontFamily = FontFamily.Monospace,
            ),
            maxLines = 1,
            overflow = TextOverflow.Clip,
        )
        Spacer(Modifier.size(8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            levels.forEach { level ->
                Box(
                    modifier = Modifier
                        .size(18.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(heatmapCellColor(level)),
                    contentAlignment = Alignment.Center,
                ) {
                    if (level > 0f) {
                        Text(
                            text = fireIcon,
                            style = TextStyle(
                                fontSize = (8 + level * 6).sp,
                                color = heatmapFireTint(level),
                            ),
                        )
                    }
                }
            }
        }
        Spacer(Modifier.size(8.dp))
        Text(
            text = stringResource(Res.string.heatmap_more),
            style = TextStyle(
                fontSize = 10.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontFamily = FontFamily.Monospace,
            ),
            maxLines = 1,
            overflow = TextOverflow.Clip,
        )
    }
}

private fun heatmapIntensity(durationSeconds: Int, maxDurationSeconds: Int): Float {
    if (durationSeconds <= 0) return 0f
    return (durationSeconds.toFloat() / maxDurationSeconds.coerceAtLeast(1)).coerceIn(0f, 1f)
}

@Composable
private fun heatmapCellColor(intensity: Float): Color {
    val surface = MaterialTheme.colorScheme.surfaceVariant
    return when {
        intensity <= 0f -> surface.copy(alpha = 0.35f)
        intensity <= 0.25f -> FireEmber.copy(alpha = 0.18f)
        intensity <= 0.5f -> FireEmber.copy(alpha = 0.32f)
        intensity <= 0.75f -> FireOrange.copy(alpha = 0.42f)
        else -> FireBlaze.copy(alpha = 0.5f)
    }
}

private fun heatmapFireTint(intensity: Float): Color {
    return when {
        intensity <= 0.25f -> FireEmber
        intensity <= 0.5f -> FireOrange
        intensity <= 0.75f -> FireBlaze
        else -> FireInferno
    }
}

@Preview
@Composable
private fun ActivityHeatmapPreview() {
    val sampleDurations = mapOf(
        "2026-07-01" to 120,
        "2026-07-02" to 300,
        "2026-07-03" to 45,
        "2026-07-05" to 900,
        "2026-07-10" to 1800,
        "2026-07-15" to 600,
    )

    MobileTypistTheme(darkTheme = false) {
        ActivityHeatmap(
            dailyDurations = sampleDurations,
            isVisible = true,
        )
    }
}

@Preview
@Composable
private fun ActivityHeatmapDarkPreview() {
    val sampleDurations = mapOf(
        "2026-07-01" to 120,
        "2026-07-05" to 900,
        "2026-07-10" to 1800,
    )

    MobileTypistTheme(darkTheme = true) {
        ActivityHeatmap(
            dailyDurations = sampleDurations,
            isVisible = true,
        )
    }
}
