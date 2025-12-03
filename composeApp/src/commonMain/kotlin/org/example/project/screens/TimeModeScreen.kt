package org.example.project.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import org.example.project.data.QuotesData
import org.example.project.data.StorageManager
import org.example.project.data.TypingMode
import org.example.project.data.createSettings
import org.example.project.navigation.NavigationManager
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun TimeModeScreen(
    navigationManager: NavigationManager,
    storageManager: StorageManager,
    modifier: Modifier = Modifier
) {
    val timeOptions = listOf(15, 30, 60)
    val targetText = remember {
        QuotesData.quotes.joinToString(" ") + " " +
        "The quick brown fox jumps over the lazy dog. This is a comprehensive typing test designed to challenge your speed and accuracy. " +
        "Practice makes perfect when it comes to improving your typing skills. The more you type, the faster and more accurate you become. " +
        "Consistency is key in developing muscle memory for efficient typing. Focus on accuracy first, then gradually increase your speed. " +
        "Remember to maintain proper posture and finger positioning while typing. Take breaks to avoid strain and maintain productivity. " +
        "Technology has revolutionized the way we communicate through written text. Digital literacy includes mastering keyboard skills. " +
        "Many professions require excellent typing abilities for success in today's workplace. Start with basic exercises and progress gradually. " +
        "Challenge yourself with different types of content including numbers, symbols, and special characters. Regular practice will yield results. " +
        "Set realistic goals and track your progress over time. Celebrate small improvements and stay motivated throughout your learning journey."
    }
    
    TypingScreen(
        mode = TypingMode.TIME,
        targetText = targetText,
        timeOptions = timeOptions,
        onTestComplete = { result ->
            storageManager.saveResult(result)
        },
        onBack = {
            navigationManager.navigateBack()
        },
        modifier = modifier
    )
}

@Preview
@Composable
private fun TimeModeScreenPreview() {
    org.example.project.MobileTypistTheme(darkTheme = true) {
        TimeModeScreen(
            navigationManager = org.example.project.navigation.NavigationManager(),
            storageManager = org.example.project.data.StorageManager(
                settings = createSettings()
            )
        )
    }
}
