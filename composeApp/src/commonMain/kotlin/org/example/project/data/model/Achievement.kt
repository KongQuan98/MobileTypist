package org.example.project.data.model

import androidx.compose.ui.graphics.vector.ImageVector
import compose.icons.FeatherIcons
import compose.icons.feathericons.Activity
import compose.icons.feathericons.Award
import compose.icons.feathericons.Clock
import compose.icons.feathericons.Moon
import compose.icons.feathericons.Share2
import compose.icons.feathericons.Shield
import compose.icons.feathericons.Star
import compose.icons.feathericons.Sun
import compose.icons.feathericons.Target
import compose.icons.feathericons.Type
import compose.icons.feathericons.UserCheck
import compose.icons.feathericons.Zap

data class Achievement(
    val id: String,
    val title: String,
    val description: String,
    val icon: ImageVector,
    val isUnlocked: Boolean = false
)

object AchievementRepository {
    val achievements = listOf(
        Achievement("speed_demon", "Speed Demon", "Reach 100 WPM", FeatherIcons.Zap, true),
        Achievement("sharpshooter", "Sharpshooter", "100% Accuracy", FeatherIcons.Target, true),
        Achievement("on_streak", "On Streak", "10 Day Streak", FeatherIcons.Star, true),
        Achievement("grandmaster", "Grandmaster", "Reach 150 WPM", FeatherIcons.Award),
        Achievement("marathon", "Marathon", "Type for 24h", FeatherIcons.Clock),
        Achievement("wordsmith", "Wordsmith", "1M Keystrokes", FeatherIcons.Type),
        Achievement("early_bird", "Early Bird", "Practice before 6am", FeatherIcons.Sun),
        Achievement("night_owl", "Night Owl", "Practice after 10pm", FeatherIcons.Moon),
        Achievement("consistent", "Consistent", "30 Day Streak", FeatherIcons.Activity),
        Achievement("socialite", "Socialite", "Share a result", FeatherIcons.Share2),
        Achievement("veteran", "Veteran", "Complete 1000 tests", FeatherIcons.UserCheck),
        Achievement("perfectionist", "Perfectionist", "No errors in 10 tests", FeatherIcons.Shield)
    )
}
