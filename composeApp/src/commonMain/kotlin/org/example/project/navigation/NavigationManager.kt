package org.example.project.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import org.example.project.navigation.model.Screen

class NavigationManager(
    initialScreen: Screen = Screen.Home,
) {
    var currentScreen by mutableStateOf(initialScreen)
        private set

    var showBottomBar by mutableStateOf(true)

    fun navigateTo(screen: Screen) {
        currentScreen = screen
        showBottomBar = true
    }
    
    fun navigateBack() {
        when (currentScreen) {
            is Screen.Profile,
            is Screen.Settings,
            is Screen.About,
            is Screen.LeaderBoard,
            is Screen.Statistics -> {
                currentScreen = Screen.Home
                showBottomBar = true
            }
            is Screen.Achievements -> {
                currentScreen = Screen.Profile
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
fun rememberNavigationManager(
    initialScreen: Screen = Screen.Home,
): NavigationManager {
    return remember { NavigationManager(initialScreen) }
}
