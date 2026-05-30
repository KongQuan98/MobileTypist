package org.example.project.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
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
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import compose.icons.FeatherIcons
import compose.icons.feathericons.Edit3
import kotlinx.coroutines.delay
import org.example.project.MobileTypistTheme
import org.example.project.data.model.Achievement
import org.example.project.data.model.AchievementRepository
import org.example.project.data.model.TypingMode
import org.example.project.data.model.TypingTestResult
import org.example.project.data.model.UserProfile
import org.example.project.ui.LocalAudioPlayer
import org.example.project.ui.LocalHaptics
import org.example.project.ui.hapticClickable
import org.example.project.utils.formatDate
import org.jetbrains.compose.ui.tooling.preview.Preview

data class ProfileScreenState(
    val userProfile: UserProfile,
    val recentTestResult: List<TypingTestResult> = emptyList(),
    val averageWpm: Int = 0,
    val bestWpm: Int = 0,
    val totalTests: Int = 0
)

@Composable
fun ProfileScreen(
    profileScreenState: ProfileScreenState,
    onEditProfileClicked: () -> Unit,
    onViewMoreAchievements: () -> Unit,
    modifier: Modifier = Modifier,
    refreshData: () -> Unit = {},
) {
    val results = profileScreenState.recentTestResult
    val bestWpm = profileScreenState.bestWpm
    val totalTests = profileScreenState.totalTests

    val averageWpm = if (results.isNotEmpty()) {
        results.map { it.wpm }.average().toInt()
    } else {
        0
    }

    val averageAccuracy = if (results.isNotEmpty()) {
        results.map { it.accuracy }.average().toInt()
    } else {
        0
    }

    val editProfileInteractionSource = remember { MutableInteractionSource() }
    val viewMoreAchievementInteractionSource = remember { MutableInteractionSource() }
    val isEditProfilePressed by editProfileInteractionSource.collectIsPressedAsState()
    val isViewMoreAchievementPressed by viewMoreAchievementInteractionSource.collectIsPressedAsState()

    val editProfileBorderColor =
        if (isEditProfilePressed) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant.copy(
            alpha = 0.3f
        )
    val viewMoreAchievementBorderColor =
        if (isViewMoreAchievementPressed) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant.copy(
            alpha = 0.3f
        )

    var startAnimation by remember { mutableStateOf(false) }
    LaunchedEffect(startAnimation) {
        startAnimation = true
    }

    LaunchedEffect(Unit) {
        refreshData.invoke()
    }
    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .padding(horizontal = 24.dp)
                .padding(top = 40.dp)
        ) {
            // Profile Header Section
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (profileScreenState.userProfile.username.length >= 2)
                            profileScreenState.userProfile.username.take(2).uppercase()
                        else "??",
                        style = TextStyle(
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.background,
                            fontFamily = FontFamily.Monospace
                        )
                    )
                }

                Spacer(Modifier.height(16.dp))

                Text(
                    text = profileScreenState.userProfile.displayName,
                    style = TextStyle(
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontFamily = FontFamily.Monospace
                    )
                )

                Spacer(Modifier.height(8.dp))

                Text(
                    text = "member since jan 2024",
                    style = TextStyle(
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontFamily = FontFamily.Monospace
                    ),
                )

                Spacer(Modifier.height(16.dp))

                // Edit Profile Button
                Row(
                    modifier = Modifier
                        .border(
                            1.dp,
                            editProfileBorderColor,
                            RoundedCornerShape(8.dp)
                        )
                        .clip(RoundedCornerShape(8.dp))
                        .hapticClickable(
                            interactionSource = editProfileInteractionSource,
                            onClick = onEditProfileClicked
                        ),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Spacer(Modifier.width(16.dp))
                    Icon(
                        imageVector = FeatherIcons.Edit3,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier
                            .size(14.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = "edit profile",
                        style = TextStyle(
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurface,
                            fontFamily = FontFamily.Monospace
                        ),
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                    Spacer(Modifier.width(16.dp))
                }


                Spacer(Modifier.height(20.dp))

                HorizontalDivider(
                    thickness = 1.dp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
                )
            }

            LazyColumn(
                modifier = Modifier
                    .padding(top = 20.dp)
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                contentPadding = PaddingValues(bottom = 32.dp)
            ) {
                // Summary Stats Cards
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        StatBox(
                            label = "avg wpm",
                            value = averageWpm.toString(),
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.weight(1f)
                        )
                        StatBox(
                            label = "best",
                            value = bestWpm.toString(),
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.weight(1f)
                        )
                        StatBox(
                            label = "tests",
                            value = totalTests.toString(),
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(Modifier.height(32.dp))
                }

                // Global Accuracy
                item {
                    val animatedAverageAccuracy by animateFloatAsState(
                        targetValue = if (startAnimation) averageAccuracy.toFloat() else 0f,
                        animationSpec = tween(
                            durationMillis = 1000,
                            delayMillis = 0,
                            easing = FastOutSlowInEasing
                        )
                    )
                    GlobalAccuracyBar(animatedAverageAccuracy)
                    Spacer(Modifier.height(40.dp))
                }

                // Achievements Section
                item {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "achievements",
                                style = TextStyle(
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontFamily = FontFamily.Monospace,
                                    letterSpacing = 1.sp
                                )
                            )
                            val unlockedCount =
                                AchievementRepository.achievements.count { it.isUnlocked }
                            Text(
                                text = "$unlockedCount / ${AchievementRepository.achievements.size} unlocked",
                                style = TextStyle(
                                    fontSize = 14.sp,
                                    color = MaterialTheme.colorScheme.primary,
                                    fontFamily = FontFamily.Monospace
                                )
                            )
                        }

                        Spacer(Modifier.height(16.dp))

                        // Showcase top 3 achievements
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            AchievementRepository.achievements.filter { it.isUnlocked }.take(3)
                                .forEach { achievement ->
                                    AchievementCard(
                                        achievement = achievement,
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                        }

                        Spacer(Modifier.height(24.dp))

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(
                                    1.dp,
                                    viewMoreAchievementBorderColor,
                                    RoundedCornerShape(8.dp)
                                )
                                .clip(RoundedCornerShape(8.dp))
                                .hapticClickable(
                                    interactionSource = viewMoreAchievementInteractionSource,
                                    onClick = onViewMoreAchievements
                                ),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "view more achievements",
                                style = TextStyle(
                                    fontSize = 14.sp,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    fontFamily = FontFamily.Monospace,
                                    textAlign = TextAlign.Center,
                                ),
                                modifier = Modifier
                                    .padding(vertical = 8.dp)
                                    .fillMaxWidth(),
                            )
                        }

                        Spacer(Modifier.height(40.dp))
                    }
                }

                // Recent Tests Section
                item {
                    Box(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "recent tests (${results.size})",
                            style = TextStyle(
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontFamily = FontFamily.Monospace,
                                letterSpacing = 1.sp
                            )
                        )
                    }
                    Spacer(Modifier.height(16.dp))
                }

                if (results.isEmpty()) {
                    item {
                        Text(
                            text = "no tests completed yet",
                            style = TextStyle(
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontFamily = FontFamily.Monospace
                            ),
                            modifier = Modifier.padding(top = 20.dp)
                        )
                    }
                } else {
                    itemsIndexed(results) { index, result ->
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
                            ProfileResultItem(result)
                            Spacer(Modifier.height(8.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AchievementCard(
    achievement: Achievement,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .background(
                MaterialTheme.colorScheme.surface.copy(alpha = 0.5f),
                RoundedCornerShape(12.dp)
            )
            .then(
                if (achievement.isUnlocked) Modifier.border(
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
                .background(if (achievement.isUnlocked) MaterialTheme.colorScheme.primary.copy(alpha = 0.1f) else Color.Transparent),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = achievement.icon,
                contentDescription = null,
                tint = if (achievement.isUnlocked) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                modifier = Modifier.size(20.dp)
            )
        }

        Spacer(Modifier.height(12.dp))

        Text(
            text = achievement.title,
            style = TextStyle(
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = if (achievement.isUnlocked) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.surfaceVariant,
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
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun StatBox(
    label: String,
    value: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    val scale = remember { Animatable(1f) }

    LaunchedEffect(value) {
        scale.animateTo(
            targetValue = 1.3f,
            animationSpec = tween(200)
        )
        scale.animateTo(
            targetValue = 1f,
            animationSpec = tween(200)
        )
    }

    Column(
        modifier = modifier
            .background(
                MaterialTheme.colorScheme.surface.copy(alpha = 0.5f),
                RoundedCornerShape(12.dp)
            )
            .padding(vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = label,
            style = TextStyle(
                fontSize = 11.sp,
                color = Color(0xFF646669),
                fontFamily = FontFamily.Monospace
            )
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = value,
            style = TextStyle(
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = color,
                fontFamily = FontFamily.Monospace
            ),
            modifier = Modifier.graphicsLayer {
                scaleX = scale.value
                scaleY = scale.value
            }
        )
    }
}

@Composable
private fun GlobalAccuracyBar(
    averageAccuracy: Float,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = "global accuracy",
            style = TextStyle(
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontFamily = FontFamily.Monospace
            )
        )

        Spacer(Modifier.height(16.dp))

        Text(
            text = "${averageAccuracy.toInt()}%",
            style = TextStyle(
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace
            )
        )
    }
    Spacer(Modifier.height(8.dp))
    LinearProgressIndicator(
        progress = { averageAccuracy / 100f },
        modifier = Modifier
            .fillMaxWidth()
            .height(8.dp)
            .clip(RoundedCornerShape(4.dp)),
        color = MaterialTheme.colorScheme.primary,
        trackColor = MaterialTheme.colorScheme.background
    )
}

@Composable
private fun ProfileResultItem(
    result: TypingTestResult,
) {
    val dateString = formatDate(result.timestamp)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background, RoundedCornerShape(8.dp))
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Mode box (e.g., 30s)
        Box(
            modifier = Modifier
                .border(
                    1.dp,
                    MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
                    RoundedCornerShape(4.dp)
                )
                .padding(horizontal = 6.dp, vertical = 4.dp)
        ) {
            Text(
                text = result.mode.name.take(3).lowercase(),
                style = TextStyle(
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.primary,
                    fontFamily = FontFamily.Monospace
                )
            )
        }

        Spacer(Modifier.width(16.dp))

        // WPM and Time Ago
        Column(modifier = Modifier.weight(1f)) {
            Row(
                modifier = Modifier.padding(bottom = 4.dp),
                verticalAlignment = Alignment.Bottom
            ) {
                Text(
                    text = result.wpm.toString(),
                    style = TextStyle(
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontFamily = FontFamily.Monospace
                    )
                )
                Spacer(Modifier.width(4.dp))
                Text(
                    text = "wpm",
                    style = TextStyle(
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.primary,
                        fontFamily = FontFamily.Monospace
                    )
                )
            }
            Text(
                text = dateString,
                style = TextStyle(
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.primary,
                    fontFamily = FontFamily.Monospace
                ),
            )
        }

        // Accuracy and Dot
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "${result.accuracy}%",
                style = TextStyle(
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontFamily = FontFamily.Monospace
                )
            )
            Spacer(Modifier.width(8.dp))
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary)
            )
        }
    }
}

