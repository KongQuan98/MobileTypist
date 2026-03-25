package org.example.project.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
        ) {
            Spacer(Modifier.height(40.dp))

            Text(
                text = "leaderboard",
                style = TextStyle(
                    fontSize = 32.sp,
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
                                fontSize = 16.sp,
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

            Spacer(Modifier.height(40.dp))

            // Podium Section
            if (entries.size >= 3) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.Bottom
                ) {
                    // 2nd Place
                    PodiumPosition(
                        entry = entries[1],
                        rank = 2,
                        height = 100.dp,
                        modifier = Modifier.weight(1f)
                    )

                    // 1st Place
                    PodiumPosition(
                        entry = entries[0],
                        rank = 1,
                        height = 160.dp, // Significantly taller than 2nd and 3rd
                        modifier = Modifier.weight(1.2f),
                        showCrown = true
                    )

                    // 3rd Place
                    PodiumPosition(
                        entry = entries[2],
                        rank = 3,
                        height = 80.dp,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Spacer(Modifier.height(40.dp))

            // Remaining List
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                itemsIndexed(entries.drop(3)) { index, entry ->
                    LeaderboardListItem(rank = index + 4, entry = entry)
                }
            }
        }
    }
}

@Composable
private fun PodiumPosition(
    entry: LeaderboardEntry,
    rank: Int,
    height: androidx.compose.ui.unit.Dp,
    modifier: Modifier = Modifier,
    showCrown: Boolean = false
) {
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
                modifier = Modifier.size(24.dp)
            )
            Spacer(Modifier.height(8.dp))
        }

        Box(
            modifier = Modifier
                .size(if (rank == 1) 70.dp else 60.dp)
                .clip(CircleShape)
                .background(if (rank == 1) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = entry.initials,
                style = TextStyle(
                    fontSize = if (rank == 1) 24.sp else 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (rank == 1) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
                    fontFamily = FontFamily.Monospace
                )
            )
        }

        Spacer(Modifier.height(12.dp))

        Text(
            text = entry.username,
            style = TextStyle(
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                fontFamily = FontFamily.Monospace
            )
        )

        Row(verticalAlignment = Alignment.Bottom) {
            Text(
                text = entry.wpm.toString(),
                style = TextStyle(
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (rank == 1) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                    fontFamily = FontFamily.Monospace
                )
            )
            Spacer(Modifier.width(4.dp))
            Text(
                text = "wpm",
                style = TextStyle(
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontFamily = FontFamily.Monospace
                )
            )
        }

        Spacer(Modifier.height(8.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .height(height)
                .background(
                    if (rank == 1) MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
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
                    fontSize = if (rank == 1) 32.sp else 24.sp,
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
            modifier = Modifier.width(40.dp),
            style = TextStyle(
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontFamily = FontFamily.Monospace
            )
        )

        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surfaceVariant),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = entry.initials,
                style = TextStyle(
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontFamily = FontFamily.Monospace
                )
            )
        }

        Spacer(Modifier.width(16.dp))

        Text(
            text = entry.username,
            modifier = Modifier.weight(1f),
            style = TextStyle(
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onSurface,
                fontFamily = FontFamily.Monospace
            )
        )

        Column(horizontalAlignment = Alignment.End) {
            Text(
                text = entry.wpm.toString(),
                style = TextStyle(
                    fontSize = 16.sp,
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
