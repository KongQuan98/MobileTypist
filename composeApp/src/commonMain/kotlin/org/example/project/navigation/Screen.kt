package org.example.project.navigation

sealed class Screen {
    object Home : Screen()
    object TimeModeScreen : Screen()
    object WordsMode : Screen()
    object QuotesMode : Screen()
    object Statistics : Screen()
    object Settings : Screen()
    object About : Screen()
}


