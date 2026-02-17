package org.example.project.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.example.project.MobileTypistTheme
import org.example.project.data.StorageManager
import org.example.project.data.createSettings
import org.example.project.getPlatform
import org.example.project.navigation.NavigationManager
import org.example.project.utils.LiquidToggle
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun SettingsScreen(
    navigationManager: NavigationManager,
    storageManager: StorageManager,
    onThemeChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val currentSettings = storageManager.getSettings()
    var darkTheme by remember { mutableStateOf(currentSettings.darkTheme) }
    var soundEnabled by remember { mutableStateOf(currentSettings.soundEnabled) }
    var vibrationEnabled by remember { mutableStateOf(currentSettings.vibrationEnabled) }
    var showStatistics by remember { mutableStateOf(currentSettings.showStatistics) }
    
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
                    text = "Settings",
                    style = TextStyle(
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
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

            // Settings Cards
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = "Appearance",
                        style = TextStyle(
                            fontSize = 18.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    )

                    Spacer(Modifier.height(16.dp))

                    SettingRow(
                        title = "Dark Theme",
                        description = "Use dark theme for better visibility in low light",
                        checked = darkTheme,
                        onCheckedChange = {
                            darkTheme = it
                            onThemeChange(it)
                            storageManager.saveSettings(
                                currentSettings.copy(darkTheme = it)
                            )
                        }
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = "Preferences",
                        style = TextStyle(
                            fontSize = 18.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    )

                    Spacer(Modifier.height(16.dp))

                    SettingRow(
                        title = "Sound Effects",
                        description = "Play sounds for test completion",
                        checked = soundEnabled,
                        onCheckedChange = {
                            soundEnabled = it
                            storageManager.saveSettings(
                                currentSettings.copy(soundEnabled = it)
                            )
                        }
                    )

                    Spacer(Modifier.height(12.dp))

                    SettingRow(
                        title = "Vibration",
                        description = "Vibrate on test completion",
                        checked = vibrationEnabled,
                        onCheckedChange = {
                            vibrationEnabled = it
                            storageManager.saveSettings(
                                currentSettings.copy(vibrationEnabled = it)
                            )
                        }
                    )

                    Spacer(Modifier.height(12.dp))

                    SettingRow(
                        title = "Show Statistics",
                        description = "Display statistics on home screen",
                        checked = showStatistics,
                        onCheckedChange = {
                            showStatistics = it
                            storageManager.saveSettings(
                                currentSettings.copy(showStatistics = it)
                            )
                        }
                    )
                }
            }

            Spacer(Modifier.height(24.dp))

            Button(
                onClick = {
                    storageManager.clearAllData()
                    navigationManager.navigateBack()
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFE57373)
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "Clear All Data",
                    style = TextStyle(
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.White
                    )
                )
            }
        }
    }
}

@Preview
@Composable
private fun SettingsScreenPreview() {
    MobileTypistTheme(darkTheme = true) {
        SettingsScreen(
            navigationManager = NavigationManager(),
            storageManager = StorageManager(
                settings = createSettings()
            ),
            onThemeChange = {}
        )
    }
}

@Composable
private fun SettingRow(
    title: String,
    description: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = title,
                style = TextStyle(
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            )
            Text(
                text = description,
                style = TextStyle(
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            )
        }
        when (getPlatform().name == "Android") {
            true -> {
                LiquidToggle(
                    checked = checked,
                    onCheckedChange = onCheckedChange
                )
            }
            else -> {
                Switch(
                    checked = checked,
                    onCheckedChange = onCheckedChange
                )
            }
        }
    }
}
