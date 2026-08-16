package org.example.project.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import compose.icons.FeatherIcons
import compose.icons.feathericons.Check
import compose.icons.feathericons.Lock
import compose.icons.feathericons.Zap
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.isoDayNumber
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime
import mobiletypist.composeapp.generated.resources.Res
import mobiletypist.composeapp.generated.resources.streak_badge_continued
import mobiletypist.composeapp.generated.resources.streak_badge_ended
import mobiletypist.composeapp.generated.resources.streak_badge_milestone
import mobiletypist.composeapp.generated.resources.streak_badge_new
import mobiletypist.composeapp.generated.resources.streak_badge_started
import mobiletypist.composeapp.generated.resources.streak_badge_welcome
import mobiletypist.composeapp.generated.resources.streak_button_keep
import mobiletypist.composeapp.generated.resources.streak_button_new
import mobiletypist.composeapp.generated.resources.streak_button_secured
import mobiletypist.composeapp.generated.resources.streak_days
import mobiletypist.composeapp.generated.resources.streak_headline_broken
import mobiletypist.composeapp.generated.resources.streak_headline_continued
import mobiletypist.composeapp.generated.resources.streak_headline_first
import mobiletypist.composeapp.generated.resources.streak_headline_started
import mobiletypist.composeapp.generated.resources.streak_headline_welcome
import mobiletypist.composeapp.generated.resources.streak_milestone_progress
import mobiletypist.composeapp.generated.resources.streak_milestones_title
import mobiletypist.composeapp.generated.resources.streak_more_days
import mobiletypist.composeapp.generated.resources.streak_next_milestone
import mobiletypist.composeapp.generated.resources.streak_subheadline_broken
import mobiletypist.composeapp.generated.resources.streak_subheadline_continued
import mobiletypist.composeapp.generated.resources.streak_subheadline_first
import mobiletypist.composeapp.generated.resources.streak_subheadline_milestone
import mobiletypist.composeapp.generated.resources.streak_subheadline_started
import mobiletypist.composeapp.generated.resources.streak_subheadline_welcome
import mobiletypist.composeapp.generated.resources.streak_title
import org.example.project.MobileTypistTheme
import org.example.project.dailystreak.model.StreakEvent
import org.example.project.di.LocalAppContainer
import org.example.project.utils.PreviewCompositionLocals
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import kotlin.time.ExperimentalTime

