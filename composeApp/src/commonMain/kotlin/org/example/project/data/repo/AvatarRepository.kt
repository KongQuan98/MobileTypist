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

data class Avatar(
    val id: String,
    val name: String,
    val icon: ImageVector,
    val isLocked: Boolean = false
)

object AvatarRepository {
    val avatars = listOf(
        Avatar("monkey", "Monkey", FeatherIcons.Smile),
        Avatar("typist", "Typist", FeatherIcons.Monitor),
        Avatar("blazer", "Blazer", FeatherIcons.Zap, isLocked = true),
        Avatar("flash", "Flash", FeatherIcons.FastForward, isLocked = true),
        Avatar("bullseye", "Bullseye", FeatherIcons.Target, isLocked = true),
        Avatar("night_owl", "Night Owl", FeatherIcons.Moon, isLocked = true),
        Avatar("champion", "Champion", FeatherIcons.Award, isLocked = true),
        Avatar("diamond", "Diamond", FeatherIcons.Activity, isLocked = true),
        Avatar("fox", "Fox", FeatherIcons.Frown, isLocked = true),
        Avatar("dragon", "Dragon", FeatherIcons.Wind, isLocked = true),
        Avatar("gamer", "Gamer", FeatherIcons.Cpu, isLocked = true),
        Avatar("rocket", "Rocket", FeatherIcons.Send, isLocked = true)
    )

    fun getAvatarById(id: String): Avatar = avatars.find { it.id == id } ?: avatars.first()
}
