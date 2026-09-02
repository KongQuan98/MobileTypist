package org.example.project.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserProfile(
    @SerialName("id")
    val id: String? = null,
    @SerialName("username")
    val username: String = "guest_${(1000..9999).random()}",
    val email: String? = null,
    @SerialName("bio")
    val bio: String = "just a monkey typing away...",
    @SerialName("avatar")
    val avatarId: String = "monkey",
    val profilePicture: String? = null,
    val isLoggedIn: Boolean = false
) {
    val isGuest: Boolean get() = !isLoggedIn
}