@Composable
fun StreakScreen(
    event: StreakEvent,
    onDismiss: () -> Unit
) {
    val appContainer = LocalAppContainer.current
    val storageManager = appContainer.storageManager
    val dailyActivity by storageManager.dailyActivityFlow.collectAsState()
    val streakData by storageManager.streakFlow.collectAsState()

    val currentStreak = when (event) {
        is StreakEvent.StreakContinued -> event.streak
        is StreakEvent.StreakStarted -> event.streak
        is StreakEvent.Milestone -> event.streak
        is StreakEvent.FirstPlay -> 1
        is StreakEvent.StreakBroken -> 0
        is StreakEvent.WelcomeBack -> 1
        else -> streakData.currentStreak
    }

    val themeColor = when (event) {
        is StreakEvent.StreakBroken -> MaterialTheme.colorScheme.error
        is StreakEvent.WelcomeBack -> Color(0xFFFF6B35) // Orange is not in standard M3, keeping for brand consistency or using secondary?
        else -> MaterialTheme.colorScheme.primary // Gold/Yellow
    }

    val badgeText = when (event) {
        is StreakEvent.FirstPlay -> stringResource(Res.string.streak_badge_started)
        is StreakEvent.StreakStarted -> stringResource(Res.string.streak_badge_new)
        is StreakEvent.StreakContinued -> stringResource(Res.string.streak_badge_continued)
        is StreakEvent.StreakBroken -> stringResource(Res.string.streak_badge_ended)
        is StreakEvent.WelcomeBack -> stringResource(Res.string.streak_badge_welcome)
        is StreakEvent.Milestone -> stringResource(Res.string.streak_badge_milestone)
        else -> stringResource(Res.string.streak_title)
    }

    val headline = when (event) {
        is StreakEvent.FirstPlay -> stringResource(Res.string.streak_headline_first)
        is StreakEvent.StreakStarted -> stringResource(
            Res.string.streak_headline_started,
            currentStreak
        )

        is StreakEvent.StreakContinued -> stringResource(
            Res.string.streak_headline_continued,
            currentStreak
        )

        is StreakEvent.StreakBroken -> stringResource(
            Res.string.streak_headline_broken,
            event.previousStreak
        )

        is StreakEvent.WelcomeBack -> stringResource(Res.string.streak_headline_welcome)
        is StreakEvent.Milestone -> stringResource(
            Res.string.streak_headline_continued,
            currentStreak
        )

        else -> ""
    }

    val subHeadline = when (event) {
        is StreakEvent.FirstPlay -> stringResource(Res.string.streak_subheadline_first)
        is StreakEvent.StreakStarted -> stringResource(Res.string.streak_subheadline_started)
        is StreakEvent.StreakContinued -> stringResource(Res.string.streak_subheadline_continued)
        is StreakEvent.StreakBroken -> stringResource(Res.string.streak_subheadline_broken)
        is StreakEvent.WelcomeBack -> stringResource(
            Res.string.streak_subheadline_welcome,
            event.daysAway
        )

        is StreakEvent.Milestone -> stringResource(Res.string.streak_subheadline_milestone)
        else -> ""
    }

    val buttonText = when (event) {
        is StreakEvent.StreakBroken -> stringResource(Res.string.streak_button_new)
        is StreakEvent.WelcomeBack -> stringResource(Res.string.streak_button_keep)
        else -> stringResource(Res.string.streak_button_secured)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(Modifier.height(40.dp))

            // Header
            Box(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = stringResource(Res.string.streak_title),
                    modifier = Modifier.align(Alignment.Center),
                    style = TextStyle(
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        letterSpacing = 1.sp,
                        fontFamily = FontFamily.Monospace
                    )
                )
            }

            Spacer(Modifier.height(32.dp))

            // Badge
            Surface(
                modifier = Modifier.align(Alignment.CenterHorizontally),
                color = themeColor.copy(alpha = 0.1f),
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    themeColor.copy(alpha = 0.2f)
                )
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = FeatherIcons.Zap,
                        contentDescription = null,
                        tint = themeColor,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(
                        text = badgeText,
                        style = TextStyle(
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = themeColor,
                            letterSpacing = 0.5.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            // Large Number
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.Bottom
            ) {
                Text(
                    text = currentStreak.toString(),
                    style = TextStyle(
                        fontSize = 100.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontFamily = FontFamily.Monospace
                    )
                )
                Spacer(Modifier.width(12.dp))
                Text(
                    text = stringResource(Res.string.streak_days),
                    modifier = Modifier.padding(bottom = 24.dp),
                    style = TextStyle(
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = themeColor,
                        fontFamily = FontFamily.Monospace
                    )
                )
            }

            Spacer(Modifier.height(16.dp))

            // Headline
            Text(
                text = headline,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                style = TextStyle(
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontFamily = FontFamily.Monospace
                )
            )

            Spacer(Modifier.height(8.dp))

            // Sub Headline
            Text(
                text = subHeadline,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                style = TextStyle(
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontFamily = FontFamily.Monospace,
                    lineHeight = 20.sp
                )
            )

            Spacer(Modifier.height(40.dp))

            // Weekly View
            WeeklyView(dailyActivity = dailyActivity, themeColor = themeColor)

            Spacer(Modifier.height(24.dp))

            // Next Milestone
            MilestoneCard(currentStreak = currentStreak, themeColor = themeColor)

            Spacer(Modifier.height(24.dp))

            // All Milestones
            StreakMilestonesSection(currentStreak = currentStreak)

            Spacer(Modifier.height(120.dp)) // Space for button
        }

        // Bottom Button
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            contentAlignment = Alignment.BottomCenter
        ) {
            Button(
                onClick = onDismiss,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = themeColor,
                    contentColor = if (themeColor == MaterialTheme.colorScheme.primary)
                        Color(0xFF323437) else Color.White
                ),
                shape = RoundedCornerShape(16.dp),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (event !is StreakEvent.StreakBroken) {
                        Icon(
                            imageVector = FeatherIcons.Check,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(Modifier.width(12.dp))
                    }
                    Text(
                        text = buttonText,
                        style = TextStyle(
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalTime::class)
@Composable
private fun WeeklyView(
    dailyActivity: Map<String, Int>,
    themeColor: Color
) {
    val days = listOf("MON", "TUE", "WED", "THU", "FRI", "SAT", "SUN")

    val today = Instant.fromEpochMilliseconds(kotlin.time.Clock.System.now().toEpochMilliseconds())
        .toLocalDateTime(TimeZone.currentSystemDefault()).date
    val isoDay = today.dayOfWeek.isoDayNumber // 1 (Mon) to 7 (Sun)
    val monday = today.minus(isoDay - 1, DateTimeUnit.DAY)

    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
        shape = RoundedCornerShape(24.dp),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            MaterialTheme.colorScheme.outline.copy(alpha = 0.1f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            days.forEachIndexed { index, day ->
                val date = monday.plus(index, DateTimeUnit.DAY)
                val dateKey = date.toString()
                val hasActivity = dailyActivity.containsKey(dateKey)
                val isToday = date == today

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = day,
                        style = TextStyle(
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isToday) themeColor else MaterialTheme.colorScheme.onSurfaceVariant.copy(
                                alpha = 0.5f
                            ),
                            fontFamily = FontFamily.Monospace
                        )
                    )

                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(
                                if (hasActivity) themeColor.copy(alpha = 0.15f)
                                else Color.Transparent
                            )
                            .border(
                                width = 1.dp,
                                color = when {
                                    hasActivity -> themeColor.copy(alpha = 0.5f)
                                    isToday -> themeColor.copy(alpha = 0.3f)
                                    else -> MaterialTheme.colorScheme.outline.copy(alpha = 0.1f)
                                },
                                shape = RoundedCornerShape(10.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        if (hasActivity) {
                            Icon(
                                imageVector = if (isToday) FeatherIcons.Zap else FeatherIcons.Check,
                                contentDescription = null,
                                tint = themeColor,
                                modifier = Modifier.size(16.dp)
                            )
                        } else if (isToday) {
                            Box(
                                modifier = Modifier
                                    .size(4.dp)
                                    .clip(CircleShape)
                                    .background(themeColor)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MilestoneCard(
    currentStreak: Int,
    themeColor: Color
) {
    val milestones = listOf(7, 14, 30, 50, 100, 365)
    val nextMilestone = milestones.find { it > currentStreak } ?: milestones.last()
    val progress = (currentStreak.toFloat() / nextMilestone).coerceIn(0f, 1f)
    val daysLeft = nextMilestone - currentStreak

    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
        shape = RoundedCornerShape(24.dp),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            MaterialTheme.colorScheme.outline.copy(alpha = 0.1f)
        )
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(Res.string.streak_next_milestone),
                    style = TextStyle(
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                        letterSpacing = 1.sp,
                        fontFamily = FontFamily.Monospace
                    )
                )

                Surface(
                    color = themeColor.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = stringResource(Res.string.streak_more_days, daysLeft),
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        style = TextStyle(
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = themeColor,
                            fontFamily = FontFamily.Monospace
                        )
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = FeatherIcons.Zap,
                    contentDescription = null,
                    tint = themeColor,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = nextMilestone.toString(),
                    style = TextStyle(
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontFamily = FontFamily.Monospace
                    )
                )
                Spacer(Modifier.width(6.dp))
                Text(
                    text = stringResource(Res.string.streak_days),
                    style = TextStyle(
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontFamily = FontFamily.Monospace
                    )
                )
            }

            Spacer(Modifier.height(16.dp))

            // Progress Bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.outline.copy(alpha = 0.1f))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(progress)
                        .fillMaxHeight()
                        .clip(CircleShape)
                        .background(themeColor)
                )
            }

            Spacer(Modifier.height(12.dp))

            Text(
                text = stringResource(
                    Res.string.streak_milestone_progress,
                    currentStreak,
                    nextMilestone
                ),
                style = TextStyle(
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                    fontFamily = FontFamily.Monospace
                )
            )
        }
    }
}

@Composable
private fun StreakMilestonesSection(currentStreak: Int) {
    val milestones = listOf(7, 14, 30, 50, 100)

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = stringResource(Res.string.streak_milestones_title),
            style = TextStyle(
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                letterSpacing = 1.sp,
                fontFamily = FontFamily.Monospace
            )
        )

        Spacer(Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            milestones.forEach { milestone ->
                val isReached = currentStreak >= milestone

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .aspectRatio(1f)
                        .clip(RoundedCornerShape(14.dp))
                        .background(
                            if (isReached) MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                            else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                        )
                        .border(
                            width = 1.dp,
                            color = if (isReached) MaterialTheme.colorScheme.primary.copy(alpha = 0.4f)
                            else MaterialTheme.colorScheme.outline.copy(alpha = 0.1f),
                            shape = RoundedCornerShape(14.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        if (isReached) {
                            Icon(
                                imageVector = FeatherIcons.Check,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(16.dp)
                            )
                        } else {
                            Icon(
                                imageVector = FeatherIcons.Lock,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.2f),
                                modifier = Modifier.size(16.dp)
                            )
                        }
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = milestone.toString(),
                            style = TextStyle(
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isReached) MaterialTheme.colorScheme.onSurface
                                else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f),
                                fontFamily = FontFamily.Monospace
                            )
                        )
                    }
                }
            }
        }
    }
}

