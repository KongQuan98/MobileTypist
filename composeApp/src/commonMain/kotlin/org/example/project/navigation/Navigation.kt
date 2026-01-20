package org.example.project.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import org.example.project.data.StorageManager
import org.example.project.screens.AboutScreen
import org.example.project.screens.HomeScreen
import org.example.project.screens.QuotesModeScreen
import org.example.project.screens.SettingsScreen
import org.example.project.screens.StatisticsScreen
import org.example.project.screens.StatisticsScreenState
import org.example.project.screens.TimeModeScreen
import org.example.project.screens.TypingScreenAction
import org.example.project.screens.WordsModeScreen
import org.example.project.ui.StarryBackground

@Composable
fun Navigation(
    navigationManager: NavigationManager,
    storageManager: StorageManager,
    onThemeChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    // Handle platform back button (Android) - no-op on iOS
    BackHandler(
        enabled = navigationManager.canGoBack(),
        onBack = {
            navigationManager.navigateBack()
        }
    )
    
    val currentScreen = navigationManager.currentScreen
    
    when (currentScreen) {
        is Screen.Home -> {
            StarryBackground {
                HomeScreen(
                    navigationManager = navigationManager,
                    storageManager = storageManager,
                    modifier = modifier
                )
            }
        }
        is Screen.TimeModeScreen -> {
            TimeModeScreen(
                navigationManager = navigationManager,
                storageManager = storageManager,
                modifier = modifier
            )
        }
        is Screen.WordsMode -> {
            WordsModeScreen(
                navigationManager = navigationManager,
                storageManager = storageManager,
                modifier = modifier
            )
        }
        is Screen.QuotesMode -> {
            QuotesModeScreen(
                action = {
                    when (it) {
                        is TypingScreenAction.OnTestComplete -> storageManager.saveResult(it.result)
                        TypingScreenAction.OnNavigateBack -> {
                            navigationManager.navigateBack()
                        }
                    }
                },
                modifier = modifier
            )
        }
        is Screen.Statistics -> {
            StatisticsScreen(
                navigationManager = navigationManager,
                statisticsScreenState = StatisticsScreenState(
                    results = storageManager.getResults(),
                    bestWpm = storageManager.getBestWpm(),
                    totalTests = storageManager.getTotalTests(),
                ),
                modifier = modifier
            )
        }
        is Screen.Settings -> {
            SettingsScreen(
                navigationManager = navigationManager,
                storageManager = storageManager,
                onThemeChange = onThemeChange,
                modifier = modifier
            )
        }
        is Screen.About -> {
            AboutScreen(
                navigationManager = navigationManager,
                modifier = modifier
            )
        }
    }
}

