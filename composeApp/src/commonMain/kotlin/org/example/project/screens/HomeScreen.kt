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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import compose.icons.FeatherIcons
import compose.icons.feathericons.ArrowLeft
import compose.icons.feathericons.ArrowRight
import compose.icons.feathericons.Settings
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.example.project.ThemeColors
import org.example.project.data.StorageManager
import org.example.project.data.TypingMode
import org.example.project.data.createSettings
import org.example.project.navigation.NavigationManager
import org.example.project.navigation.Screen
import org.example.project.ui.StarryBackground
import org.example.project.viewModel.HomeViewModel
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
        navigationManager = navigationManager,
        coroutineScope = coroutineScope
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HomeScreenContent(
    viewModel: HomeViewModel,
    pagerState: PagerState,
    navigationManager: NavigationManager,
    coroutineScope: CoroutineScope
) {
    var currentTypingText = viewModel.typingTexts[pagerState.currentPage]

    LaunchedEffect(pagerState.currentPage) {
        currentTypingText = viewModel.typingTexts[pagerState.currentPage]
    }

    StarryBackground {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
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
                        Text(
                            text = "Keyboard Warrior",
                            style = MaterialTheme.typography.headlineMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = ThemeColors.OnSurface
                            ),
                            modifier = Modifier.align(Alignment.Center)
                        )
                        IconButton(
                            onClick = { navigationManager.navigateTo(Screen.Settings) },
                            modifier = Modifier.align(Alignment.CenterEnd)
                        ) {
                            Icon(
                                imageVector = FeatherIcons.Settings,
                                contentDescription = "Settings",
                                tint = Color.White
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
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier.weight(1f)
                ) { page ->
                    TypingScreen(
                        mode = viewModel.modes[page],
                        targetText = currentTypingText,
                        timeOptions = if (viewModel.modes[page] == TypingMode.TIME) viewModel.timeOptions else null,
                        wordOptions = if (viewModel.modes[page] == TypingMode.WORDS) viewModel.wordOptions else null,
                        onTestComplete = { result ->
                            viewModel.onTestComplete(result)
                        },
                        onBack = { viewModel.onBack() },
                        isStarted = !viewModel.showContent
                    )
                }

                AnimatedVisibility(
                    visible = viewModel.showContent,
                    enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
                    exit = slideOutVertically(targetOffsetY = { it }) + fadeOut()
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            IconButton(onClick = {
                                if (pagerState.currentPage > 0) {
                                    coroutineScope.launch {
                                        pagerState.animateScrollToPage(pagerState.currentPage - 1)
                                    }
                                }
                            }) {
                                Icon(FeatherIcons.ArrowLeft, contentDescription = "Previous")
                            }
                            Text(
                                text = viewModel.modes[pagerState.currentPage].name.lowercase()
                                    .replaceFirstChar { it.uppercase() },
                                style = MaterialTheme.typography.headlineSmall,
                                modifier = Modifier.padding(horizontal = 16.dp),
                                textAlign = TextAlign.Center
                            )
                            IconButton(onClick = {
                                if (pagerState.currentPage < viewModel.modes.size - 1) {
                                    coroutineScope.launch {
                                        pagerState.animateScrollToPage(pagerState.currentPage + 1)
                                    }
                                }
                            }) {
                                Icon(FeatherIcons.ArrowRight, contentDescription = "Next")
                            }
                        }

                        Button(
                            onClick = { viewModel.onStartTapped() },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = ThemeColors.Primary
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                text = "Start",
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                ),
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                        }
                    }
                }
            }
        }
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
            navigationManager = NavigationManager(),
            coroutineScope = coroutineScope
        )
    }
}
