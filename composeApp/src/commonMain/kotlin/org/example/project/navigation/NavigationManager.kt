package org.example.project.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import org.example.project.navigation.model.Screen

class NavigationManager(
    initialScreen: Screen = Screen.Home,
    private val useComposeBottomBar: Boolean = true
) {
    var currentScreen by mutableStateOf<Screen>(initialScreen)
        private set

    var showBottomBar by mutableStateOf(useComposeBottomBar)

    fun navigateTo(screen: Screen) {
        currentScreen = screen
        // If iOS uses native tabs, keep Compose bottom bar hidden.
        showBottomBar = useComposeBottomBar
    }
    
    fun navigateBack() {
        when (currentScreen) {
            is Screen.Profile,
            is Screen.Settings,
            is Screen.About -> {
                currentScreen = Screen.Home
                // Keep hidden on iOS native tab setup.
                showBottomBar = useComposeBottomBar
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
    useComposeBottomBar: Boolean = true
): NavigationManager {
    return remember { NavigationManager(initialScreen, useComposeBottomBar) }
}
