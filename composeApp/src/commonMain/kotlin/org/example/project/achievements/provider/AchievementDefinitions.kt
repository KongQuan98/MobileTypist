package org.example.project.achievements.provider

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
import org.example.project.achievements.model.AchievementDefinition
import org.example.project.achievements.requirements.AccuracyRequirement
import org.example.project.achievements.requirements.DurationRequirement
import org.example.project.achievements.requirements.EarlyBirdRequirement
import org.example.project.achievements.requirements.NightOwlRequirement
import org.example.project.achievements.requirements.PerfectTestsRequirement
import org.example.project.achievements.requirements.ReachWpmRequirement
import org.example.project.achievements.requirements.SocialShareRequirement
import org.example.project.achievements.requirements.StreakRequirement
import org.example.project.achievements.requirements.TotalKeystrokesRequirement
import org.example.project.achievements.requirements.TotalTestsRequirement

object AchievementDefinitions {
    val all = listOf(
        AchievementDefinition(
            id = "speed_demon",
            title = "Speed Demon",
            description = "Reach 100 WPM",
            icon = FeatherIcons.Zap,
            requirement = ReachWpmRequirement(100)
        ),
        AchievementDefinition(
            id = "sharpshooter",
            title = "Sharpshooter",
            description = "100% Accuracy",
            icon = FeatherIcons.Target,
            requirement = AccuracyRequirement(100)
        ),
        AchievementDefinition(
            id = "on_streak",
            title = "On Streak",
            description = "10 Day Streak",
            icon = FeatherIcons.Star,
            requirement = StreakRequirement(10)
        ),
        AchievementDefinition(
            id = "grandmaster",
            title = "Grandmaster",
            description = "Reach 150 WPM",
            icon = FeatherIcons.Award,
            requirement = ReachWpmRequirement(150)
        ),
        AchievementDefinition(
            id = "marathon",
            title = "Marathon",
            description = "Type for 24h total",
            icon = FeatherIcons.Clock,
            requirement = DurationRequirement(24 * 60 * 60)
        ),
        AchievementDefinition(
            id = "wordsmith",
            title = "Wordsmith",
            description = "1M Keystrokes",
            icon = FeatherIcons.Type,
            requirement = TotalKeystrokesRequirement(1_000_000L)
        ),
        AchievementDefinition(
            id = "early_bird",
            title = "Early Bird",
            description = "Practice before 6am",
            icon = FeatherIcons.Sun,
            requirement = EarlyBirdRequirement()
        ),
        AchievementDefinition(
            id = "night_owl",
            title = "Night Owl",
            description = "Practice after 10pm",
            icon = FeatherIcons.Moon,
            requirement = NightOwlRequirement()
        ),
        AchievementDefinition(
            id = "consistent",
            title = "Consistent",
            description = "30 Day Streak",
            icon = FeatherIcons.Activity,
            requirement = StreakRequirement(30)
        ),
        AchievementDefinition(
            id = "socialite",
            title = "Socialite",
            description = "Share a result",
            icon = FeatherIcons.Share2,
            requirement = SocialShareRequirement(1)
        ),
        AchievementDefinition(
            id = "veteran",
            title = "Veteran",
            description = "Complete 1000 tests",
            icon = FeatherIcons.UserCheck,
            requirement = TotalTestsRequirement(1000)
        ),
        AchievementDefinition(
            id = "perfectionist",
            title = "Perfectionist",
            description = "No errors in 10 consecutive tests",
            icon = FeatherIcons.Shield,
            requirement = PerfectTestsRequirement(10)
        )
    )
}
