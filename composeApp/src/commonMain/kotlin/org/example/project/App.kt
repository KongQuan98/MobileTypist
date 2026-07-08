package org.example.project

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.LifecycleResumeEffect
import org.example.project.data.storage.StorageManager
import org.example.project.data.storage.createSettings
import org.example.project.navigation.Navigation
import org.example.project.navigation.model.Screen
import org.example.project.navigation.rememberNavigationManager
import org.example.project.ui.LocalAudioPlayer
import org.example.project.ui.LocalHaptics
import org.example.project.utils.AudioPlayer
import org.example.project.utils.TypingHapticFeedback

@Composable
fun App(
    startScreen: Screen = Screen.Home,
    onToggleTabBar: ((Boolean) -> Unit)? = null // Callback to bridge to iOS native TabBar
) {
    // Create shared instances
    val navigationManager = rememberNavigationManager(
        initialScreen = startScreen,
    )
    val storageManager = remember {
        StorageManager(settings = createSettings())
    }

    // Refresh data when screen becomes visible (Crucial for iOS TabBar navigation)
    LifecycleResumeEffect(Unit) {
        storageManager.refreshStats()
        onPauseOrDispose { }
    }

    // Load theme from settings and manage state
    val settingState by storageManager.settingsFlow.collectAsState()

    val haptics = remember {
        TypingHapticFeedback(
            isEnabled = { settingState.vibrationEnabled },
        )
    }

    // Load AudioPlayer for click sound effect
    val audioPlayer = remember {
        AudioPlayer(
            isEnabled = { settingState.soundEnabled }
        )
    }

    // Observe changes and notify the native platform
    LaunchedEffect(navigationManager.showBottomBar) {
        onToggleTabBar?.invoke(navigationManager.showBottomBar)
        audioPlayer.preload()
    }

    MobileTypistTheme(darkTheme = settingState.darkTheme) {
        CompositionLocalProvider(
            LocalHaptics provides haptics,
            LocalAudioPlayer provides audioPlayer
        ) {
            Navigation(
                navigationManager = navigationManager,
                storageManager = storageManager,
                appSettings = settingState,
                audioPlayer = audioPlayer,
                modifier = Modifier
                    .fillMaxSize()
                    .safeDrawingPadding()
            )
        }
    }
}
