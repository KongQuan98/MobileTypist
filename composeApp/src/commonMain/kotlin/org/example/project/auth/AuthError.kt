package org.example.project.auth

sealed class AuthError {
    data object InvalidCredentials : AuthError()
    data object EmailAlreadyRegistered : AuthError()
    data object EmailNotVerified : AuthError()
    data object ExpiredVerification : AuthError()
    data object InvalidVerification : AuthError()
    data object PasswordResetFailed : AuthError()
    data object ExpiredResetLink : AuthError()
    data object GoogleCancelled : AuthError()
    data object AppleCancelled : AuthError()
    data object Network : AuthError()
    data object ExpiredSession : AuthError()
    data object WeakPassword : AuthError()
    data object PasswordsDoNotMatch : AuthError()
    data object InvalidEmail : AuthError()
    data object NotConfigured : AuthError()
    data object Unknown : AuthError()

    val userMessage: String
        get() = when (this) {
            InvalidCredentials -> "Incorrect email or password."
            EmailAlreadyRegistered -> "An account with this email already exists."
            EmailNotVerified -> "Please verify your email before signing in."
            ExpiredVerification -> "This verification link has expired. Request a new one."
            InvalidVerification -> "This verification link is invalid."
            PasswordResetFailed -> "We couldn't reset your password. Try again."
            ExpiredResetLink -> "This password reset link has expired. Request a new one."
            GoogleCancelled -> "Google sign-in was cancelled."
            AppleCancelled -> "Apple sign-in was cancelled."
            Network -> "Check your connection and try again."
            ExpiredSession -> "Your session expired. Please sign in again."
            WeakPassword -> "Choose a stronger password (at least 6 characters)."
            PasswordsDoNotMatch -> "Passwords do not match."
            InvalidEmail -> "Enter a valid email address."
            NotConfigured -> "Sign-in is not configured yet."
            Unknown -> "Something went wrong. Please try again."
        }
}

internal class AuthCancelledException(val provider: OAuthProvider) : Exception()

internal enum class OAuthProvider {
    Google,
    Apple,
}