@Preview
@Composable
private fun ProfileScreenPreviewDark() {
    val dummyProfileScreenState = ProfileScreenState(
        userProfile = UserProfile(),
        recentTestResult = listOf(
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
                accuracy = 90,
                timestamp = 1000L,
                correctChars = 0,
                errorCount = 0,
                duration = 0,
            )
        ),
        bestWpm = 50,
        totalTests = 25,
    )

    CompositionLocalProvider(
        LocalHaptics provides PreviewHaptics,
        LocalAudioPlayer provides PreviewAudioPlayer,
    ) {
        MobileTypistTheme(darkTheme = true) {
            ProfileScreen(
                onEditProfileClicked = {},
                onViewMoreAchievements = {},
                profileScreenState = dummyProfileScreenState,
            )
        }
    }
}

@Preview
@Composable
private fun ProfileScreenPreview() {
    val dummyProfileScreenState = ProfileScreenState(
        userProfile = UserProfile(),
        recentTestResult = listOf(
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
                accuracy = 90,
                timestamp = 1000L,
                correctChars = 0,
                errorCount = 0,
                duration = 0,
            )
        ),
        bestWpm = 50,
        totalTests = 25,
    )

    CompositionLocalProvider(
        LocalHaptics provides PreviewHaptics,
        LocalAudioPlayer provides PreviewAudioPlayer,
    ) {
        MobileTypistTheme(darkTheme = false) {
            ProfileScreen(
                onEditProfileClicked = {},
                onViewMoreAchievements = {},
                profileScreenState = dummyProfileScreenState,
            )
        }
    }
}
