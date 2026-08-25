package org.example.project.data.model

import kotlinx.serialization.Serializable

@Serializable
data class UserProfile(
    val username: String = "guest_${(1000..9999).random()}",
    val email: String? = null,
    val bio: String = "just a monkey typing away...",
    val avatarId: String = "monkey",
    val profilePicture: String? = null,
    val isLoggedIn: Boolean = false
) {
    val isGuest: Boolean get() = !isLoggedIn
}
