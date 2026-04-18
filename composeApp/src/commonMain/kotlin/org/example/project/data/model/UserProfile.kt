package org.example.project.data.model

import kotlinx.serialization.Serializable

@Serializable
data class UserProfile(
    val username: String = "speedtyper",
    val displayName: String = "Speed Typer",
    val email: String = "speedtyper@email.com",
    val bio: String = "just a monkey typing away...",
    val avatarId: String = "monkey",
    val profilePicture: String? = null
)
