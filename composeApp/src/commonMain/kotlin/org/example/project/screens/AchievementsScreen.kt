package org.example.project.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import compose.icons.FeatherIcons
import compose.icons.feathericons.ArrowLeft
import compose.icons.feathericons.Star
import org.example.project.MobileTypistTheme
import org.example.project.achievements.model.Achievement
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun AchievementsScreen(
    achievements: List<Achievement>,
    onBackClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    val unlockedCount = achievements.count { it.unlocked }
    val totalCount = achievements.size

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

            // Header
            Box(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .clickable { onBackClicked() },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = FeatherIcons.ArrowLeft,
                        contentDescription = "Back",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = "back",
                        style = TextStyle(
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 16.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    )
                }

                Text(
                    text = "achievements",
                    modifier = Modifier.align(Alignment.Center),
                    style = TextStyle(
                        color = MaterialTheme.colorScheme.onSurface,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                )
            }

            Spacer(Modifier.height(20.dp))

            HorizontalDivider(
                thickness = 1.dp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
            )

            Spacer(Modifier.height(20.dp))

            // Stats Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "COLLECTION",
                    style = TextStyle(
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontFamily = FontFamily.Monospace,
                        letterSpacing = 1.sp
                    )
                )
                Text(
                    text = "$unlockedCount / $totalCount unlocked",
                    style = TextStyle(
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.primary,
                        fontFamily = FontFamily.Monospace
                    )
                )
            }

            Spacer(Modifier.height(24.dp))

            // Grid
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 40.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(achievements) { achievement ->
                    AchievementGridItem(achievement)
                }
            }
        }
    }
}

@Composable
fun AchievementGridItem(achievement: Achievement) {
    Column(
        modifier = Modifier
            .background(
                MaterialTheme.colorScheme.surface.copy(alpha = 0.5f),
                RoundedCornerShape(12.dp)
            )
            .then(
                if (achievement.unlocked) Modifier.border(
                    width = 1.dp,
                    brush = Brush.verticalGradient(
                        colors = listOf(MaterialTheme.colorScheme.primary, Color.Transparent)
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) else Modifier
            )
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(if (achievement.unlocked) MaterialTheme.colorScheme.primary.copy(alpha = 0.1f) else Color.Transparent),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = achievement.icon,
                contentDescription = null,
                tint = if (achievement.unlocked) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant.copy(
                    alpha = 0.4f
                ),
                modifier = Modifier.size(20.dp)
            )
        }

        Spacer(Modifier.height(12.dp))

        Text(
            text = achievement.title,
            style = TextStyle(
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = if (achievement.unlocked) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant.copy(
                    alpha = 0.5f
                ),
                fontFamily = FontFamily.Monospace
            ),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center
        )

        Spacer(Modifier.height(4.dp))

        Text(
            text = achievement.description,
            style = TextStyle(
                fontSize = 10.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                fontFamily = FontFamily.Monospace
            ),
            textAlign = TextAlign.Center,
            lineHeight = 14.sp,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Preview
@Composable
private fun AchievementsScreenPreview() {
    MobileTypistTheme(darkTheme = false) {
        AchievementsScreen(
            achievements = listOf(
                Achievement(
                    id = "first_game",
                    title = "First Strike",
                    description = "Completed your first typing test",
                    icon = FeatherIcons.Star,
                    hidden = false,
                    progress = 1,
                    target = 1,
                    unlocked = true,
                    unlockedAt = null
                ),
                Achievement(
                    id = "first_game",
                    title = "First Strike",
                    description = "Completed your first typing test",
                    icon = FeatherIcons.Star,
                    hidden = false,
                    progress = 1,
                    target = 1,
                    unlocked = true,
                    unlockedAt = null
                ),
                Achievement(
                    id = "first_game",
                    title = "First Strike",
                    description = "Completed your first typing test",
                    icon = FeatherIcons.Star,
                    hidden = false,
                    progress = 1,
                    target = 1,
                    unlocked = true,
                    unlockedAt = null
                ),
                Achievement(
                    id = "first_game",
                    title = "First Strike",
                    description = "Completed your first typing test",
                    icon = FeatherIcons.Star,
                    hidden = false,
                    progress = 1,
                    target = 1,
                    unlocked = false,
                    unlockedAt = null
                ),
                Achievement(
                    id = "first_game",
                    title = "First Strike",
                    description = "Completed your first typing test",
                    icon = FeatherIcons.Star,
                    hidden = false,
                    progress = 1,
                    target = 1,
                    unlocked = false,
                    unlockedAt = null
                ),
                Achievement(
                    id = "first_game",
                    title = "First Strike",
                    description = "Completed your first typing test",
                    icon = FeatherIcons.Star,
                    hidden = false,
                    progress = 1,
                    target = 1,
                    unlocked = false,
                    unlockedAt = null
                ),
            ),
            onBackClicked = {}
        )
    }
}

@Preview
@Composable
private fun AchievementsScreenDarkPreview() {
    MobileTypistTheme(darkTheme = true) {
        AchievementsScreen(
            achievements = listOf(
                Achievement(
                    id = "first_game",
                    title = "First Strike",
                    description = "Completed your first typing test",
                    icon = FeatherIcons.Star,
                    hidden = false,
                    progress = 1,
                    target = 1,
                    unlocked = true,
                    unlockedAt = null
                ),
                Achievement(
                    id = "first_game",
                    title = "First Strike",
                    description = "Completed your first typing test",
                    icon = FeatherIcons.Star,
                    hidden = false,
                    progress = 1,
                    target = 1,
                    unlocked = true,
                    unlockedAt = null
                ),
                Achievement(
                    id = "first_game",
                    title = "First Strike",
                    description = "Completed your first typing test",
                    icon = FeatherIcons.Star,
                    hidden = false,
                    progress = 1,
                    target = 1,
                    unlocked = true,
                    unlockedAt = null
                ),
                Achievement(
                    id = "first_game",
                    title = "First Strike",
                    description = "Completed your first typing test",
                    icon = FeatherIcons.Star,
                    hidden = false,
                    progress = 1,
                    target = 1,
                    unlocked = false,
                    unlockedAt = null
                ),
                Achievement(
                    id = "first_game",
                    title = "First Strike",
                    description = "Completed your first typing test",
                    icon = FeatherIcons.Star,
                    hidden = false,
                    progress = 1,
                    target = 1,
                    unlocked = false,
                    unlockedAt = null
                ),
                Achievement(
                    id = "first_game",
                    title = "First Strike",
                    description = "Completed your first typing test",
                    icon = FeatherIcons.Star,
                    hidden = false,
                    progress = 1,
                    target = 1,
                    unlocked = false,
                    unlockedAt = null
                ),
            ),
            onBackClicked = {}
        )
    }
}
