package org.example.project.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import compose.icons.FeatherIcons
import compose.icons.feathericons.Edit3
import org.example.project.MobileTypistTheme
import org.example.project.data.TypingTestResult
import org.example.project.navigation.NavigationManager
import org.example.project.utils.formatDate
import org.jetbrains.compose.ui.tooling.preview.Preview

data class ProfileScreenState(
    val recentTestResult: List<TypingTestResult> = emptyList(),
    val averageWpm: Int = 0,
    val bestWpm: Int = 0,
    val totalTests: Int = 0
)

@Composable
fun ProfileScreen(
    navigationManager: NavigationManager,
    profileScreenState: ProfileScreenState,
    modifier: Modifier = Modifier
) {
    val results = profileScreenState.recentTestResult
    val bestWpm = profileScreenState.bestWpm
    val totalTests = profileScreenState.totalTests

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
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            contentPadding = PaddingValues(top = 40.dp, bottom = 40.dp)
        ) {
            // Profile Header Section
            item {
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "SP", // Initials
                        style = TextStyle(
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.background,
                            fontFamily = FontFamily.Monospace
                        )
                    )
                }

                Spacer(Modifier.height(16.dp))

                Text(
                    text = "@speedtyper",
                    style = TextStyle(
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontFamily = FontFamily.Monospace
                    )
                )

                Text(
                    text = "member since jan 2024",
                    style = TextStyle(
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontFamily = FontFamily.Monospace
                    )
                )

                Spacer(Modifier.height(16.dp))

                // Edit Profile Button
                Row(
                    modifier = Modifier
                        .border(
                            1.dp,
                            MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f),
                            RoundedCornerShape(8.dp)
                        )
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = FeatherIcons.Edit3,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = "edit profile",
                        style = TextStyle(
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurface,
                            fontFamily = FontFamily.Monospace
                        )
                    )
                }

                Spacer(Modifier.height(40.dp))
            }

            // Summary Stats Cards
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    StatBox(
                        label = "avg wpm",
                        value = averageWpm.toString(),
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.weight(1f)
                    )
                    StatBox(
                        label = "best",
                        value = bestWpm.toString(),
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.weight(1f)
                    )
                    StatBox(
                        label = "tests",
                        value = totalTests.toString(),
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(Modifier.height(32.dp))
            }

            // Global Accuracy
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "global accuracy",
                        style = TextStyle(
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontFamily = FontFamily.Monospace
                        )
                    )
                    Text(
                        text = "$averageAccuracy%",
                        style = TextStyle(
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurface,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                    )
                }
                Spacer(Modifier.height(8.dp))
                LinearProgressIndicator(
                    progress = { averageAccuracy / 100f },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = MaterialTheme.colorScheme.primary,
                    trackColor = MaterialTheme.colorScheme.primary
                )

                Spacer(Modifier.height(40.dp))
            }

            // Recent Tests Section
            item {
                Box(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "RECENT TESTS",
                        style = TextStyle(
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontFamily = FontFamily.Monospace,
                            letterSpacing = 1.sp
                        )
                    )
                }
                Spacer(Modifier.height(16.dp))
            }

            if (results.isEmpty()) {
                item {
                    Text(
                        text = "no tests completed yet",
                        style = TextStyle(
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontFamily = FontFamily.Monospace
                        ),
                        modifier = Modifier.padding(top = 20.dp)
                    )
                }
            } else {
                items(results.reversed()) { result ->
                    ProfileResultItem(result)
                    Spacer(Modifier.height(8.dp))
                }
            }
        }
    }
}

@Composable
private fun StatBox(label: String, value: String, color: Color, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .background(
                Color(MaterialTheme.colorScheme.surface.value).copy(alpha = 0.5f),
                RoundedCornerShape(12.dp)
            )
            .padding(vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = label,
            style = TextStyle(
                fontSize = 11.sp,
                color = Color(0xFF646669),
                fontFamily = FontFamily.Monospace
            )
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = value,
            style = TextStyle(
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = color,
                fontFamily = FontFamily.Monospace
            )
        )
    }
}

@Composable
private fun ProfileResultItem(
    result: TypingTestResult,
) {
    val dateString = formatDate(result.timestamp)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background, RoundedCornerShape(8.dp))
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Mode box (e.g., 30s)
        Box(
            modifier = Modifier
                .border(
                    1.dp,
                    MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
                    RoundedCornerShape(4.dp)
                )
                .padding(horizontal = 6.dp, vertical = 4.dp)
        ) {
            Text(
                text = result.mode.name.take(3).lowercase(),
                style = TextStyle(
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.primary,
                    fontFamily = FontFamily.Monospace
                )
            )
        }

        Spacer(Modifier.width(16.dp))

        // WPM and Time Ago
        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.Bottom) {
                Text(
                    text = result.wpm.toString(),
                    style = TextStyle(
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontFamily = FontFamily.Monospace
                    )
                )
                Spacer(Modifier.width(4.dp))
                Text(
                    text = "wpm",
                    style = TextStyle(
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.primary,
                        fontFamily = FontFamily.Monospace
                    )
                )
            }
            Text(
                text = dateString,
                style = TextStyle(
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.primary,
                    fontFamily = FontFamily.Monospace
                )
            )
        }

        // Accuracy and Dot
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "${result.accuracy}%",
                style = TextStyle(
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontFamily = FontFamily.Monospace
                )
            )
            Spacer(Modifier.width(8.dp))
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary)
            )
        }
    }
}

@Preview
@Composable
private fun ProfileScreenPreviewDark() {
    MobileTypistTheme(darkTheme = true) {
        ProfileScreen(
            navigationManager = NavigationManager(),
            profileScreenState = ProfileScreenState()
        )
    }
}

@Preview
@Composable
private fun ProfileScreenPreview() {
    MobileTypistTheme(darkTheme = false) {
        ProfileScreen(
            navigationManager = NavigationManager(),
            profileScreenState = ProfileScreenState()
        )
    }
}