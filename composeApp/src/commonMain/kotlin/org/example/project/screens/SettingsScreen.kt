package org.example.project.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import mobiletypist.composeapp.generated.resources.Res
import mobiletypist.composeapp.generated.resources.app_name
import mobiletypist.composeapp.generated.resources.settings_account
import mobiletypist.composeapp.generated.resources.settings_appearance
import mobiletypist.composeapp.generated.resources.settings_dark_theme
import mobiletypist.composeapp.generated.resources.settings_font_family
import mobiletypist.composeapp.generated.resources.settings_font_size
import mobiletypist.composeapp.generated.resources.settings_gameplay
import mobiletypist.composeapp.generated.resources.settings_haptic_feedback
import mobiletypist.composeapp.generated.resources.settings_language
import mobiletypist.composeapp.generated.resources.settings_reset_statistics
import mobiletypist.composeapp.generated.resources.settings_sign_out
import mobiletypist.composeapp.generated.resources.settings_sound_effects
import mobiletypist.composeapp.generated.resources.settings_theme
import mobiletypist.composeapp.generated.resources.settings_title
import mobiletypist.composeapp.generated.resources.version
import org.example.project.MobileTypistTheme
import org.example.project.data.model.AppSettings
import org.example.project.theme.AppColorTheme
import org.example.project.theme.TypingFontFamily
import org.example.project.theme.TypingFontSize
import org.example.project.ui.SettingsOptionSheet
import org.example.project.ui.ThemeColorSwatch
import org.example.project.utils.AudioPlayer
import org.example.project.utils.LocalHaptics
import org.example.project.utils.PreviewCompositionLocals
import org.example.project.utils.hapticClickable
import org.example.project.utils.wrap
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

sealed interface SettingsScreenAction {
    object Back : SettingsScreenAction
    object ClearAllData : SettingsScreenAction
    data class SaveSettings(val settings: AppSettings) : SettingsScreenAction
}

private enum class SettingsPicker {
    Theme,
    FontSize,
    FontFamily,
}

private sealed interface SettingsListItem {
    data class Header(val title: String) : SettingsListItem
    data class NavRow(val label: String, val value: String, val onClick: () -> Unit) :
        SettingsListItem
    data class Toggle(
        val label: String,
        val checked: Boolean,
        val onCheckedChange: (Boolean) -> Unit,
    ) : SettingsListItem

    data class ActionRow(
        val label: String,
        val isDestructive: Boolean,
        val onClick: () -> Unit,
    ) : SettingsListItem

    data object Spacer40 : SettingsListItem
    data object Footer : SettingsListItem
}

