package org.example.project.navigation.model

import androidx.compose.ui.graphics.vector.ImageVector
import compose.icons.FeatherIcons
import compose.icons.feathericons.BarChart
import compose.icons.feathericons.Clipboard
import compose.icons.feathericons.Home
import compose.icons.feathericons.Settings
import compose.icons.feathericons.User

sealed class Screen {
    object Home : Screen()
    object Statistics : Screen()
    object Settings : Screen()
    object About : Screen()
    object Login : Screen()
    object Register : Screen()
    object Profile : Screen()
}

sealed class BottomNavigationTab(
    val title: String,
    val icon: ImageVector,
    val screenRoute: Screen,
) {
    object Settings : BottomNavigationTab(
        title = "Settings",
        icon = FeatherIcons.Settings,
        screenRoute = Screen.Settings,
    )

    object Login : BottomNavigationTab(
        title = "Leaderboard",
        icon = FeatherIcons.Clipboard,
        screenRoute = Screen.Login,
    )

    object Home : BottomNavigationTab(
        title = "Home",
        icon = FeatherIcons.Home,
        screenRoute = Screen.Home
    )

    object Statistics : BottomNavigationTab(
        title = "Statistics",
        icon = FeatherIcons.BarChart,
        screenRoute = Screen.Statistics,
    )

    object Profile : BottomNavigationTab(
        title = "Profile",
        icon = FeatherIcons.User,
        screenRoute = Screen.Profile,
    )

    companion object {
        val tabList: List<BottomNavigationTab> = listOf(
            Settings,
            Login,
            Home,
            Statistics,
            Profile
        )
    }
}
