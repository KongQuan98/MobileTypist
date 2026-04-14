package org.example.project

import androidx.compose.ui.window.ComposeUIViewController
import org.example.project.navigation.model.Screen

fun MainViewController() = ComposeUIViewController { App() }

fun HomeTabViewController() = ComposeUIViewController {
    App(
        startScreen = Screen.Home,
        useComposeBottomBar = false
    )
}

fun StatisticsTabViewController() = ComposeUIViewController {
    App(
        startScreen = Screen.Statistics,
        useComposeBottomBar = false
    )
}

fun SettingsTabViewController() = ComposeUIViewController {
    App(
        startScreen = Screen.Settings,
        useComposeBottomBar = false
    )
}

fun ProfileTabViewController() = ComposeUIViewController {
    App(
        startScreen = Screen.Profile,
        useComposeBottomBar = false
    )
}

fun LeaderboardTabViewController() = ComposeUIViewController {
    App(
        startScreen = Screen.LeaderBoard,
        useComposeBottomBar = false
    )
}
