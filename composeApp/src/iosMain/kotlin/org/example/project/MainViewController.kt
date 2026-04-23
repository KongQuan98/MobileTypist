package org.example.project

import androidx.compose.ui.window.ComposeUIViewController
import org.example.project.navigation.model.Screen

fun MainViewController() = ComposeUIViewController { App() }

fun HomeTabViewController(onToggleTabBar: (Boolean) -> Unit) = ComposeUIViewController {
    App(
        startScreen = Screen.Home,
        onToggleTabBar = onToggleTabBar
    )
}

fun StatisticsTabViewController(onToggleTabBar: (Boolean) -> Unit) = ComposeUIViewController {
    App(
        startScreen = Screen.Statistics,
        onToggleTabBar = onToggleTabBar
    )
}

fun SettingsTabViewController(onToggleTabBar: (Boolean) -> Unit) = ComposeUIViewController {
    App(
        startScreen = Screen.Settings,
        onToggleTabBar = onToggleTabBar
    )
}

fun ProfileTabViewController(onToggleTabBar: (Boolean) -> Unit) = ComposeUIViewController {
    App(
        startScreen = Screen.Profile,
        onToggleTabBar = onToggleTabBar
    )
}

fun LeaderboardTabViewController(onToggleTabBar: (Boolean) -> Unit) = ComposeUIViewController {
    App(
        startScreen = Screen.LeaderBoard,
        onToggleTabBar = onToggleTabBar
    )
}
