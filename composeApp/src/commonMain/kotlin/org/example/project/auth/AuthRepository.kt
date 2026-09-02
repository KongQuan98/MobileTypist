package org.example.project.auth

import kotlinx.coroutines.flow.StateFlow

interface AuthRepository {
    val authState: StateFlow<AuthState>

    suspend fun signUp(email: String, password: String, displayName: String?)
    suspend fun signIn(email: String, password: String)
    suspend fun resendVerification(email: String)
    suspend fun sendPasswordResetEmail(email: String)
    suspend fun updatePassword(newPassword: String)
    suspend fun signInWithGoogle()
    suspend fun signInWithApple()
    suspend fun signOut()
    fun onDeepLinkReceived(url: String)
}

expect class OAuthAuthenticator() {
    suspend fun signInWithGoogle()
    suspend fun signInWithApple()
}
