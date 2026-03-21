package org.example.project.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
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
import org.example.project.MobileTypistTheme
import org.example.project.navigation.NavigationManager
import org.example.project.navigation.model.BottomNavigationTab
import org.example.project.navigation.model.Screen
import org.example.project.screens.SettingsScreen
import org.jetbrains.compose.ui.tooling.preview.Preview

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScaffold(
    navigationManager: NavigationManager,
    content: @Composable (Modifier) -> Unit
) {
    val currentScreen = navigationManager.currentScreen
    Scaffold(
        bottomBar = {
            AnimatedVisibility(
                visible = navigationManager.showBottomBar,
                enter = slideInVertically(initialOffsetY = { it }),
                exit = slideOutVertically(targetOffsetY = { it })
            ) {
                BottomNavigation(
                    currentScreen = currentScreen,
                    navigationManager = navigationManager,
                )
            }
        }
    ) { innerPadding ->
        // When hidden, we don't want to keep the bottom padding
        val bottomPadding =
            if (navigationManager.showBottomBar) innerPadding.calculateBottomPadding() else 0.dp
        Box(
            modifier = Modifier.padding(bottom = bottomPadding)
        ) {
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
        BottomNavigationTab.tabList.forEach { tab ->
            NavigationBarItem(
                modifier = Modifier.padding(horizontal = 6.dp),
                selected = currentScreen == tab.screenRoute,
                onClick = { navigationManager.navigateTo(tab.screenRoute) },
                icon = { Icon(tab.icon, contentDescription = tab.title) },
                label = {
                    Text(
                        text = tab.title,
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
        }
    }
}

@Composable
@Preview
private fun MainScaffoldDarkThemePreview() {
    MobileTypistTheme(darkTheme = true) {
        MainScaffold(
            navigationManager = NavigationManager(),
            content = {
                SettingsScreen(
                    action = {},
                    modifier = Modifier
                )
            }
        )
    }
}