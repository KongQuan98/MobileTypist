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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
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
import kotlinx.coroutines.launch
import org.example.project.ThemeColors
import org.example.project.data.QuotesData
import org.example.project.data.StorageManager
import org.example.project.data.TypingMode
import org.example.project.data.createSettings
import org.example.project.navigation.NavigationManager
import org.example.project.navigation.Screen
import org.example.project.ui.StarryBackground
import org.jetbrains.compose.ui.tooling.preview.Preview

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HomeScreen(
    navigationManager: NavigationManager,
    storageManager: StorageManager,
    modifier: Modifier = Modifier
) {
    val modes = listOf(TypingMode.TIME, TypingMode.WORDS, TypingMode.QUOTES)
    val pagerState = rememberPagerState(pageCount = { modes.size })
    var showContent by remember { mutableStateOf(true) }
    val coroutineScope = rememberCoroutineScope()

    val timeOptions = listOf(15, 30, 60)
    val wordOptions = listOf(10, 25, 50, 100)

    val typingTexts = remember {
        modes.map {
            when (it) {
                TypingMode.TIME -> QuotesData.quotes.joinToString(" ")
                TypingMode.WORDS -> QuotesData.getQuoteForWords(wordOptions.first())
                TypingMode.QUOTES -> QuotesData.getRandomQuote()
            }
        }
    }
    var currentTypingText by remember { mutableStateOf(typingTexts[0]) }

    LaunchedEffect(pagerState.currentPage) {
        currentTypingText = typingTexts[pagerState.currentPage]
    }

    StarryBackground {
        Scaffold(
            modifier = modifier.fillMaxSize(),
            containerColor = Color.Transparent,
            topBar = {
                AnimatedVisibility(
                    visible = showContent,
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
                        mode = modes[page],
                        targetText = currentTypingText,
                        timeOptions = if (modes[page] == TypingMode.TIME) timeOptions else null,
                        wordOptions = if (modes[page] == TypingMode.WORDS) wordOptions else null,
                        onTestComplete = { result ->
                            storageManager.saveResult(result)
                        },
                        onBack = { showContent = true },
                        isStarted = !showContent
                    )
                }

                AnimatedVisibility(
                    visible = showContent,
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
                                text = modes[pagerState.currentPage].name.lowercase().replaceFirstChar { it.uppercase() },
                                style = MaterialTheme.typography.headlineSmall,
                                modifier = Modifier.padding(horizontal = 16.dp),
                                textAlign = TextAlign.Center
                            )
                            IconButton(onClick = { 
                                if (pagerState.currentPage < modes.size - 1) {
                                    coroutineScope.launch {
                                        pagerState.animateScrollToPage(pagerState.currentPage + 1)
                                    }
                                }
                            }) {
                                Icon(FeatherIcons.ArrowRight, contentDescription = "Next")
                            }
                        }

                        Button(
                            onClick = { showContent = false },
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

@Preview
@Composable
private fun HomeScreenPreview() {
    org.example.project.MobileTypistTheme(darkTheme = true) {
        HomeScreen(
            navigationManager = NavigationManager(),
            storageManager = StorageManager(
                settings = createSettings()
            )
        )
    }
}