@Preview
@Composable
private fun StreakScreenFirstPlayPreview() {
    PreviewCompositionLocals {
        MobileTypistTheme(darkTheme = false) {
            StreakScreen(
                event = StreakEvent.FirstPlay,
                onDismiss = {}
            )
        }
    }
}

@Preview
@Composable
private fun StreakScreenFirstPlayDarkPreview() {
    PreviewCompositionLocals {
        MobileTypistTheme(darkTheme = true) {
            StreakScreen(
                event = StreakEvent.FirstPlay,
                onDismiss = {}
            )
        }
    }
}

@Preview
@Composable
private fun StreakScreenStreakStartedPreview() {
    PreviewCompositionLocals {
        MobileTypistTheme(darkTheme = false) {
            StreakScreen(
                event = StreakEvent.StreakStarted(2),
                onDismiss = {}
            )
        }
    }
}

@Preview
@Composable
private fun StreakScreenStreakStartedDarkPreview() {
    PreviewCompositionLocals {
        MobileTypistTheme(darkTheme = true) {
            StreakScreen(
                event = StreakEvent.StreakStarted(2),
                onDismiss = {}
            )
        }
    }
}

@Preview
@Composable
private fun StreakScreenStreakContinuePreview() {
    PreviewCompositionLocals {
        MobileTypistTheme(darkTheme = false) {
            StreakScreen(
                event = StreakEvent.StreakContinued(3),
                onDismiss = {}
            )
        }
    }
}

