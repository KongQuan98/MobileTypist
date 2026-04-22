package org.example.project.navigation.model

import androidx.compose.ui.graphics.vector.ImageVector
import compose.icons.FeatherIcons
import compose.icons.feathericons.BarChart
import compose.icons.feathericons.Clipboard
import compose.icons.feathericons.Home
import compose.icons.feathericons.Settings
import compose.icons.feathericons.User
import mobiletypist.composeapp.generated.resources.Res
import mobiletypist.composeapp.generated.resources.home_title
import mobiletypist.composeapp.generated.resources.leaderboard_title
import mobiletypist.composeapp.generated.resources.profile_title
import mobiletypist.composeapp.generated.resources.settings_title
import mobiletypist.composeapp.generated.resources.statistics_title
import org.jetbrains.compose.resources.StringResource

sealed class Screen {
    object Home : Screen()
    object Statistics : Screen()
    object Settings : Screen()
    object About : Screen()
    object Login : Screen()
    object Register : Screen()
    object Profile : Screen()
    object LeaderBoard : Screen()
    object EditProfile : Screen()
    object SelectAvatar : Screen()
    object Achievements : Screen()
}

sealed class BottomNavigationTab(
    val title: StringResource,
    val icon: ImageVector,
    val screenRoute: Screen,
) {
    object Settings : BottomNavigationTab(
        title = Res.string.settings_title,
        icon = FeatherIcons.Settings,
        screenRoute = Screen.Settings,
    )

    object LeaderBoard : BottomNavigationTab(
        title = Res.string.leaderboard_title,
        icon = FeatherIcons.Clipboard,
        screenRoute = Screen.LeaderBoard,
    )

    object Home : BottomNavigationTab(
        title = Res.string.home_title,
        icon = FeatherIcons.Home,
        screenRoute = Screen.Home
    )

    object Statistics : BottomNavigationTab(
        title = Res.string.statistics_title,
        icon = FeatherIcons.BarChart,
        screenRoute = Screen.Statistics,
    )

    object Profile : BottomNavigationTab(
        title = Res.string.profile_title,
        icon = FeatherIcons.User,
        screenRoute = Screen.Profile,
    )

    companion object {
        val tabList: List<BottomNavigationTab> = listOf(
            Settings,
            LeaderBoard,
            Home,
            Statistics,
            Profile
        )
    }
}
