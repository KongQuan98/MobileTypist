package org.example.project.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.example.project.MobileTypistTheme
import org.example.project.data.model.AppSettings
import org.example.project.navigation.NavigationManager
import org.example.project.navigation.model.BottomNavigationTab
import org.example.project.navigation.model.Screen
import org.example.project.screens.SettingsScreenContent
import org.example.project.utils.AudioPlayer
import org.example.project.utils.LocalHaptics
import org.example.project.utils.PreviewCompositionLocals
import org.example.project.utils.isIOS
import org.example.project.utils.wrap
import org.jetbrains.compose.ui.tooling.preview.Preview

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScaffold(
    navigationManager: NavigationManager,
    audioPlayer: AudioPlayer? = null,
    content: @Composable (Modifier) -> Unit
) {
    val currentScreen = navigationManager.currentScreen
    val shouldShowComposeBottomBar = navigationManager.showBottomBar && !isIOS()
    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        bottomBar = {
            AnimatedVisibility(
                visible = shouldShowComposeBottomBar,
                enter = slideInVertically(initialOffsetY = { it }),
                exit = slideOutVertically(targetOffsetY = { it })
            ) {
                BottomNavigation(
                    currentScreen = currentScreen,
                    navigationManager = navigationManager,
                    audioPlayer = audioPlayer,
                )
            }
        }
    ) { innerPadding ->
        val bottomPadding =
            if (shouldShowComposeBottomBar) innerPadding.calculateBottomPadding() else 0.dp
        Box(
            modifier = Modifier
                .padding(bottom = bottomPadding)
                .windowInsetsPadding(WindowInsets.systemBars)
        ) {
            content(Modifier)
        }
    }
}

@Composable
private fun BottomNavigation(
    currentScreen: Screen,
    navigationManager: NavigationManager,
    audioPlayer: AudioPlayer?,
) {
    val haptics = LocalHaptics.current

    val navItems = remember {
        BottomNavigationTab.tabList.map { tab ->
            LimelightNavItem(
                id = tab.screenRoute.toString(),
                icon = { Icon(tab.icon, contentDescription = null) },
                label = "",
                screenRoute = tab.screenRoute
            )
        }
    }

    Box(
        modifier = Modifier.windowInsetsPadding(WindowInsets.navigationBars)
    ) {
        LimelightNav(
            items = navItems,
            currentScreen = currentScreen,
            onTabSelected = { route ->
                haptics.wrap(audioPlayer) {
                    navigationManager.navigateTo(route)
                }
            }
        )
    }
}

@Composable
@Preview
private fun MainScaffoldDarkThemePreview() {
    PreviewCompositionLocals {
        MobileTypistTheme(darkTheme = true) {
            MainScaffold(
                navigationManager = NavigationManager(),
                content = {
                    SettingsScreenContent(
                        appSettings = AppSettings(),
                        onBack = {},
                        onSaveSettings = {},
                        onClearAllData = {},
                        onSignOut = {}
                    )
                }
            )
        }
    }
}
