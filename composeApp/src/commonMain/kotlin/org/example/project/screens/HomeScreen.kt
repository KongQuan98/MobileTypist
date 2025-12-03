package org.example.project.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import compose.icons.FeatherIcons
import compose.icons.feathericons.Activity
import compose.icons.feathericons.BarChart2
import compose.icons.feathericons.Clock
import compose.icons.feathericons.FileText
import compose.icons.feathericons.Settings
import compose.icons.feathericons.Info
import org.example.project.ThemeColors
import org.example.project.data.StorageManager
import org.example.project.data.createSettings
import org.example.project.navigation.NavigationManager
import org.example.project.navigation.Screen
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun HomeScreen(
    navigationManager: NavigationManager,
    storageManager: StorageManager,
    modifier: Modifier = Modifier
) {
    val bestWpm = storageManager.getBestWpm()
    val totalTests = storageManager.getTotalTests()
    
    Surface(
        modifier = modifier.fillMaxSize(),
        color = ThemeColors.Background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
        // App Title
        Text(
            text = "Keyboard Warrior",
            style = TextStyle(
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = ThemeColors.OnSurface
            )
        )
        
        Spacer(Modifier.height(8.dp))
        
        Text(
            text = "Master Your Typing Skills",
            style = TextStyle(
                fontSize = 16.sp,
                color = ThemeColors.OnSurface.copy(alpha = 0.7f)
            )
        )
        
        Spacer(Modifier.height(40.dp))
        
        // Statistics Cards
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            StatCard(
                title = "Best WPM",
                value = bestWpm.toString(),
                icon = FeatherIcons.Activity,
                modifier = Modifier.weight(1f)
            )
            StatCard(
                title = "Tests",
                value = totalTests.toString(),
                icon = FeatherIcons.BarChart2,
                modifier = Modifier.weight(1f)
            )
        }
        
        Spacer(Modifier.height(32.dp))
        
        // Mode Selection
        Text(
            text = "Choose Your Challenge",
            style = TextStyle(
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold,
                color = ThemeColors.OnSurface
            )
        )
        
        Spacer(Modifier.height(16.dp))
        
        ModeButton(
            title = "Time Mode",
            description = "Type as much as you can in a set time",
            icon = FeatherIcons.Clock,
            onClick = { navigationManager.navigateTo(Screen.TimeModeScreen) },
            modifier = Modifier.fillMaxWidth()
        )
        
        Spacer(Modifier.height(12.dp))
        
        ModeButton(
            title = "Words Mode",
            description = "Type a specific number of words",
            icon = FeatherIcons.FileText,
            onClick = { navigationManager.navigateTo(Screen.WordsMode) },
            modifier = Modifier.fillMaxWidth()
        )
        
        Spacer(Modifier.height(12.dp))
        
        ModeButton(
            title = "Quotes Mode",
            description = "Type inspiring quotes and passages",
            icon = FeatherIcons.FileText,
            onClick = { navigationManager.navigateTo(Screen.QuotesMode) },
            modifier = Modifier.fillMaxWidth()
        )
        
        Spacer(Modifier.height(32.dp))
        
        // Bottom Navigation
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            IconButton(
                icon = FeatherIcons.BarChart2,
                label = "Stats",
                onClick = { navigationManager.navigateTo(Screen.Statistics) }
            )
            IconButton(
                icon = FeatherIcons.Settings,
                label = "Settings",
                onClick = { navigationManager.navigateTo(Screen.Settings) }
            )
            IconButton(
                icon = FeatherIcons.Info,
                label = "About",
                onClick = { navigationManager.navigateTo(Screen.About) }
            )
        }
        }
    }
}

@Composable
private fun StatCard(
    title: String,
    value: String,
    icon: ImageVector,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = ThemeColors.Surface
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                modifier = Modifier.size(24.dp),
                tint = ThemeColors.Primary
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = value,
                style = TextStyle(
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = ThemeColors.OnSurface
                )
            )
            Text(
                text = title,
                style = TextStyle(
                    fontSize = 12.sp,
                    color = ThemeColors.OnSurface.copy(alpha = 0.7f)
                )
            )
        }
    }
}

@Composable
private fun ModeButton(
    title: String,
    description: String,
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        modifier = modifier,
        colors = ButtonDefaults.buttonColors(
            containerColor = ThemeColors.Primary
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                modifier = Modifier.size(24.dp)
            )
            Spacer(Modifier.size(16.dp))
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = title,
                    style = TextStyle(
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                )
                Text(
                    text = description,
                    style = TextStyle(
                        fontSize = 12.sp,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                )
            }
        }
    }
}

@Composable
private fun IconButton(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = ThemeColors.Surface
        ),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                modifier = Modifier.size(20.dp),
                tint = ThemeColors.OnSurface
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = label,
                style = TextStyle(
                    fontSize = 10.sp,
                    color = ThemeColors.OnSurface
                )
            )
        }
    }
}

@Preview
@Composable
private fun HomeScreenPreview() {
    org.example.project.MobileTypistTheme(darkTheme = true) {
        HomeScreen(
            navigationManager = org.example.project.navigation.NavigationManager(),
            storageManager = org.example.project.data.StorageManager(
                settings = createSettings()
            )
        )
    }
}









