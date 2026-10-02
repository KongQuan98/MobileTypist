package org.example.project.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import compose.icons.FeatherIcons
import compose.icons.feathericons.Edit3
import compose.icons.feathericons.LogIn
import compose.icons.feathericons.LogOut
import mobiletypist.composeapp.generated.resources.Res
import mobiletypist.composeapp.generated.resources.profile_achievements
import mobiletypist.composeapp.generated.resources.profile_avg_wpm
import mobiletypist.composeapp.generated.resources.profile_best
import mobiletypist.composeapp.generated.resources.profile_edit_profile
import mobiletypist.composeapp.generated.resources.profile_global_accuracy
import mobiletypist.composeapp.generated.resources.profile_guest_label
import mobiletypist.composeapp.generated.resources.profile_keep_practicing
import mobiletypist.composeapp.generated.resources.profile_login_button
import mobiletypist.composeapp.generated.resources.profile_login_card_subtitle
import mobiletypist.composeapp.generated.resources.profile_login_card_title
import mobiletypist.composeapp.generated.resources.profile_member_since
import mobiletypist.composeapp.generated.resources.profile_no_tests
import mobiletypist.composeapp.generated.resources.profile_recent_tests
import mobiletypist.composeapp.generated.resources.profile_tests
import mobiletypist.composeapp.generated.resources.profile_unlocked_label
import mobiletypist.composeapp.generated.resources.profile_view_less
import mobiletypist.composeapp.generated.resources.profile_view_more_achievements
import mobiletypist.composeapp.generated.resources.profile_view_more_tests
import mobiletypist.composeapp.generated.resources.wpm
import org.example.project.achievements.model.Achievement
import org.example.project.data.model.TypingTestResult
import org.example.project.data.repo.AvatarRepository
import org.example.project.ui.TooltipHint
import org.example.project.utils.formatDate
import org.example.project.utils.hapticClickable
import org.example.project.viewModel.ProfileUiState
import org.example.project.viewModel.ProfileViewModel
import org.jetbrains.compose.resources.stringResource

@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel,
    authViewModel: org.example.project.auth.AuthViewModel,
    onEditProfileClicked: () -> Unit,
    onViewMoreAchievements: () -> Unit,
    onLoginClicked: () -> Unit = {},
    onLogoutClicked: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.refreshData()
    }

    ProfileScreenContent(
        uiState = uiState,
        onEditProfileClicked = onEditProfileClicked,
        onViewMoreAchievements = onViewMoreAchievements,
        onLoginClicked = onLoginClicked,
        onLogoutClicked = {
            authViewModel.signOut()
            onLogoutClicked()
        },
        modifier = modifier
    )
}

