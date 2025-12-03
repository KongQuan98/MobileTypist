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
fun QuotesModeScreen(
    navigationManager: NavigationManager,
    storageManager: StorageManager,
    modifier: Modifier = Modifier
) {
    val targetText = remember {
        QuotesData.getRandomQuote()
    }
    
    TypingScreen(
        mode = TypingMode.QUOTES,
        targetText = targetText,
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
private fun QuotesModeScreenPreview() {
    org.example.project.MobileTypistTheme(darkTheme = true) {
        QuotesModeScreen(
            navigationManager = org.example.project.navigation.NavigationManager(),
            storageManager = org.example.project.data.StorageManager(
                settings = createSettings()
            )
        )
    }
}

