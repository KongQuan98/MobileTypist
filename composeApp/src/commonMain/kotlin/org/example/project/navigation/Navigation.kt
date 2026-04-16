package org.example.project.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import org.example.project.data.StorageManager
import org.example.project.data.model.AppSettings
import org.example.project.navigation.model.Screen
import org.example.project.screens.AboutScreen
import org.example.project.screens.CreateAccountScreen
import org.example.project.screens.HomeScreen
import org.example.project.screens.LeaderboardScreen
import org.example.project.screens.LoginScreen
import org.example.project.screens.ProfileScreen
import org.example.project.screens.ProfileScreenState
import org.example.project.screens.SettingsScreen
import org.example.project.screens.SettingsScreenAction
import org.example.project.screens.StatisticsScreen
import org.example.project.screens.StatisticsScreenState
import org.example.project.ui.MainScaffold

@Composable
fun Navigation(
    navigationManager: NavigationManager,
    storageManager: StorageManager,
    appSettings: AppSettings,
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

            is Screen.Statistics -> {
                StatisticsScreen(
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
                    action = { action ->
                        when (action) {
                            is SettingsScreenAction.Back -> navigationManager.navigateBack()
                            is SettingsScreenAction.ClearAllData -> storageManager.clearAllData()
                            is SettingsScreenAction.SaveSettings -> {
                                storageManager.saveSettings(action.settings)
                            }
                        }
                    },
                    appSettings = appSettings,
                    modifier = modifier.then(scaffoldModifier)
                )
            }

            is Screen.About -> {
                AboutScreen(
                    navigationManager = navigationManager,
                    modifier = modifier.then(scaffoldModifier)
                )
            }

            is Screen.Login -> {
                LoginScreen(
                    navigationManager = navigationManager,
                    onSignInClick = { _, _ -> },
                    onForgotPasswordClick = {},
                    onGuestClick = {
                        navigationManager.navigateTo(Screen.Home)
                    },
                    onSignUpClick = {
                        navigationManager.navigateTo(Screen.Register)
                    }
                )
            }

            is Screen.Register -> {
                CreateAccountScreen(
                    navigationManager = navigationManager,
                    onSignInClick = { _, _ -> },
                    onForgotPasswordClick = {},
                    onGuestClick = {
                        navigationManager.navigateTo(Screen.Home)
                    },
                    onSignUpClick = {
                        navigationManager.navigateTo(Screen.Register)
                    }
                )
            }

            is Screen.Profile -> {
                ProfileScreen(
                    navigationManager = navigationManager,
                    profileScreenState = ProfileScreenState(
                        recentTestResult = storageManager.getResults(),
                        bestWpm = storageManager.getBestWpm(),
                        totalTests = storageManager.getTotalTests(),
                    ),
                    modifier = modifier.then(scaffoldModifier)
                )
            }

            is Screen.LeaderBoard -> {
                LeaderboardScreen(
                    navigationManager = navigationManager,
                    modifier = modifier.then(scaffoldModifier)
                )
            }
        }
    }
}
