package org.example.project.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.toUpperCase
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import compose.icons.FeatherIcons
import compose.icons.feathericons.Play
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.example.project.MobileTypistTheme
import org.example.project.data.StorageManager
import org.example.project.data.createSettings
import org.example.project.data.model.TypingMode
import org.example.project.navigation.NavigationManager
import org.example.project.viewModel.HomeViewModel
import org.example.project.viewModel.TypingScreenAction
import org.jetbrains.compose.ui.tooling.preview.Preview

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HomeScreen(
    navigationManager: NavigationManager,
    storageManager: StorageManager,
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()
    val viewModel = remember { HomeViewModel(storageManager, coroutineScope) }
    val pagerState = rememberPagerState(pageCount = { viewModel.modes.size })

    HomeScreenContent(
        viewModel = viewModel,
        pagerState = pagerState,
        coroutineScope = coroutineScope,
        modifier = modifier,
        navigationManager = navigationManager,
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HomeScreenContent(
    viewModel: HomeViewModel,
    pagerState: PagerState,
    coroutineScope: CoroutineScope,
    modifier: Modifier = Modifier,
    navigationManager: NavigationManager,
) {
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
                viewModel = viewModel, coroutineScope = coroutineScope, pagerState = pagerState
            )

            Box(
                modifier = Modifier.weight(1f)
            ) {
                HorizontalPager(
                    state = pagerState,
                    userScrollEnabled = viewModel.showContent,
                ) { page ->
                    val textForPage = viewModel.typingTexts.getOrNull(page) ?: ""

                    TypingScreen(
                        mode = viewModel.modes[page],
                        targetText = textForPage,
                        timeOptions = if (viewModel.modes[page] == TypingMode.TIME) listOf(viewModel.selectedTime) else null,
                        wordOptions = if (viewModel.modes[page] == TypingMode.WORDS) listOf(
                            viewModel.selectedWords
                        ) else null,
                        action = {
                            when (it) {
                                is TypingScreenAction.OnTestComplete -> {
                                    viewModel.onTestComplete(it.result)
                                    // Bottom bar stays hidden during result screen (which is full screen)
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

                if (viewModel.showContent) {
                    IconButton(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .clip(CircleShape)
                            .size(180.dp)
                            .background(MaterialTheme.colorScheme.background.copy(alpha = 0.7f)),
                        onClick = {
                            viewModel.onStartTapped()
                            navigationManager.showBottomBar = false
                        },
                    ) {
                        Icon(
                            FeatherIcons.Play,
                            contentDescription = "Start",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier
                                .size(80.dp)
                                .align(Alignment.Center)
                        )
                    }
                }
            }
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
        Text(
            text = "mobile typist",
            style = TextStyle(
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                fontFamily = FontFamily.Monospace
            )
        )
    }
}

@Composable
private fun TypingModeBar(
    viewModel: HomeViewModel,
    coroutineScope: CoroutineScope,
    pagerState: PagerState,
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
                                onClick = { viewModel.selectedTime = time })
                        }
                    } else if (currentMode == TypingMode.WORDS) {
                        viewModel.wordOptions.forEach { count ->
                            ChildSelectionButton(
                                modifier = Modifier.weight(1f),
                                title = count.toString(),
                                isSelected = viewModel.selectedWords == count,
                                onClick = { viewModel.selectedWords = count })
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
) {
    Button(
        onClick = {
            coroutineScope.launch {
                pagerState.animateScrollToPage(mode.ordinal)
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
    modifier: Modifier, title: String, isSelected: Boolean, onClick: () -> Unit
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

@OptIn(ExperimentalFoundationApi::class)
@Preview
@Composable
private fun HomeScreenPreview() {
    val coroutineScope = rememberCoroutineScope()
    val storageManager = StorageManager(settings = createSettings())
    val viewModel = HomeViewModel(storageManager, coroutineScope)
    val pagerState = rememberPagerState(pageCount = { viewModel.modes.size })

    MobileTypistTheme(darkTheme = true) {
        HomeScreenContent(
            viewModel = viewModel,
            pagerState = pagerState,
            coroutineScope = coroutineScope,
            navigationManager = NavigationManager(),
        )
    }
}