@Preview
@Composable
private fun StreakScreenStreakContinueDarkPreview() {
    PreviewCompositionLocals {
        MobileTypistTheme(darkTheme = true) {
            StreakScreen(
                event = StreakEvent.StreakContinued(3),
                onDismiss = {}
            )
        }
    }
}

@Preview
@Composable
private fun StreakScreenStreakMilestonePreview() {
    PreviewCompositionLocals {
        MobileTypistTheme(darkTheme = false) {
            StreakScreen(
                event = StreakEvent.Milestone(5),
                onDismiss = {}
            )
        }
    }
}

@Preview
@Composable
private fun StreakScreenStreakMilestoneDarkPreview() {
    PreviewCompositionLocals {
        MobileTypistTheme(darkTheme = true) {
            StreakScreen(
                event = StreakEvent.Milestone(5),
                onDismiss = {}
            )
        }
    }
}

@Preview
@Composable
private fun StreakScreenBrokenPreview() {
    PreviewCompositionLocals {
        MobileTypistTheme(darkTheme = false) {
            StreakScreen(
                event = StreakEvent.StreakBroken(12),
                onDismiss = {}
            )
        }
    }
}

@Preview
@Composable
private fun StreakScreenBrokenDarkPreview() {
    PreviewCompositionLocals {
        MobileTypistTheme(darkTheme = true) {
            StreakScreen(
                event = StreakEvent.StreakBroken(12),
                onDismiss = {}
            )
        }
    }
}

@Preview
@Composable
private fun StreakScreenStreakWelcomeBackPreview() {
    PreviewCompositionLocals {
        MobileTypistTheme(darkTheme = false) {
            StreakScreen(
                event = StreakEvent.WelcomeBack(3),
                onDismiss = {}
            )
        }
    }
}

@Preview
@Composable
private fun StreakScreenStreakWelcomeBackdarkPreview() {
    PreviewCompositionLocals {
        MobileTypistTheme(darkTheme = true) {
            StreakScreen(
                event = StreakEvent.WelcomeBack(3),
                onDismiss = {}
            )
        }
    }
}
