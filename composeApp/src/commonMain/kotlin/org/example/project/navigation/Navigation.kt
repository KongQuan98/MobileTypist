package org.example.project.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import org.example.project.data.StorageManager
import org.example.project.screens.*
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
                navigationManager = navigationManager,
                storageManager = storageManager,
                modifier = modifier
            )
        }
        is Screen.Statistics -> {
            StatisticsScreen(
                navigationManager = navigationManager,
                storageManager = storageManager,
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

