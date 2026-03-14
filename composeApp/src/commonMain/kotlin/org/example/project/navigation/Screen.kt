package org.example.project.navigation

sealed class Screen {
    object Home : Screen()
    object Statistics : Screen()
    object Settings : Screen()
    object About : Screen()
    object Login : Screen()
    object Register : Screen()
    object Profile : Screen()
}
