package org.example.project

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import org.example.project.data.StorageManager
import org.example.project.data.createSettings
import org.example.project.navigation.Navigation
import org.example.project.navigation.rememberNavigationManager
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun App(darkTheme: Boolean = false) {
    // Create shared instances
    val navigationManager = rememberNavigationManager()
    val storageManager = remember {
        StorageManager(settings = createSettings())
    }

    // Load theme from settings and manage state
    val settingState by storageManager.settingsFlow.collectAsState()

    MobileTypistTheme(darkTheme = settingState.darkTheme) {
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