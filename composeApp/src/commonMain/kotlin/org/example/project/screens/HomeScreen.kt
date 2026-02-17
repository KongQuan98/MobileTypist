package org.example.project.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import compose.icons.FeatherIcons
import compose.icons.feathericons.Play
import compose.icons.feathericons.Settings
import compose.icons.feathericons.User
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.example.project.data.StorageManager
import org.example.project.data.TypingMode
import org.example.project.data.createSettings
import org.example.project.navigation.NavigationManager
import org.example.project.navigation.Screen
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
        settingsAction = {
            navigationManager.navigateTo(Screen.Settings)
        },
        profileAction = {
            navigationManager.navigateTo(Screen.Statistics)
        }
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HomeScreenContent(
    viewModel: HomeViewModel,
    pagerState: PagerState,
    coroutineScope: CoroutineScope,
    modifier: Modifier = Modifier,
    settingsAction: () -> Unit,
    profileAction: () -> Unit,
) {
    var currentTypingText = viewModel.typingTexts[pagerState.currentPage]

    LaunchedEffect(pagerState.currentPage) {
        currentTypingText = viewModel.typingTexts[pagerState.currentPage]
    }

    Scaffold(
        modifier = modifier,
        containerColor = Color.Transparent,
        topBar = {
            AnimatedVisibility(
                visible = viewModel.showContent,
                enter = slideInVertically(initialOffsetY = { -it }) + fadeIn(),
                exit = slideOutVertically(targetOffsetY = { -it }) + fadeOut()
            ) {
                Box(
                    modifier = Modifier.fillMaxWidth().padding(16.dp)
                ) {
                    IconButton(
                        onClick = { profileAction() },
                        modifier = Modifier.align(Alignment.CenterStart)
                    ) {
                        Icon(
                            imageVector = FeatherIcons.User,
                            contentDescription = "Profile",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    Text(
                        text = "Mobile Typist",
                        modifier = Modifier.align(Alignment.Center),
                        color = MaterialTheme.colorScheme.onSurface,
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                    )

                    IconButton(
                        onClick = { settingsAction() },
                        modifier = Modifier.align(Alignment.CenterEnd)
                    ) {
                        Icon(
                            imageVector = FeatherIcons.Settings,
                            contentDescription = "Settings",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            TypingModeBar(
                isShowContent = viewModel.showContent,
                coroutineScope = coroutineScope,
                pagerState = pagerState
            )

            HorizontalPager(
                state = pagerState,
            ) { page ->
                TypingScreen(
                    mode = viewModel.modes[page],
                    targetText = currentTypingText,
                    timeOptions = if (viewModel.modes[page] == TypingMode.TIME) viewModel.timeOptions else null,
                    wordOptions = if (viewModel.modes[page] == TypingMode.WORDS) viewModel.wordOptions else null,
                    action = {
                        when (it) {
                            is TypingScreenAction.OnTestComplete -> viewModel.onTestComplete(it.result)
                            TypingScreenAction.OnNavigateBack -> {
                                viewModel.onBack()
                            }
                        }
                    },
                    isStarted = !viewModel.showContent
                )
            }

            IconButton(
                modifier = Modifier.padding(16.dp),
                onClick = { viewModel.onStartTapped() }
            ) {
                Icon(
                    FeatherIcons.Play,
                    contentDescription = "Start",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
private fun TypingModeBar(
    isShowContent: Boolean,
    coroutineScope: CoroutineScope,
    pagerState: PagerState,
) {
    AnimatedVisibility(
        visible = isShowContent,
        enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
        exit = slideOutVertically(targetOffsetY = { it }) + fadeOut()
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(
                    1.dp,
                    Alignment.CenterHorizontally
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                ScreenSelectionButton(
                    Modifier.weight(1f),
                    coroutineScope,
                    pagerState,
                    TypingMode.TIME,
                    "Time"
                )
                ScreenSelectionButton(
                    Modifier.weight(1f),
                    coroutineScope,
                    pagerState,
                    TypingMode.WORDS,
                    "Words"
                )
                ScreenSelectionButton(
                    Modifier.weight(1f),
                    coroutineScope,
                    pagerState,
                    TypingMode.QUOTES,
                    "Quotes"
                )

                VerticalDivider(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {},
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Transparent,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.padding(vertical = 8.dp).weight(1f),
                ) {
                    Text(
                        text = "10s",
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.padding(vertical = 4.dp),
                    )
                }

                Button(
                    onClick = {},
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Transparent,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.padding(vertical = 8.dp).weight(1f),
                ) {
                    Text(
                        text = "30s",
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.padding(vertical = 4.dp),
                    )
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
            changeTypingModeTo(
                mode = mode,
                coroutineScope = coroutineScope,
                pagerState = pagerState,
            )
        },
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent,
            contentColor = MaterialTheme.colorScheme.onPrimary
        ),
        shape = RoundedCornerShape(12.dp),
        modifier = modifier
            .padding(vertical = 4.dp),
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.bodySmall.copy(
                fontWeight = FontWeight.Bold
            ),
            color = if (pagerState.currentPage == mode.ordinal) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(vertical = 4.dp),
        )
    }
}

private fun changeTypingModeTo(
    mode: TypingMode,
    coroutineScope: CoroutineScope,
    pagerState: PagerState,
) {
    coroutineScope.launch {
        pagerState.animateScrollToPage(mode.ordinal)
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

    org.example.project.MobileTypistTheme(darkTheme = true) {
        HomeScreenContent(
            viewModel = viewModel,
            pagerState = pagerState,
            coroutineScope = coroutineScope,
            settingsAction = {},
            profileAction = {}
        )
    }
}
