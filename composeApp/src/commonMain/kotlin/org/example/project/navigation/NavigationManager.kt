package org.example.project.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import org.example.project.navigation.model.Screen

class NavigationManager {
    var currentScreen by mutableStateOf<Screen>(Screen.Home)
        private set

    var showBottomBar by mutableStateOf(true)

    fun navigateTo(screen: Screen) {
        currentScreen = screen
        // Always show bottom bar when navigating to main screens
        showBottomBar = true
    }
    
    fun navigateBack() {
        when (currentScreen) {
            is Screen.Profile,
            is Screen.Settings,
            is Screen.About -> {
                currentScreen = Screen.Home
                showBottomBar = true
            }
            else -> {}
        }
    }
    
    fun canGoBack(): Boolean {
        return currentScreen !is Screen.Home
    }
}

@Composable
fun rememberNavigationManager(): NavigationManager {
    return remember { NavigationManager() }
}