@Composable
fun SettingsScreen(
    action: (SettingsScreenAction) -> Unit = {},
    appSettings: AppSettings = AppSettings(),
    audioPlayer: AudioPlayer? = null,
    modifier: Modifier = Modifier,
) {
    var activePicker by remember { mutableStateOf<SettingsPicker?>(null) }

    val themeLabel = stringResource(appSettings.colorTheme.labelRes)
    val fontSizeLabel = stringResource(appSettings.typingFontSize.labelRes)
    val fontFamilyLabel = stringResource(appSettings.typingFontFamily.labelRes)

    val appearanceHeader = stringResource(Res.string.settings_appearance)
    val themeSettingLabel = stringResource(Res.string.settings_theme)
    val fontSizeSettingLabel = stringResource(Res.string.settings_font_size)
    val fontFamilySettingLabel = stringResource(Res.string.settings_font_family)
    val darkThemeLabel = stringResource(Res.string.settings_dark_theme)
    val gameplayHeader = stringResource(Res.string.settings_gameplay)
    val soundEffectsLabel = stringResource(Res.string.settings_sound_effects)
    val hapticFeedbackLabel = stringResource(Res.string.settings_haptic_feedback)
    val accountHeader = stringResource(Res.string.settings_account)
    val languageLabel = stringResource(Res.string.settings_language)
    val resetStatisticsLabel = stringResource(Res.string.settings_reset_statistics)
    val signOutLabel = stringResource(Res.string.settings_sign_out)
    val appName = stringResource(Res.string.app_name)
    val appVersion = stringResource(Res.string.version)

    val items = remember(
        appSettings,
        themeLabel,
        fontSizeLabel,
        fontFamilyLabel,
        appearanceHeader,
        themeSettingLabel,
        fontSizeSettingLabel,
        fontFamilySettingLabel,
        darkThemeLabel,
        gameplayHeader,
        soundEffectsLabel,
        hapticFeedbackLabel,
        accountHeader,
        languageLabel,
        resetStatisticsLabel,
        signOutLabel,
    ) {
        listOf(
            SettingsListItem.Header(appearanceHeader),
            SettingsListItem.NavRow(
                label = themeSettingLabel,
                value = themeLabel,
                onClick = { activePicker = SettingsPicker.Theme },
            ),
            SettingsListItem.NavRow(
                label = fontSizeSettingLabel,
                value = fontSizeLabel,
                onClick = { activePicker = SettingsPicker.FontSize },
            ),
            SettingsListItem.NavRow(
                label = fontFamilySettingLabel,
                value = fontFamilyLabel,
                onClick = { activePicker = SettingsPicker.FontFamily },
            ),
            SettingsListItem.Toggle(
                label = darkThemeLabel,
                checked = appSettings.darkTheme,
                onCheckedChange = {
                    action(SettingsScreenAction.SaveSettings(appSettings.copy(darkTheme = it)))
                },
            ),
            SettingsListItem.Spacer40,
            SettingsListItem.Header(gameplayHeader),
            SettingsListItem.Toggle(
                label = soundEffectsLabel,
                checked = appSettings.soundEnabled,
                onCheckedChange = {
                    action(SettingsScreenAction.SaveSettings(appSettings.copy(soundEnabled = it)))
                },
            ),
            SettingsListItem.Toggle(
                label = hapticFeedbackLabel,
                checked = appSettings.vibrationEnabled,
                onCheckedChange = {
                    action(SettingsScreenAction.SaveSettings(appSettings.copy(vibrationEnabled = it)))
                },
            ),
            SettingsListItem.Spacer40,
            SettingsListItem.Header(accountHeader),
            SettingsListItem.NavRow(
                label = languageLabel,
                value = "english",
                onClick = { },
            ),
            SettingsListItem.Spacer40,
            SettingsListItem.ActionRow(
                label = resetStatisticsLabel,
                isDestructive = true,
                onClick = { action(SettingsScreenAction.ClearAllData) },
            ),
            SettingsListItem.ActionRow(
                label = signOutLabel,
                isDestructive = true,
                onClick = { },
            ),
            SettingsListItem.Spacer40,
            SettingsListItem.Footer,
        )
    }

    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background,
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
        ) {
            Spacer(Modifier.height(40.dp))

            Text(
                text = stringResource(Res.string.settings_title),
                style = TextStyle(
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontFamily = FontFamily.Monospace,
                ),
            )

            Spacer(Modifier.height(20.dp))

            HorizontalDivider(
                thickness = 1.dp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f),
            )

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(top = 20.dp, bottom = 20.dp),
            ) {
                itemsIndexed(
                    items = items,
                    key = { index, item ->
                        when (item) {
                            is SettingsListItem.Header -> "header-${item.title}"
                            is SettingsListItem.NavRow -> "nav-${item.label}"
                            is SettingsListItem.Toggle -> "toggle-${item.label}"
                            is SettingsListItem.ActionRow -> "action-${item.label}"
                            SettingsListItem.Spacer40 -> "spacer-$index"
                            SettingsListItem.Footer -> "footer"
                        }
                    },
                ) { _, item ->
                    AnimatedVisibility(
                        visible = true,
                        enter = fadeIn() + slideInVertically(initialOffsetY = { it / 2 }),
                    ) {
                        when (item) {
                            is SettingsListItem.Header -> SettingSectionHeader(item.title)
                            is SettingsListItem.NavRow -> SettingNavRow(
                                label = item.label,
                                value = item.value,
                                onClick = item.onClick,
                            )

                            is SettingsListItem.Toggle -> SettingToggleRow(
                                label = item.label,
                                checked = item.checked,
                                audioPlayer = audioPlayer,
                                onCheckedChange = item.onCheckedChange,
                            )
                            is SettingsListItem.ActionRow -> {
                                Text(
                                    text = item.label,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .hapticClickable(onClick = item.onClick)
                                        .padding(vertical = 12.dp),
                                    style = TextStyle(
                                        color = MaterialTheme.colorScheme.error,
                                        fontSize = 16.sp,
                                        fontFamily = FontFamily.Monospace,
                                    ),
                                    textAlign = TextAlign.Center,
                                )
                            }
                            SettingsListItem.Spacer40 -> Spacer(Modifier.height(40.dp))
                            SettingsListItem.Footer -> {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 24.dp),
                                    contentAlignment = Alignment.Center,
                                ) {
                                    Text(
                                        text = "$appName $appVersion",
                                        style = TextStyle(
                                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(
                                                alpha = 0.5f
                                            ),
                                            fontSize = 12.sp,
                                            fontFamily = FontFamily.Monospace,
                                        ),
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    SettingsOptionSheet(
        visible = activePicker == SettingsPicker.Theme,
        title = themeSettingLabel,
        options = AppColorTheme.entries,
        selected = appSettings.colorTheme,
        onDismiss = { activePicker = null },
        optionLabel = { stringResource(it.labelRes) },
        optionLeading = { ThemeColorSwatch(it.previewColor) },
        onSelect = { theme ->
            action(SettingsScreenAction.SaveSettings(appSettings.copy(colorTheme = theme)))
        },
    )

    SettingsOptionSheet(
        visible = activePicker == SettingsPicker.FontSize,
        title = fontSizeSettingLabel,
        options = TypingFontSize.entries,
        selected = appSettings.typingFontSize,
        onDismiss = { activePicker = null },
        optionLabel = { stringResource(it.labelRes) },
        onSelect = { size ->
            action(SettingsScreenAction.SaveSettings(appSettings.copy(typingFontSize = size)))
        },
    )

    SettingsOptionSheet(
        visible = activePicker == SettingsPicker.FontFamily,
        title = fontFamilySettingLabel,
        options = TypingFontFamily.entries,
        selected = appSettings.typingFontFamily,
        onDismiss = { activePicker = null },
        optionLabel = { stringResource(it.labelRes) },
        onSelect = { family ->
            action(SettingsScreenAction.SaveSettings(appSettings.copy(typingFontFamily = family)))
        },
    )
}

@Composable
private fun SettingSectionHeader(title: String) {
    Text(
        text = title,
        style = TextStyle(
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 18.sp,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.ExtraBold,
        ),
        modifier = Modifier.padding(vertical = 8.dp),
    )
}

@Composable
private fun SettingNavRow(
    label: String,
    value: String,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = label,
            style = TextStyle(
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = 16.sp,
                fontFamily = FontFamily.Monospace,
            ),
        )
        Row(
            modifier = Modifier
                .hapticClickable(onClick = onClick)
                .clip(RoundedCornerShape(12.dp))
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = value,
                style = TextStyle(
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 14.sp,
                    fontFamily = FontFamily.Monospace,
                ),
                textAlign = TextAlign.End,
            )
            Spacer(Modifier.width(8.dp))
            Text(
                text = ">",
                style = TextStyle(
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 14.sp,
                    fontFamily = FontFamily.Monospace,
                ),
            )
        }
    }

    HorizontalDivider(
        thickness = 1.dp,
        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f),
    )
}