@Composable
fun ProfileScreenContent(
    uiState: ProfileUiState,
    onEditProfileClicked: () -> Unit,
    onViewMoreAchievements: () -> Unit,
    onLoginClicked: () -> Unit = {},
    onLogoutClicked: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    val results = uiState.recentTestResult
    val bestWpm = uiState.bestWpm
    val totalTests = uiState.totalTests
    val achievements = uiState.achievements
    val userProfile = uiState.userProfile

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

    var isRecentTestsExpanded by remember { mutableStateOf(false) }

    val editProfileInteractionSource = remember { MutableInteractionSource() }
    val viewMoreAchievementInteractionSource = remember { MutableInteractionSource() }

    val isEditProfilePressed by editProfileInteractionSource.collectIsPressedAsState()
    val isViewMoreAchievementPressed by viewMoreAchievementInteractionSource.collectIsPressedAsState()

    val editProfileBorderColor by animateColorAsState(
        targetValue = if (isEditProfilePressed) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant.copy(
            alpha = 0.3f
        ),
        animationSpec = tween(durationMillis = 150)
    )
    val viewMoreAchievementBorderColor by animateColorAsState(
        targetValue = if (isViewMoreAchievementPressed) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant.copy(
            alpha = 0.3f
        ),
        animationSpec = tween(durationMillis = 150)
    )

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
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .border(2.dp, MaterialTheme.colorScheme.primary, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    val avatar = remember(userProfile.avatarId) {
                        AvatarRepository.getAvatarById(userProfile.avatarId)
                    }
                    Icon(
                        imageVector = avatar.icon,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.size(50.dp)
                    )

                    // Logged in indicator
                    if (userProfile.isLoggedIn) {
                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .size(12.dp)
                                .background(Color(0xFF4CAF50), CircleShape)
                                .border(1.dp, MaterialTheme.colorScheme.background, CircleShape)
                        )
                    }
                }

                Spacer(Modifier.height(16.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = userProfile.username,
                        style = TextStyle(
                            fontSize = 24.sp,
                            fontWeight = FontWeight.ExtraBold, // Duolingo style
                            color = MaterialTheme.colorScheme.onSurface,
                            fontFamily = FontFamily.Monospace
                        )
                    )
                    if (userProfile.isGuest) {
                        Spacer(Modifier.width(8.dp))
                        Surface(
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text(
                                text = stringResource(Res.string.profile_guest_label),
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp),
                                style = TextStyle(
                                    fontSize = 8.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontFamily = FontFamily.Monospace
                                )
                            )
                        }
                    }
                }

                Spacer(Modifier.height(8.dp))

                Text(
                    text = stringResource(Res.string.profile_member_since),
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
                        text = stringResource(Res.string.profile_edit_profile),
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
                // Login Card for Guests
                if (userProfile.isGuest) {
                    item {
                        LoginPromptCard(onLoginClicked)
                        Spacer(Modifier.height(32.dp))
                    }
                } else {
                    item {
                        Spacer(Modifier.height(16.dp))
                        Button(
                            onClick = onLogoutClicked,
                            modifier = Modifier.fillMaxWidth().height(56.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.errorContainer,
                                contentColor = MaterialTheme.colorScheme.onErrorContainer
                            )
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = FeatherIcons.LogOut,
                                    contentDescription = null,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(Modifier.width(12.dp))
                                Text(
                                    text = "Sign Out",
                                    style = TextStyle(
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = FontFamily.Monospace
                                    )
                                )
                            }
                        }
                        Spacer(Modifier.height(32.dp))
                    }
                }

                // Summary Stats Cards
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        StatBox(
                            label = stringResource(Res.string.profile_avg_wpm),
                            value = averageWpm.toString(),
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.weight(1f)
                        )
                        StatBox(
                            label = stringResource(Res.string.profile_best),
                            value = bestWpm.toString(),
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.weight(1f)
                        )
                        StatBox(
                            label = stringResource(Res.string.profile_tests),
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
                                text = stringResource(Res.string.profile_achievements),
                                style = TextStyle(
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontFamily = FontFamily.Monospace,
                                    letterSpacing = 1.sp
                                )
                            )
                            val unlockedCount = achievements.count { it.unlocked }
                            Text(
                                text = stringResource(
                                    Res.string.profile_unlocked_label,
                                    unlockedCount,
                                    achievements.size
                                ),
                                style = TextStyle(
                                    fontSize = 14.sp,
                                    color = MaterialTheme.colorScheme.primary,
                                    fontFamily = FontFamily.Monospace
                                )
                            )
                        }

                        Spacer(Modifier.height(16.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            val unlockedShowcase = achievements.filter { it.unlocked }.take(3)
                            unlockedShowcase.forEach { achievement ->
                                AchievementCard(
                                    achievement = achievement,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                            repeat(3 - unlockedShowcase.size) {
                                Box(modifier = Modifier.weight(1f))
                            }
                        }

                        if (achievements.none { it.unlocked }) {
                            Text(
                                text = stringResource(Res.string.profile_keep_practicing),
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis,
                                style = TextStyle(
                                    fontSize = 14.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontFamily = FontFamily.Monospace,
                                    textAlign = TextAlign.Center,
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 24.dp),
                            )
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
                                text = stringResource(Res.string.profile_view_more_achievements),
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
                            text = stringResource(Res.string.profile_recent_tests, results.size),
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
                            text = stringResource(Res.string.profile_no_tests),
                            style = TextStyle(
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontFamily = FontFamily.Monospace
                            ),
                            modifier = Modifier.padding(top = 20.dp)
                        )
                    }
                } else {
                    val displayResults = if (isRecentTestsExpanded) results else results.take(3)
                    itemsIndexed(displayResults) { _, result ->
                        AnimatedVisibility(
                            visible = true,
                            enter = fadeIn() + slideInVertically(
                                initialOffsetY = { it / 2 }
                            )
                        ) {
                            ProfileResultItem(result)
                            Spacer(Modifier.height(8.dp))
                        }
                    }

                    if (results.size > 3) {
                        item(key = "view_more_less_tests_toggle") {
                            val viewMoreTestInteractionSource =
                                remember { MutableInteractionSource() }
                            val isPressed by viewMoreTestInteractionSource.collectIsPressedAsState()
                            val animatedBorderColor by animateColorAsState(
                                targetValue = if (isPressed) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant.copy(
                                    alpha = 0.3f
                                ),
                                animationSpec = tween(durationMillis = 150)
                            )

                            Spacer(Modifier.height(16.dp))
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .border(
                                        1.dp,
                                        animatedBorderColor,
                                        RoundedCornerShape(8.dp)
                                    )
                                    .clip(RoundedCornerShape(8.dp))
                                    .hapticClickable(
                                        interactionSource = viewMoreTestInteractionSource,
                                        onClick = {
                                            isRecentTestsExpanded = !isRecentTestsExpanded
                                        }
                                    ),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = if (isRecentTestsExpanded) stringResource(Res.string.profile_view_less) else stringResource(
                                        Res.string.profile_view_more_tests
                                    ),
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
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun LoginPromptCard(onLoginClicked: () -> Unit) {
    Surface(
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            MaterialTheme.colorScheme.outline.copy(alpha = 0.1f)
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(Res.string.profile_login_card_title),
                style = TextStyle(
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontFamily = FontFamily.Monospace
                ),
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = stringResource(Res.string.profile_login_card_subtitle),
                style = TextStyle(
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontFamily = FontFamily.Monospace
                ),
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(20.dp))
            Button(
                onClick = onLoginClicked,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = FeatherIcons.LogIn,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(Modifier.width(12.dp))
                    Text(
                        text = stringResource(Res.string.profile_login_button),
                        style = TextStyle(
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                    )
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
            .fillMaxWidth()
            .height(150.dp)
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
                .size(64.dp)
                .clip(CircleShape)
                .background(if (achievement.unlocked) MaterialTheme.colorScheme.primary.copy(alpha = 0.1f) else Color.Transparent),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = achievement.icon,
                contentDescription = null,
                tint = if (achievement.unlocked) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                modifier = Modifier.size(20.dp)
            )
        }

        Spacer(Modifier.height(12.dp))

        TooltipHint(hint = stringResource(achievement.title)) {
            Text(
                text = stringResource(achievement.title),
                style = TextStyle(
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (achievement.unlocked) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.surfaceVariant,
                    fontFamily = FontFamily.Monospace
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center
            )
        }

        Spacer(Modifier.height(4.dp))

        TooltipHint(hint = stringResource(achievement.description)) {
            Text(
                text = stringResource(achievement.description),
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
                color = MaterialTheme.colorScheme.onSurfaceVariant,
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
            text = stringResource(Res.string.profile_global_accuracy),
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
                    text = stringResource(Res.string.wpm),
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
