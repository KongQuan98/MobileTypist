package org.example.project.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import org.example.project.MobileTypistTheme
import org.example.project.navigation.NavigationManager
import org.jetbrains.compose.ui.tooling.preview.Preview

data class LeaderboardEntry(
    val username: String,
    val wpm: Int,
    val accuracy: Int,
    val initials: String
)

@Composable
fun LeaderboardScreen(
    navigationManager: NavigationManager,
    modifier: Modifier = Modifier
) {
    val tabs = listOf("15s", "30s", "60s", "all")
    var selectedTab by remember { mutableStateOf(1) } // 30s selected by default

    // Mock data based on screenshot
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

            // Proportional sizing
            val titleSize = (screenWidth.value * 0.08f).coerceIn(24f, 32f).sp
            val podiumSectionHeight = screenHeight * 0.35f
            val listSectionHeight = screenHeight * 0.45f

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp)
            ) {
                Spacer(Modifier.height(40.dp))

                Text(
                    text = "leaderboard",
                    style = TextStyle(
                        fontSize = titleSize,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontFamily = FontFamily.Monospace
                    )
                )

                Spacer(Modifier.height(16.dp))

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
                            if (selectedTab == index) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth(0.6f)
                                        .height(2.dp)
                                        .background(MaterialTheme.colorScheme.primary)
                                )
                            } else {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(1.dp)
                                        .background(
                                            MaterialTheme.colorScheme.onSurfaceVariant.copy(
                                                alpha = 0.2f
                                            )
                                        )
                                )
                            }
                        }
                    }
                }

                Spacer(Modifier.height(24.dp))

                // Podium Section - Responsive
                if (entries.size >= 3) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(podiumSectionHeight),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        // 2nd Place
                        PodiumPosition(
                            entry = entries[1],
                            rank = 2,
                            relativeHeight = 0.6f,
                            containerHeight = podiumSectionHeight,
                            modifier = Modifier.weight(1f)
                        )

                        // 1st Place
                        PodiumPosition(
                            entry = entries[0],
                            rank = 1,
                            relativeHeight = 0.9f,
                            containerHeight = podiumSectionHeight,
                            modifier = Modifier.weight(1.2f),
                            showCrown = true
                        )

                        // 3rd Place
                        PodiumPosition(
                            entry = entries[2],
                            rank = 3,
                            relativeHeight = 0.5f,
                            containerHeight = podiumSectionHeight,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                Spacer(Modifier.height(24.dp))

                // Remaining List
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f), // Take remaining space
                    contentPadding = PaddingValues(bottom = 24.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    itemsIndexed(entries.drop(3)) { index, entry ->
                        LeaderboardListItem(rank = index + 4, entry = entry)
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
    relativeHeight: Float, // 0.0 to 1.0
    containerHeight: Dp,
    modifier: Modifier = Modifier,
    showCrown: Boolean = false
) {
    val blockHeight = containerHeight * 0.5f * relativeHeight
    val avatarSize = (containerHeight.value * 0.25f).coerceIn(40f, 70f).dp

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
                modifier = Modifier.size((avatarSize.value * 0.4f).dp)
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
                    fontSize = (avatarSize.value * 0.35f).sp,
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
                .height(blockHeight)
                .background(
                    if (rank == 1) MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
                    else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                    RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp)
                )
                .border(
                    width = if (rank == 1) 2.dp else 0.dp,
                    color = if (rank == 1) MaterialTheme.colorScheme.primary else Color.Transparent,
                    shape = RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = rank.toString(),
                style = TextStyle(
                    fontSize = (blockHeight.value * 0.4f).coerceAtLeast(16f).sp,
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
            .padding(vertical = 2.dp),
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

// Helper to disable ripple
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
        LeaderboardScreen(
            navigationManager = NavigationManager()
        )
    }
}

@Preview
@Composable
private fun LeaderboardScreenPreviewDark() {
    MobileTypistTheme(darkTheme = true) {
        LeaderboardScreen(
            navigationManager = NavigationManager()
        )
    }
}
