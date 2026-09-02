package org.example.project.auth

data class AuthUser(
    val id: String,
    val email: String?,
    val displayName: String?,
    val avatarUrl: String?,
)
