package org.example.project.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import org.example.project.MobileTypistTheme
import org.example.project.data.QuotesData
import org.example.project.data.StorageManager
import org.example.project.data.TypingMode
import org.example.project.data.createSettings
import org.example.project.navigation.NavigationManager
import org.example.project.viewModel.TypingScreenAction
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun WordsModeScreen(
    navigationManager: NavigationManager,
    storageManager: StorageManager,
    modifier: Modifier = Modifier
) {
    val wordOptions = listOf(10, 25, 50, 100)
    var selectedWords by remember { mutableStateOf(10) }
    val targetText = remember(selectedWords) {
        QuotesData.getQuoteForWords(selectedWords)
    }
    
    TypingScreen(
        mode = TypingMode.WORDS,
        targetText = targetText,
        wordOptions = wordOptions,
        action = {
            when (it) {
                is TypingScreenAction.OnTestComplete -> storageManager.saveResult(it.result)
                TypingScreenAction.OnNavigateBack -> {
                    navigationManager.navigateBack()
                }
            }
        },
        modifier = modifier
    )
}

@Preview
@Composable
private fun WordsModeScreenPreview() {
    MobileTypistTheme(darkTheme = true) {
        WordsModeScreen(
            navigationManager = NavigationManager(),
            storageManager = StorageManager(
                settings = createSettings()
            )
        )
    }
}
