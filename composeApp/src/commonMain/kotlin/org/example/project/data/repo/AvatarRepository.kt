package org.example.project.data.repo

import androidx.compose.ui.graphics.vector.ImageVector
import compose.icons.FeatherIcons
import compose.icons.feathericons.Activity
import compose.icons.feathericons.Award
import compose.icons.feathericons.Cpu
import compose.icons.feathericons.FastForward
import compose.icons.feathericons.Frown
import compose.icons.feathericons.Monitor
import compose.icons.feathericons.Moon
import compose.icons.feathericons.Send
import compose.icons.feathericons.Smile
import compose.icons.feathericons.Target
import compose.icons.feathericons.Wind
import compose.icons.feathericons.Zap
import mobiletypist.composeapp.generated.resources.Res
import mobiletypist.composeapp.generated.resources.avatar_blazer
import mobiletypist.composeapp.generated.resources.avatar_bullseye
import mobiletypist.composeapp.generated.resources.avatar_champion
import mobiletypist.composeapp.generated.resources.avatar_diamond
import mobiletypist.composeapp.generated.resources.avatar_dragon
import mobiletypist.composeapp.generated.resources.avatar_flash
import mobiletypist.composeapp.generated.resources.avatar_fox
import mobiletypist.composeapp.generated.resources.avatar_gamer
import mobiletypist.composeapp.generated.resources.avatar_monkey
import mobiletypist.composeapp.generated.resources.avatar_night_owl
import mobiletypist.composeapp.generated.resources.avatar_rocket
import mobiletypist.composeapp.generated.resources.avatar_typist
import org.jetbrains.compose.resources.StringResource

data class Avatar(
    val id: String,
    val name: StringResource,
    val icon: ImageVector,
    val isLocked: Boolean = false
)

object AvatarRepository {
    val avatars = listOf(
        Avatar("monkey", Res.string.avatar_monkey, FeatherIcons.Smile),
        Avatar("typist", Res.string.avatar_typist, FeatherIcons.Monitor),
        Avatar("blazer", Res.string.avatar_blazer, FeatherIcons.Zap, isLocked = true),
        Avatar("flash", Res.string.avatar_flash, FeatherIcons.FastForward, isLocked = true),
        Avatar("bullseye", Res.string.avatar_bullseye, FeatherIcons.Target, isLocked = true),
        Avatar("night_owl", Res.string.avatar_night_owl, FeatherIcons.Moon, isLocked = true),
        Avatar("champion", Res.string.avatar_champion, FeatherIcons.Award, isLocked = true),
        Avatar("diamond", Res.string.avatar_diamond, FeatherIcons.Activity, isLocked = true),
        Avatar("fox", Res.string.avatar_fox, FeatherIcons.Frown, isLocked = true),
        Avatar("dragon", Res.string.avatar_dragon, FeatherIcons.Wind, isLocked = true),
        Avatar("gamer", Res.string.avatar_gamer, FeatherIcons.Cpu, isLocked = true),
        Avatar("rocket", Res.string.avatar_rocket, FeatherIcons.Send, isLocked = true)
    )

    fun getAvatarById(id: String): Avatar = avatars.find { it.id == id } ?: avatars.first()
}
