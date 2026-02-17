package org.example.project.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import org.example.project.data.StorageManager
import org.example.project.screens.AboutScreen
import org.example.project.screens.HomeScreen
import org.example.project.screens.SettingsScreen
import org.example.project.screens.StatisticsScreen
import org.example.project.screens.StatisticsScreenState
import org.example.project.ui.MainScaffold

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

    MainScaffold(navigationManager = navigationManager) { scaffoldModifier ->
        when (currentScreen) {
            is Screen.Home -> {
                HomeScreen(
                    navigationManager = navigationManager,
                    storageManager = storageManager,
                    modifier = modifier.then(scaffoldModifier)
                )
            }

            is Screen.TimeModeScreen -> {
                // TimeModeScreen call
            }

            is Screen.WordsMode -> {
                // WordsModeScreen call
            }

            is Screen.QuotesMode -> {
                // QuotesModeScreen call
            }

            is Screen.Statistics -> {
                StatisticsScreen(
                    navigationManager = navigationManager,
                    statisticsScreenState = StatisticsScreenState(
                        results = storageManager.getResults(),
                        bestWpm = storageManager.getBestWpm(),
                        totalTests = storageManager.getTotalTests(),
                    ),
                    modifier = modifier.then(scaffoldModifier)
                )
            }

            is Screen.Settings -> {
                SettingsScreen(
                    navigationManager = navigationManager,
                    storageManager = storageManager,
                    onThemeChange = onThemeChange,
                    modifier = modifier.then(scaffoldModifier)
                )
            }

            is Screen.About -> {
                AboutScreen(
                    navigationManager = navigationManager,
                    modifier = modifier.then(scaffoldModifier)
                )
            }
        }
    }
}
