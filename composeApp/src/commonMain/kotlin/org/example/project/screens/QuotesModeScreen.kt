package org.example.project.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import org.example.project.MobileTypistTheme
import org.example.project.data.QuotesData
import org.example.project.data.TypingMode
import org.example.project.viewModel.TypingScreenAction
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun QuotesModeScreen(
    action: (TypingScreenAction) -> Unit,
    modifier: Modifier = Modifier
) {
    val targetText = remember {
        QuotesData.getRandomQuote()
    }

    TypingScreen(
        mode = TypingMode.QUOTES,
        targetText = targetText,
        action = action,
        modifier = modifier
    )
}

@Preview
@Composable
private fun QuotesModeScreenPreview() {
    MobileTypistTheme(darkTheme = false) {
        QuotesModeScreen(
            action = {}
        )
    }
}

@Preview
@Composable
private fun QuotesModeScreenDarkModePreview() {
    MobileTypistTheme(darkTheme = true) {
        QuotesModeScreen(
            action = {}
        )
    }
}