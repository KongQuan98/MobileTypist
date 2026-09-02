package org.example.project.auth

sealed class AuthState {
    data object Loading : AuthState()
    data object Unauthenticated : AuthState()
    data class Authenticated(val user: AuthUser) : AuthState()
    data class PasswordRecovery(val user: AuthUser) : AuthState()
}
