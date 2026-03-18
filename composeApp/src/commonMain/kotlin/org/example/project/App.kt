package org.example.project

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
    val initialSettings = storageManager.getSettings()
    var isDarkTheme by remember { mutableStateOf(initialSettings.darkTheme) }

    // Update theme if darkTheme parameter changes (for preview)
    LaunchedEffect(darkTheme) {
        if (darkTheme != initialSettings.darkTheme) {
            isDarkTheme = darkTheme
        }
    }

    MobileTypistTheme(darkTheme = isDarkTheme) {
        Navigation(
            navigationManager = navigationManager,
            storageManager = storageManager,
            modifier = Modifier.fillMaxSize().safeDrawingPadding()
        )
    }
}