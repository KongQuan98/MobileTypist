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
import org.example.project.data.storage.StorageManager
import org.example.project.data.storage.createSettings
import org.example.project.navigation.Navigation
import org.example.project.navigation.model.Screen
import org.example.project.navigation.rememberNavigationManager
import org.example.project.ui.LocalHaptics
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

    // Observe changes and notify the native platform
    LaunchedEffect(navigationManager.showBottomBar) {
        onToggleTabBar?.invoke(navigationManager.showBottomBar)
    }

    // Load theme from settings and manage state
    val settingState by storageManager.settingsFlow.collectAsState()

    val haptics = remember {
        TypingHapticFeedback(
            isEnabled = { settingState.vibrationEnabled },
        )
    }

    MobileTypistTheme(darkTheme = settingState.darkTheme) {
        CompositionLocalProvider(
            LocalHaptics provides haptics
        ) {
            Navigation(
                navigationManager = navigationManager,
                storageManager = storageManager,
                appSettings = settingState,
                modifier = Modifier
                    .fillMaxSize()
                    .safeDrawingPadding()
            )
        }
    }
}
