package org.example.project.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import compose.icons.FeatherIcons
import compose.icons.feathericons.BarChart
import compose.icons.feathericons.Clipboard
import compose.icons.feathericons.Home
import compose.icons.feathericons.Settings
import compose.icons.feathericons.User
import org.example.project.MobileTypistTheme
import org.example.project.navigation.NavigationManager
import org.example.project.navigation.Screen
import org.example.project.screens.StatisticsScreen
import org.example.project.screens.StatisticsScreenState
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun MainScaffold(
    navigationManager: NavigationManager,
    content: @Composable (Modifier) -> Unit
) {
    Scaffold(
        bottomBar = {
            val currentScreen = navigationManager.currentScreen
            BottomNavigation(
                currentScreen = currentScreen,
                navigationManager = navigationManager,
            )
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(bottom = innerPadding.calculateBottomPadding())) {
            content(Modifier)
        }
    }
}

@Composable
private fun BottomNavigation(
    currentScreen: Screen,
    navigationManager: NavigationManager,
) {
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.onSurface,
        tonalElevation = 0.dp
    ) {
        NavigationBarItem(
            selected = currentScreen is Screen.Login,
            onClick = { navigationManager.navigateTo(Screen.Login) },
            icon = { Icon(FeatherIcons.Clipboard, contentDescription = "Leaderboard") },
            label = {
                Text(
                    text = "Leaderboard",
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = MaterialTheme.colorScheme.primary,
                selectedTextColor = MaterialTheme.colorScheme.primary,
                unselectedIconColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                unselectedTextColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                indicatorColor = Color.Transparent
            )
        )
        NavigationBarItem(
            selected = currentScreen is Screen.Settings,
            onClick = { navigationManager.navigateTo(Screen.Settings) },
            icon = { Icon(FeatherIcons.Settings, contentDescription = "Settings") },
            label = {
                Text(
                    text = "Settings",
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = MaterialTheme.colorScheme.primary,
                selectedTextColor = MaterialTheme.colorScheme.primary,
                unselectedIconColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                unselectedTextColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                indicatorColor = Color.Transparent
            )
        )
        NavigationBarItem(
            selected = currentScreen is Screen.Home,
            onClick = { navigationManager.navigateTo(Screen.Home) },
            icon = { Icon(FeatherIcons.Home, contentDescription = "Home") },
            label = { Text("Home") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = MaterialTheme.colorScheme.primary,
                selectedTextColor = MaterialTheme.colorScheme.primary,
                unselectedIconColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                unselectedTextColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                indicatorColor = Color.Transparent
            )
        )
        NavigationBarItem(
            selected = currentScreen is Screen.Statistics,
            onClick = { navigationManager.navigateTo(Screen.Statistics) },
            icon = { Icon(FeatherIcons.BarChart, contentDescription = "Statistics") },
            label = { Text("Statistics") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = MaterialTheme.colorScheme.primary,
                selectedTextColor = MaterialTheme.colorScheme.primary,
                unselectedIconColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                unselectedTextColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                indicatorColor = Color.Transparent
            )
        )
        NavigationBarItem(
            selected = currentScreen is Screen.Profile,
            onClick = { navigationManager.navigateTo(Screen.Profile) },
            icon = { Icon(FeatherIcons.User, contentDescription = "Profile") },
            label = { Text("Profile") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = MaterialTheme.colorScheme.primary,
                selectedTextColor = MaterialTheme.colorScheme.primary,
                unselectedIconColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                unselectedTextColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                indicatorColor = Color.Transparent
            )
        )
    }
}

@Composable
@Preview
private fun MainScaffoldPreview() {
    MobileTypistTheme(darkTheme = false) {
        MainScaffold(
            navigationManager = NavigationManager(),
            content = {
                StatisticsScreen(
                    navigationManager = NavigationManager(),
                    statisticsScreenState = StatisticsScreenState(

                    )
                )
            }
        )
    }
}

@Composable
@Preview
private fun MainScaffoldDarkThemePreview() {
    MobileTypistTheme(darkTheme = true) {
        MainScaffold(
            navigationManager = NavigationManager(),
            content = {
                StatisticsScreen(
                    navigationManager = NavigationManager(),
                    statisticsScreenState = StatisticsScreenState(

                    )
                )
            }
        )
    }
}