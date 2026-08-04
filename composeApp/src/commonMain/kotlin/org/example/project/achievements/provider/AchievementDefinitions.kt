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
import mobiletypist.composeapp.generated.resources.Res
import mobiletypist.composeapp.generated.resources.achievement_consistent_desc
import mobiletypist.composeapp.generated.resources.achievement_consistent_title
import mobiletypist.composeapp.generated.resources.achievement_early_bird_desc
import mobiletypist.composeapp.generated.resources.achievement_early_bird_title
import mobiletypist.composeapp.generated.resources.achievement_grandmaster_desc
import mobiletypist.composeapp.generated.resources.achievement_grandmaster_title
import mobiletypist.composeapp.generated.resources.achievement_marathon_desc
import mobiletypist.composeapp.generated.resources.achievement_marathon_title
import mobiletypist.composeapp.generated.resources.achievement_night_owl_desc
import mobiletypist.composeapp.generated.resources.achievement_night_owl_title
import mobiletypist.composeapp.generated.resources.achievement_on_streak_desc
import mobiletypist.composeapp.generated.resources.achievement_on_streak_title
import mobiletypist.composeapp.generated.resources.achievement_perfectionist_desc
import mobiletypist.composeapp.generated.resources.achievement_perfectionist_title
import mobiletypist.composeapp.generated.resources.achievement_sharpshooter_desc
import mobiletypist.composeapp.generated.resources.achievement_sharpshooter_title
import mobiletypist.composeapp.generated.resources.achievement_socialite_desc
import mobiletypist.composeapp.generated.resources.achievement_socialite_title
import mobiletypist.composeapp.generated.resources.achievement_speed_demon_desc
import mobiletypist.composeapp.generated.resources.achievement_speed_demon_title
import mobiletypist.composeapp.generated.resources.achievement_veteran_desc
import mobiletypist.composeapp.generated.resources.achievement_veteran_title
import mobiletypist.composeapp.generated.resources.achievement_wordsmith_desc
import mobiletypist.composeapp.generated.resources.achievement_wordsmith_title
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
            title = Res.string.achievement_speed_demon_title,
            description = Res.string.achievement_speed_demon_desc,
            icon = FeatherIcons.Zap,
            requirement = ReachWpmRequirement(100)
        ),
        AchievementDefinition(
            id = "sharpshooter",
            title = Res.string.achievement_sharpshooter_title,
            description = Res.string.achievement_sharpshooter_desc,
            icon = FeatherIcons.Target,
            requirement = AccuracyRequirement(100)
        ),
        AchievementDefinition(
            id = "on_streak",
            title = Res.string.achievement_on_streak_title,
            description = Res.string.achievement_on_streak_desc,
            icon = FeatherIcons.Star,
            requirement = StreakRequirement(10)
        ),
        AchievementDefinition(
            id = "grandmaster",
            title = Res.string.achievement_grandmaster_title,
            description = Res.string.achievement_grandmaster_desc,
            icon = FeatherIcons.Award,
            requirement = ReachWpmRequirement(150)
        ),
        AchievementDefinition(
            id = "marathon",
            title = Res.string.achievement_marathon_title,
            description = Res.string.achievement_marathon_desc,
            icon = FeatherIcons.Clock,
            requirement = DurationRequirement(24 * 60 * 60)
        ),
        AchievementDefinition(
            id = "wordsmith",
            title = Res.string.achievement_wordsmith_title,
            description = Res.string.achievement_wordsmith_desc,
            icon = FeatherIcons.Type,
            requirement = TotalKeystrokesRequirement(1_000_000L)
        ),
        AchievementDefinition(
            id = "early_bird",
            title = Res.string.achievement_early_bird_title,
            description = Res.string.achievement_early_bird_desc,
            icon = FeatherIcons.Sun,
            requirement = EarlyBirdRequirement()
        ),
        AchievementDefinition(
            id = "night_owl",
            title = Res.string.achievement_night_owl_title,
            description = Res.string.achievement_night_owl_desc,
            icon = FeatherIcons.Moon,
            requirement = NightOwlRequirement()
        ),
        AchievementDefinition(
            id = "consistent",
            title = Res.string.achievement_consistent_title,
            description = Res.string.achievement_consistent_desc,
            icon = FeatherIcons.Activity,
            requirement = StreakRequirement(30)
        ),
        AchievementDefinition(
            id = "socialite",
            title = Res.string.achievement_socialite_title,
            description = Res.string.achievement_socialite_desc,
            icon = FeatherIcons.Share2,
            requirement = SocialShareRequirement(1)
        ),
        AchievementDefinition(
            id = "veteran",
            title = Res.string.achievement_veteran_title,
            description = Res.string.achievement_veteran_desc,
            icon = FeatherIcons.UserCheck,
            requirement = TotalTestsRequirement(1000)
        ),
        AchievementDefinition(
            id = "perfectionist",
            title = Res.string.achievement_perfectionist_title,
            description = Res.string.achievement_perfectionist_desc,
            icon = FeatherIcons.Shield,
            requirement = PerfectTestsRequirement(10)
        )
    )
}