@Composable
private fun SettingToggleRow(
    label: String,
    checked: Boolean,
    audioPlayer: AudioPlayer?,
    onCheckedChange: (Boolean) -> Unit,
) {
    val haptics = LocalHaptics.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = label,
            style = TextStyle(
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = 16.sp,
                fontFamily = FontFamily.Monospace,
            ),
        )
        Switch(
            checked = checked,
            onCheckedChange = {
                haptics.wrap(audioPlayer) {
                    onCheckedChange(it)
                }
            },
            colors = SwitchDefaults.colors(
                checkedThumbColor = MaterialTheme.colorScheme.onBackground,
                checkedTrackColor = MaterialTheme.colorScheme.primary,
                uncheckedThumbColor = MaterialTheme.colorScheme.onBackground,
                uncheckedTrackColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f),
                uncheckedBorderColor = Color.Transparent,
            ),
        )
    }

    HorizontalDivider(
        thickness = 1.dp,
        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f),
    )
}

@Preview
@Composable
private fun SettingsScreenPreview() {
    PreviewCompositionLocals {
        MobileTypistTheme(darkTheme = false) {
            SettingsScreen()
        }
    }
}

@Preview
@Composable
private fun SettingsScreenPreviewDarkTheme() {
    PreviewCompositionLocals {
        MobileTypistTheme(darkTheme = true) {
            SettingsScreen()
        }
    }
}
