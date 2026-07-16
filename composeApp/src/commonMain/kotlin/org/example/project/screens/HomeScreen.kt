package org.example.project.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.toUpperCase
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import compose.icons.FeatherIcons
import compose.icons.feathericons.Play
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import mobiletypist.composeapp.generated.resources.Res
import mobiletypist.composeapp.generated.resources.app_icon
import mobiletypist.composeapp.generated.resources.app_name
import org.example.project.MobileTypistTheme
import org.example.project.achievements.events.AchievementEvent
import org.example.project.achievements.model.Achievement
import org.example.project.data.model.TypingMode
import org.example.project.data.storage.StorageManager
import org.example.project.navigation.NavigationManager
import org.example.project.ui.AchievementUnlockPopup
import org.example.project.ui.LocalAchievementRepository
import org.example.project.ui.LocalHaptics
import org.example.project.ui.PreviewCompositionLocals
import org.example.project.ui.previewStorageManager
import org.example.project.ui.shimmerEffect
import org.example.project.ui.wrap
import org.example.project.utils.AudioPlayer
import org.example.project.utils.Haptics
import org.example.project.viewModel.HomeViewModel
import org.example.project.viewModel.TypingScreenAction
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HomeScreen(
    audioPlayer: AudioPlayer? = null,
    navigationManager: NavigationManager,
    storageManager: StorageManager,
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()
    val viewModel = remember { HomeViewModel(storageManager, coroutineScope) }
    val pagerState = rememberPagerState(pageCount = { viewModel.modes.size })
    val achievementRepository = LocalAchievementRepository.current

    var unlockedAchievement by remember { mutableStateOf<Achievement?>(null) }

    LaunchedEffect(achievementRepository) {
        achievementRepository.events.collectLatest { event ->
            if (event is AchievementEvent.Unlocked) {
                unlockedAchievement = event.achievement
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        HomeScreenContent(
            viewModel = viewModel,
            pagerState = pagerState,
            coroutineScope = coroutineScope,
            modifier = modifier,
            navigationManager = navigationManager,
            audioPlayer = audioPlayer,
        )

        AchievementUnlockPopup(
            achievement = unlockedAchievement,
            onDismiss = { unlockedAchievement = null }
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HomeScreenContent(
    viewModel: HomeViewModel,
    pagerState: PagerState,
    coroutineScope: CoroutineScope,
    modifier: Modifier = Modifier,
    navigationManager: NavigationManager,
    audioPlayer: AudioPlayer? = null,
) {
    val haptics = LocalHaptics.current

    LaunchedEffect(Unit) {
        viewModel.onHomeScreenVisible()
    }

    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.currentPage }
            .distinctUntilChanged()
            .collect { page ->
                viewModel.refreshTextForMode(viewModel.modes[page])
            }
    }

    LaunchedEffect(viewModel.showContent) {
        if (viewModel.showContent) {
            viewModel.refreshTextForMode(viewModel.modes[pagerState.currentPage])
        }
    }

    // Sync bottom bar visibility with HomeScreen content state
    LaunchedEffect(viewModel.showContent) {
        navigationManager.showBottomBar = viewModel.showContent
    }

    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            TitleSection(showTitleBar = viewModel.showContent)

            TypingModeBar(
                viewModel = viewModel,
                coroutineScope = coroutineScope,
                pagerState = pagerState,
                haptics = haptics,
                audioPlayer = audioPlayer,
            )

            Box(
                modifier = Modifier.weight(1f)
            ) {
                HorizontalPager(
                    state = pagerState,
                    userScrollEnabled = viewModel.showContent,
                ) { page ->
                    val mode = viewModel.modes[page]
                    val textForPage = viewModel.typingTexts.getOrNull(page) ?: ""

                    key(mode, textForPage, viewModel.selectedTime, viewModel.selectedWords) {
                        TypingScreen(
                            mode = mode,
                            targetText = textForPage,
                            timeOptions = listOf(viewModel.selectedTime),
                            wordOptions = listOf(viewModel.selectedWords),
                            action = {
                                when (it) {
                                    is TypingScreenAction.OnTestComplete -> {
                                        viewModel.onTestComplete(it.result)
                                    }

                                    TypingScreenAction.OnNavigateBack -> {
                                        viewModel.onBack()
                                        navigationManager.showBottomBar = true
                                    }
                                }
                            },
                            isStarted = !viewModel.showContent
                        )
                    }
                }

                if (viewModel.showContent) {
                    StartPlayButton(
                        modifier = Modifier.align(Alignment.Center),
                        onClick = {
                            haptics.wrap(
                                audioPlayer = audioPlayer,
                            ) {
                                viewModel.onStartTapped()
                                navigationManager.showBottomBar = false
                            }
                        },
                    )
                }
            }

            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
private fun StartPlayButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
//    val infiniteTransition = rememberInfiniteTransition(label = "playButtonGlow")
//    val glowAlpha by infiniteTransition.animateFloat(
//        initialValue = 0.6f,
//        targetValue = 0.8f,
//        animationSpec = infiniteRepeatable(
//            animation = tween(durationMillis = 1800, easing = FastOutSlowInEasing),
//            repeatMode = RepeatMode.Reverse,
//        ),
//        label = "glowAlpha",
//    )
//    val pulseScale by infiniteTransition.animateFloat(
//        initialValue = 0.95f,
//        targetValue = 1f,
//        animationSpec = infiniteRepeatable(
//            animation = tween(durationMillis = 2200, easing = FastOutSlowInEasing),
//            repeatMode = RepeatMode.Reverse,
//        ),
//        label = "pulseScale",
//    )

    Box(
        modifier = modifier
            .size(180.dp),
//            .alpha(glowAlpha)
//            .scale(pulseScale),
        contentAlignment = Alignment.Center,
    ) {
        Box(
            modifier = Modifier
                .matchParentSize()
                .clip(CircleShape)
                .shimmerEffect(),
        )
        IconButton(
            modifier = Modifier
                .matchParentSize()
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.background.copy(alpha = 0.72f)),
            onClick = onClick,
        ) {
            Icon(
                imageVector = FeatherIcons.Play,
                contentDescription = "Start",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(80.dp),
            )
        }
    }
}

@Composable
private fun TitleSection(
    showTitleBar: Boolean,
) {
    AnimatedVisibility(
        modifier = Modifier
            .padding(top = 40.dp),
        visible = showTitleBar,
        enter = slideInVertically(initialOffsetY = { -it }) + fadeIn(),
        exit = slideOutVertically(targetOffsetY = { -it }) + fadeOut()
    ) {
        // Logo Row
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .border(
                        2.dp,
                        MaterialTheme.colorScheme.primary,
                        RoundedCornerShape(8.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    stringResource(Res.string.app_icon),
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    maxLines = 1,
                    overflow = TextOverflow.MiddleEllipsis,
                    modifier = Modifier
                        .padding(6.dp)
                )
            }
            Spacer(Modifier.width(12.dp))
            Text(
                text = stringResource(Res.string.app_name),
                style = TextStyle(
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontFamily = FontFamily.Monospace
                ),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun TypingModeBar(
    viewModel: HomeViewModel,
    coroutineScope: CoroutineScope,
    pagerState: PagerState,
    haptics: Haptics,
    audioPlayer: AudioPlayer?,
) {
    AnimatedVisibility(
        visible = viewModel.showContent,
        enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
        exit = slideOutVertically(targetOffsetY = { it }) + fadeOut()
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(1.dp, Alignment.CenterHorizontally),
                modifier = Modifier
                    .padding(horizontal = 20.dp)
                    .fillMaxWidth()
            ) {
                viewModel.modes.forEach { mode ->
                    ScreenSelectionButton(
                        modifier = Modifier.weight(1f),
                        coroutineScope = coroutineScope,
                        pagerState = pagerState,
                        mode = mode,
                        title = mode.name.toUpperCase(Locale.current),
                        haptics = haptics,
                        audioPlayer = audioPlayer,
                    )
                }
            }

            HorizontalDivider(modifier = Modifier.fillMaxWidth().padding(horizontal = 32.dp))

            val currentMode = viewModel.modes[pagerState.currentPage]
            if (currentMode == TypingMode.TIME || currentMode == TypingMode.WORDS) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(
                        1.dp, Alignment.CenterHorizontally
                    ),
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 64.dp)
                ) {
                    if (currentMode == TypingMode.TIME) {
                        viewModel.timeOptions.forEach { time ->
                            ChildSelectionButton(
                                modifier = Modifier.weight(1f),
                                title = "${time}s",
                                isSelected = viewModel.selectedTime == time,
                                onClick = {
                                    haptics.wrap(audioPlayer = audioPlayer) {
                                        viewModel.selectedTime = time
                                    }
                                }
                            )
                        }
                    } else if (currentMode == TypingMode.WORDS) {
                        viewModel.wordOptions.forEach { count ->
                            ChildSelectionButton(
                                modifier = Modifier.weight(1f),
                                title = count.toString(),
                                isSelected = viewModel.selectedWords == count,
                                onClick = {
                                    haptics.wrap(audioPlayer) {
                                        viewModel.selectedWords = count
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ScreenSelectionButton(
    modifier: Modifier,
    coroutineScope: CoroutineScope,
    pagerState: PagerState,
    mode: TypingMode,
    title: String,
    haptics: Haptics,
    audioPlayer: AudioPlayer?,
) {
    Button(
        onClick = {
            haptics.wrap(audioPlayer = audioPlayer) {
                coroutineScope.launch {
                    pagerState.animateScrollToPage(mode.ordinal)
                }
            }
        },
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent, contentColor = MaterialTheme.colorScheme.onPrimary
        ),
        shape = RoundedCornerShape(12.dp),
        modifier = modifier.padding(vertical = 4.dp),
    ) {
        Text(
            text = title,
            maxLines = 1,
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
            color = if (pagerState.currentPage == mode.ordinal) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(vertical = 4.dp),
        )
    }
}

@Composable
private fun ChildSelectionButton(
    modifier: Modifier,
    title: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent, contentColor = MaterialTheme.colorScheme.onPrimary
        ),
        shape = RoundedCornerShape(12.dp),
        modifier = modifier.padding(vertical = 4.dp),
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onBackground.copy(
                alpha = 0.6f
            ),
            modifier = Modifier.padding(vertical = 4.dp),
        )
    }
}

@Preview
@Composable
private fun HomeScreenPreview() {
    val coroutineScope = rememberCoroutineScope()
    val storageManager = previewStorageManager()
    val viewModel = HomeViewModel(storageManager, coroutineScope)
    val pagerState = rememberPagerState(pageCount = { viewModel.modes.size })

    PreviewCompositionLocals {
        MobileTypistTheme(darkTheme = false) {
            HomeScreenContent(
                viewModel = viewModel,
                pagerState = pagerState,
                coroutineScope = coroutineScope,
                navigationManager = NavigationManager(),
            )
        }
    }
}

@Preview
@Composable
private fun HomeScreenDarkPreview() {
    val coroutineScope = rememberCoroutineScope()
    val storageManager = previewStorageManager()
    val viewModel = HomeViewModel(storageManager, coroutineScope)
    val pagerState = rememberPagerState(pageCount = { viewModel.modes.size })

    PreviewCompositionLocals {
        MobileTypistTheme(darkTheme = true) {
            HomeScreenContent(
                viewModel = viewModel,
                pagerState = pagerState,
                coroutineScope = coroutineScope,
                navigationManager = NavigationManager(),
            )
        }
    }
}
