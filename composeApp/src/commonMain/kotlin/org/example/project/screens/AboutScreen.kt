package org.example.project.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.example.project.ThemeColors
import org.example.project.navigation.NavigationManager
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun AboutScreen(
    navigationManager: NavigationManager,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxSize(),
        color = ThemeColors.Background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp)
                .verticalScroll(rememberScrollState())
        ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "About",
                style = TextStyle(
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = ThemeColors.OnSurface
                )
            )
            Button(
                onClick = { navigationManager.navigateBack() },
                colors = ButtonDefaults.buttonColors(
                    containerColor = ThemeColors.Surface
                )
            ) {
                Text("Back")
            }
        }
        
        Spacer(Modifier.height(24.dp))
        
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = ThemeColors.Surface
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Keyboard Warrior",
                    style = TextStyle(
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = ThemeColors.Primary
                    )
                )
                
                Spacer(Modifier.height(8.dp))
                
                Text(
                    text = "Version 1.0.0",
                    style = TextStyle(
                        fontSize = 14.sp,
                        color = ThemeColors.OnSurface.copy(alpha = 0.7f)
                    )
                )
            }
        }
        
        Spacer(Modifier.height(24.dp))
        
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = ThemeColors.Surface
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Text(
                    text = "About Keyboard Warrior",
                    style = TextStyle(
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = ThemeColors.OnSurface
                    )
                )
                
                Spacer(Modifier.height(12.dp))
                
                Text(
                    text = "Keyboard Warrior is a comprehensive typing practice app designed to help you improve your typing speed and accuracy. " +
                            "With multiple game modes including time-based challenges, word count goals, and inspiring quotes, " +
                            "you can practice typing in various engaging ways.",
                    style = TextStyle(
                        fontSize = 14.sp,
                        color = ThemeColors.OnSurface.copy(alpha = 0.8f),
                        lineHeight = 20.sp
                    ),
                    textAlign = TextAlign.Justify
                )
            }
        }
        
        Spacer(Modifier.height(16.dp))
        
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = ThemeColors.Surface
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Text(
                    text = "Features",
                    style = TextStyle(
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = ThemeColors.OnSurface
                    )
                )
                
                Spacer(Modifier.height(12.dp))
                
                FeatureItem("⏱️ Time Mode - Test your speed in timed challenges")
                FeatureItem("📝 Words Mode - Type a specific number of words")
                FeatureItem("💬 Quotes Mode - Practice with inspiring quotes")
                FeatureItem("📊 Statistics - Track your progress over time")
                FeatureItem("🎨 Customizable - Dark theme and preferences")
            }
        }
        
        Spacer(Modifier.height(16.dp))
        
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = ThemeColors.Surface
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Text(
                    text = "Privacy Policy",
                    style = TextStyle(
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = ThemeColors.OnSurface
                    )
                )
                
                Spacer(Modifier.height(12.dp))
                
                Text(
                    text = "Keyboard Warrior respects your privacy. All typing test data is stored locally on your device. " +
                            "We do not collect, transmit, or share any personal information.",
                    style = TextStyle(
                        fontSize = 14.sp,
                        color = ThemeColors.OnSurface.copy(alpha = 0.8f),
                        lineHeight = 20.sp
                    ),
                    textAlign = TextAlign.Justify
                )
            }
        }
        
        Spacer(Modifier.height(16.dp))
        
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = ThemeColors.Surface
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Text(
                    text = "Terms of Service",
                    style = TextStyle(
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = ThemeColors.OnSurface
                    )
                )
                
                Spacer(Modifier.height(12.dp))
                
                Text(
                    text = "By using Keyboard Warrior, you agree to use the app for its intended purpose of improving typing skills. " +
                            "The app is provided as-is without warranties. We are not responsible for any data loss. " +
                            "You may clear all data at any time through the settings menu.",
                    style = TextStyle(
                        fontSize = 14.sp,
                        color = ThemeColors.OnSurface.copy(alpha = 0.8f),
                        lineHeight = 20.sp
                    ),
                    textAlign = TextAlign.Justify
                )
            }
        }
        
        Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
private fun FeatureItem(text: String) {
    Text(
        text = text,
        style = TextStyle(
            fontSize = 14.sp,
            color = ThemeColors.OnSurface.copy(alpha = 0.8f)
        ),
        modifier = Modifier.padding(vertical = 4.dp)
    )
}

@Preview
@Composable
private fun AboutScreenPreview() {
    org.example.project.MobileTypistTheme(darkTheme = true) {
        AboutScreen(
            navigationManager = org.example.project.navigation.NavigationManager()
        )
    }
}

