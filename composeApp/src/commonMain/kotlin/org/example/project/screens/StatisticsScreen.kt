package org.example.project.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.example.project.MobileTypistTheme
import org.example.project.data.TypingTestResult
import org.example.project.navigation.NavigationManager
import org.example.project.utils.formatDate
import org.jetbrains.compose.ui.tooling.preview.Preview

data class StatisticsScreenState(
    val results: List<TypingTestResult> = emptyList(),
    val bestWpm: Int = 0,
    val totalTests: Int = 0
)

@Composable
fun StatisticsScreen(
    navigationManager: NavigationManager,
    statisticsScreenState: StatisticsScreenState,
    modifier: Modifier = Modifier
) {
    val results = statisticsScreenState.results
    val bestWpm = statisticsScreenState.bestWpm
    val totalTests = statisticsScreenState.totalTests

    val averageWpm = if (results.isNotEmpty()) {
        results.map { it.wpm }.average().toInt()
    } else {
        0
    }

    val averageAccuracy = if (results.isNotEmpty()) {
        results.map { it.accuracy }.average().toInt()
    } else {
        0
    }

    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Statistics",
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                )
                Button(
                    onClick = { navigationManager.navigateBack() },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant,
                        contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                ) {
                    Text("Back")
                }
            }

            Spacer(Modifier.height(24.dp))

            // Summary Cards
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatSummaryCard(
                    title = "Best WPM",
                    value = bestWpm.toString(),
                    modifier = Modifier.weight(1f)
                )
                StatSummaryCard(
                    title = "Avg WPM",
                    value = averageWpm.toString(),
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatSummaryCard(
                    title = "Avg Accuracy",
                    value = "$averageAccuracy%",
                    modifier = Modifier.weight(1f)
                )
                StatSummaryCard(
                    title = "Total Tests",
                    value = totalTests.toString(),
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(Modifier.height(24.dp))

            Text(
                text = "Recent Tests",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold,
                    fontFamily = FontFamily.Monospace
                )
            )

            Spacer(Modifier.height(12.dp))

            if (results.isEmpty()) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "No tests completed yet",
                        style = TextStyle(
                            fontSize = 16.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    )
                    Spacer(Modifier.height(16.dp))
                    Button(
                        onClick = { navigationManager.navigateBack() },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Text("Start Your First Test")
                    }
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(results) { result ->
                        ResultCard(result = result)
                    }
                }
            }
        }
    }
}

@Composable
private fun StatSummaryCard(
    title: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = value,
                style = TextStyle(
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = title,
                style = TextStyle(
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            )
        }
    }
}

@Composable
private fun ResultCard(result: TypingTestResult) {
    val dateString = formatDate(result.timestamp)

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = result.mode.name,
                    style = TextStyle(
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                )
                Text(
                    text = dateString,
                    style = TextStyle(
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                )
            }

            Spacer(Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatItem("WPM", result.wpm.toString())
                StatItem("Accuracy", "${result.accuracy}%")
                StatItem("Correct", result.correctChars.toString())
                StatItem("Errors", result.errorCount.toString())
            }
        }
    }
}

@Composable
private fun StatItem(label: String, value: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = value,
            style = TextStyle(
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        )
        Text(
            text = label,
            style = TextStyle(
                fontSize = 10.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
        )
    }
}

@Preview
@Composable
private fun StatisticsScreenPreview() {
    MobileTypistTheme(darkTheme = false) {
        StatisticsScreen(
            navigationManager = NavigationManager(),
            statisticsScreenState = StatisticsScreenState(

            )
        )
    }
}

@Preview
@Composable
private fun StatisticsScreenDarkModePreview() {
    MobileTypistTheme(darkTheme = true) {
        StatisticsScreen(
            navigationManager = NavigationManager(),
            statisticsScreenState = StatisticsScreenState(

            )
        )
    }
}
