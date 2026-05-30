package org.example.project.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
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
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import compose.icons.FeatherIcons
import compose.icons.feathericons.Award
import kotlinx.coroutines.delay
import org.example.project.MobileTypistTheme
import org.jetbrains.compose.ui.tooling.preview.Preview

data class LeaderboardEntry(
    val username: String,
    val wpm: Int,
    val accuracy: Int,
    val initials: String
)

@Composable
fun LeaderboardScreen(
    modifier: Modifier = Modifier
) {
    val tabs = listOf("15s", "30s", "60s", "all")
    var selectedTab by remember { mutableStateOf(1) } // 30s selected by default
    var animateStart by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        animateStart = true
    }

    // Mock data
    val entries = listOf(
        LeaderboardEntry("fastfinger", 142, 99, "FF"),
        LeaderboardEntry("keymaster", 138, 97, "KM"),
        LeaderboardEntry("typist_pro", 135, 98, "TP"),
        LeaderboardEntry("neon_rider", 131, 96, "NR"),
        LeaderboardEntry("cyber_cat", 128, 98, "CC"),
        LeaderboardEntry("matrix_neo", 125, 95, "MN"),
        LeaderboardEntry("glitch_01", 122, 94, "G1")
    )

    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
            val screenHeight = maxHeight
            val screenWidth = maxWidth

            // Responsive sizing: Use percentages of screen height
            val podiumSectionHeight = screenHeight * 0.35f
            val listWeight = 1f // LazyColumn takes remaining space

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp)
            ) {
                Spacer(Modifier.height(screenHeight * 0.04f))

                Text(
                    text = "leaderboard",
                    style = TextStyle(
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontFamily = FontFamily.Monospace
                    )
                )

                Spacer(Modifier.height(24.dp))

                // Custom Tab Bar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    tabs.forEachIndexed { index, title ->
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .clickableWithoutRipple { selectedTab = index },
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = title,
                                style = TextStyle(
                                    fontSize = 14.sp,
                                    color = if (selectedTab == index) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontFamily = FontFamily.Monospace
                                )
                            )
                            Spacer(Modifier.height(8.dp))
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(0.6f)
                                    .height(if (selectedTab == index) 2.dp else 1.dp)
                                    .background(
                                        if (selectedTab == index) MaterialTheme.colorScheme.primary
                                        else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.2f)
                                    )
                            )
                        }
                    }
                }

                Spacer(Modifier.height(24.dp))

                // Podium Section - Height is relative to screen
                if (entries.size >= 3) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(podiumSectionHeight),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        // 2nd Place (Left)
                        PodiumPosition(
                            entry = entries[1],
                            rank = 2,
                            heightMultiplier = 0.6f, // 60% of the max possible block height
                            containerHeight = podiumSectionHeight,
                            animate = animateStart,
                            modifier = Modifier.weight(1f)
                        )

                        // 1st Place (Center)
                        PodiumPosition(
                            entry = entries[0],
                            rank = 1,
                            heightMultiplier = 1.0f, // 100% of the max possible block height
                            containerHeight = podiumSectionHeight,
                            animate = animateStart,
                            modifier = Modifier.weight(1.2f),
                            showCrown = true
                        )

                        // 3rd Place (Right)
                        PodiumPosition(
                            entry = entries[2],
                            rank = 3,
                            heightMultiplier = 0.45f, // 45% of the max possible block height
                            containerHeight = podiumSectionHeight,
                            animate = animateStart,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                Spacer(Modifier.height(24.dp))

                // Remaining List - Takes up the rest of the screen
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(listWeight),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    itemsIndexed(
                        items = entries.drop(3)
                    ) { index, entry ->
                        var visible by remember {
                            mutableStateOf(false)
                        }

                        LaunchedEffect(Unit) {
                            delay(index * 50L)
                            visible = true
                        }

                        AnimatedVisibility(
                            visible = visible,
                            enter = fadeIn() + slideInVertically(
                                initialOffsetY = { it / 2 }
                            )
                        ) {
                            Column {
                                LeaderboardListItem(
                                    rank = index + 4,
                                    entry = entry
                                )

                                HorizontalDivider(
                                    modifier = Modifier.padding(vertical = 8.dp),
                                    thickness = 1.dp,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PodiumPosition(
    entry: LeaderboardEntry,
    rank: Int,
    heightMultiplier: Float, // Proportional height
    containerHeight: Dp,
    animate: Boolean,
    modifier: Modifier = Modifier,
    showCrown: Boolean = false
) {
    // Blocks take up about 45% of the section height at max
    val maxBlockHeight = containerHeight * 0.45f
    val targetBlockHeight = maxBlockHeight * heightMultiplier

    // Animate the height rising
    val animatedHeight by animateDpAsState(
        targetValue = if (animate) targetBlockHeight else 0.dp,
        animationSpec = tween(
            durationMillis = 1000,
            delayMillis = when (rank) {
                3 -> 0 // 3rd rises first
                2 -> 200 // then 2nd
                1 -> 400 // finally 1st
                else -> 0
            },
            easing = FastOutSlowInEasing
        )
    )

    // Avatar size relative to container
    val avatarSize = (containerHeight.value * 0.22f).coerceIn(44f, 72f).dp

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Bottom
    ) {
        if (showCrown) {
            Icon(
                imageVector = FeatherIcons.Award,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(20.dp)
            )
            Spacer(Modifier.height(4.dp))
        }

        Box(
            modifier = Modifier
                .size(if (rank == 1) avatarSize else avatarSize * 0.85f)
                .clip(CircleShape)
                .background(if (rank == 1) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = entry.initials,
                style = TextStyle(
                    fontSize = if (rank == 1) 18.sp else 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (rank == 1) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
                    fontFamily = FontFamily.Monospace
                ),
                maxLines = 1,
            )
        }

        Spacer(Modifier.height(8.dp))

        Text(
            text = entry.username,
            style = TextStyle(
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                fontFamily = FontFamily.Monospace
            ),
            maxLines = 1,
            textAlign = TextAlign.Center
        )

        Spacer(Modifier.height(4.dp))

        Row(verticalAlignment = Alignment.Bottom) {
            Text(
                text = entry.wpm.toString(),
                style = TextStyle(
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (rank == 1) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                    fontFamily = FontFamily.Monospace
                ),
            )
            Spacer(Modifier.width(2.dp))
            Text(
                text = "wpm",
                style = TextStyle(
                    fontSize = 9.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontFamily = FontFamily.Monospace
                )
            )
        }

        Spacer(Modifier.height(8.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth(0.85f)
                .height(animatedHeight)
                .background(
                    if (rank == 1) MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                    else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                    RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp)
                )
                .border(
                    width = if (rank == 1) 2.dp else 0.dp,
                    color = if (rank == 1) MaterialTheme.colorScheme.primary else Color.Transparent,
                    shape = RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp)
                ),
            contentAlignment = Alignment.TopCenter
        ) {
            Text(
                text = rank.toString(),
                modifier = Modifier.padding(top = 8.dp),
                style = TextStyle(
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (rank == 1) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                    fontFamily = FontFamily.Monospace
                )
            )
        }
    }
}

@Composable
private fun LeaderboardListItem(rank: Int, entry: LeaderboardEntry) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "#$rank",
            modifier = Modifier.width(36.dp),
            style = TextStyle(
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontFamily = FontFamily.Monospace
            )
        )

        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surfaceVariant),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = entry.initials,
                style = TextStyle(
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontFamily = FontFamily.Monospace
                )
            )
        }

        Spacer(Modifier.width(12.dp))

        Text(
            text = entry.username,
            modifier = Modifier.weight(1f),
            style = TextStyle(
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurface,
                fontFamily = FontFamily.Monospace
            ),
            maxLines = 1
        )

        Column(horizontalAlignment = Alignment.End) {
            Text(
                text = entry.wpm.toString(),
                style = TextStyle(
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontFamily = FontFamily.Monospace
                )
            )
            Text(
                text = "${entry.accuracy}%",
                style = TextStyle(
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontFamily = FontFamily.Monospace
                )
            )
        }
    }
}

fun Modifier.clickableWithoutRipple(onClick: () -> Unit): Modifier = composed {
    this.clickable(
        interactionSource = remember { MutableInteractionSource() },
        indication = null,
        onClick = onClick
    )
}

@Preview
@Composable
private fun LeaderboardScreenPreview() {
    MobileTypistTheme(darkTheme = false) {
        LeaderboardScreen()
    }
}

@Preview
@Composable
private fun LeaderboardScreenPreviewDark() {
    MobileTypistTheme(darkTheme = true) {
        LeaderboardScreen()
    }
}
