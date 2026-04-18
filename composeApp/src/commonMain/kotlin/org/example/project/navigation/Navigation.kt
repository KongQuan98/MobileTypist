package org.example.project.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import org.example.project.data.model.AppSettings
import org.example.project.data.storage.StorageManager
import org.example.project.navigation.model.Screen
import org.example.project.screens.AboutScreen
import org.example.project.screens.CreateAccountScreen
import org.example.project.screens.EditProfileScreen
import org.example.project.screens.HomeScreen
import org.example.project.screens.LeaderboardScreen
import org.example.project.screens.LoginScreen
import org.example.project.screens.ProfileScreen
import org.example.project.screens.ProfileScreenState
import org.example.project.screens.SelectAvatarScreen
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
    val userProfile by storageManager.userProfileFlow.collectAsState()

    // Show/Hide bottom bar logic
    LaunchedEffect(currentScreen) {
        navigationManager.showBottomBar = when (currentScreen) {
            is Screen.EditProfile, is Screen.Login, is Screen.Register, is Screen.SelectAvatar -> false
            else -> true
        }
    }

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
                    onSignInClick = { _, _ -> },
                    onSignUpClick = {
                        navigationManager.navigateTo(Screen.Register)
                    }
                )
            }

            is Screen.Profile -> {
                ProfileScreen(
                    onEditProfileClicked = { navigationManager.navigateTo(Screen.EditProfile) },
                    profileScreenState = ProfileScreenState(
                        userProfile = userProfile,
                        recentTestResult = storageManager.getResults(),
                        bestWpm = storageManager.getBestWpm(),
                        totalTests = storageManager.getTotalTests(),
                    ),
                    modifier = modifier.then(scaffoldModifier)
                )
            }

            is Screen.EditProfile -> {
                EditProfileScreen(
                    userProfile = userProfile,
                    onSaveClicked = { updatedProfile ->
                        storageManager.saveUserProfile(updatedProfile)
                        navigationManager.navigateTo(Screen.Profile)
                    },
                    onBackClicked = {
                        navigationManager.navigateTo(Screen.Profile)
                    },
                    onNavigateToSelectAvatar = {
                        navigationManager.navigateTo(Screen.SelectAvatar)
                    }
                )
            }

            is Screen.SelectAvatar -> {
                SelectAvatarScreen(
                    currentAvatarId = userProfile.avatarId,
                    onAvatarSelected = { newId ->
                        storageManager.saveUserProfile(userProfile.copy(avatarId = newId))
                        navigationManager.navigateTo(Screen.EditProfile)
                    },
                    onBackClicked = {
                        navigationManager.navigateTo(Screen.EditProfile)
                    }
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
