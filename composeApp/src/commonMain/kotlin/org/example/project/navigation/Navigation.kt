package org.example.project.navigation

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.example.project.auth.AuthModule
import org.example.project.data.model.AppSettings
import org.example.project.data.storage.StorageManager
import org.example.project.di.LocalAppContainer
import org.example.project.navigation.model.Screen
import org.example.project.screens.AboutScreen
import org.example.project.screens.AchievementsScreen
import org.example.project.screens.CreateAccountScreen
import org.example.project.screens.EditProfileScreen
import org.example.project.screens.HomeScreen
import org.example.project.screens.LeaderboardScreen
import org.example.project.screens.LoginScreen
import org.example.project.screens.ProfileScreen
import org.example.project.screens.SelectAvatarScreen
import org.example.project.screens.SettingsScreen
import org.example.project.screens.StatisticsScreen
import org.example.project.ui.MainScaffold
import org.example.project.utils.AudioPlayer
import org.example.project.viewModel.AchievementsViewModel
import org.example.project.viewModel.EditProfileViewModel
import org.example.project.viewModel.HomeViewModel
import org.example.project.viewModel.ProfileViewModel
import org.example.project.viewModel.SettingsViewModel
import org.example.project.viewModel.StatisticsViewModel

@Composable
fun Navigation(
    navigationManager: NavigationManager,
    storageManager: StorageManager,
    appSettings: AppSettings,
    audioPlayer: AudioPlayer,
    modifier: Modifier = Modifier
) {
    val appContainer = LocalAppContainer.current
    val achievementRepository = appContainer.achievementRepository

    val statisticsViewModel = remember { StatisticsViewModel(storageManager) }
    val profileViewModel = remember { ProfileViewModel(storageManager, achievementRepository) }
    val settingsViewModel = remember { SettingsViewModel(storageManager) }
    val homeViewModel = remember { HomeViewModel(storageManager, appContainer.streakRepository) }
    val achievementsViewModel = remember { AchievementsViewModel(achievementRepository) }
    val editProfileViewModel = remember { EditProfileViewModel(storageManager) }
    val authViewModel = remember { AuthModule.viewModel }

    // Handle platform back button (Android) - no-op on iOS
    BackHandler(
        enabled = navigationManager.canGoBack(),
        onBack = {
            navigationManager.navigateBack()
        }
    )

    val currentScreen = navigationManager.currentScreen

    // Show/Hide bottom bar logic
    LaunchedEffect(currentScreen) {
        navigationManager.showBottomBar = when (currentScreen) {
            is Screen.EditProfile, is Screen.Login, is Screen.Register, is Screen.SelectAvatar -> false
            else -> true
        }
    }

    // Evaluate achievements when results change
    val results by storageManager.resultsFlow.collectAsStateWithLifecycle()
    LaunchedEffect(results) {
        achievementRepository.evaluate()
    }

    MainScaffold(
        navigationManager = navigationManager,
        audioPlayer = audioPlayer,
    ) { scaffoldModifier ->
        AnimatedContent(
            targetState = currentScreen,
            transitionSpec = {
                if (targetState.isFullScreenOverlay() || initialState.isFullScreenOverlay()) {
                    (slideInVertically(
                        initialOffsetY = { it },
                        animationSpec = tween(400)
                    ) + fadeIn()).togetherWith(
                        slideOutVertically(
                            targetOffsetY = { it },
                            animationSpec = tween(400)
                        ) + fadeOut()
                    )
                } else {
                    fadeIn(animationSpec = tween(300))
                        .togetherWith(fadeOut(animationSpec = tween(300)))
                }
            },
            label = "screenTransition"
        ) { targetScreen ->
            when (targetScreen) {
                is Screen.Home -> {
                    HomeScreen(
                        viewModel = homeViewModel,
                        navigationManager = navigationManager,
                        modifier = modifier.then(scaffoldModifier)
                    )
                }

                is Screen.Statistics -> {
                    StatisticsScreen(
                        viewModel = statisticsViewModel,
                        modifier = modifier.then(scaffoldModifier)
                    )
                }

                is Screen.Settings -> {
                    SettingsScreen(
                        viewModel = settingsViewModel,
                        onBack = { navigationManager.navigateBack() },
                        audioPlayer = audioPlayer,
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
                        viewModel = authViewModel,
                        onGuestClick = {
                            navigationManager.navigateTo(Screen.Home)
                        },
                        onSignUpClick = {
                            navigationManager.navigateTo(Screen.Register)
                        }
                    )
                }

                is Screen.Register -> {
                    CreateAccountScreen(viewModel = authViewModel)
                }

                is Screen.Profile -> {
                    ProfileScreen(
                        viewModel = profileViewModel,
                        onEditProfileClicked = { navigationManager.navigateTo(Screen.EditProfile) },
                        modifier = modifier.then(scaffoldModifier),
                        onViewMoreAchievements = {
                            navigationManager.navigateTo(Screen.Achievements)
                        },
                        onLoginClicked = {
                            navigationManager.navigateTo(Screen.Login)
                        }
                    )
                }

                is Screen.EditProfile -> {
                    EditProfileScreen(
                        viewModel = editProfileViewModel,
                        audioPlayer = audioPlayer,
                        onSaveClicked = {
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
                        currentAvatarId = storageManager.getUserProfile().avatarId,
                        onAvatarSelected = { newId ->
                            val userProfile = storageManager.getUserProfile()
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
                        modifier = modifier.then(scaffoldModifier)
                    )
                }

                is Screen.Achievements -> {
                    AchievementsScreen(
                        viewModel = achievementsViewModel,
                        onBackClicked = {
                            navigationManager.navigateBack()
                        },
                        modifier = modifier
                    )
                }
            }
        }
    }
}

private fun Screen.isFullScreenOverlay(): Boolean =
    this is Screen.EditProfile || this is Screen.Login || this is Screen.Register || this is Screen.SelectAvatar || this is Screen.Achievements
