package org.example.project

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
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
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun App() {
    App(
        startScreen = Screen.Home,
        useComposeBottomBar = true
    )
}

@Composable
fun App(
    startScreen: Screen = Screen.Home,
    useComposeBottomBar: Boolean = true
) {
    // Create shared instances
    val navigationManager = rememberNavigationManager(
        initialScreen = startScreen,
        useComposeBottomBar = useComposeBottomBar
    )
    val storageManager = remember {
        StorageManager(settings = createSettings())
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