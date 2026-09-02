package org.example.project.auth

import io.github.jan.supabase.auth.exception.AuthErrorCode
import io.github.jan.supabase.auth.exception.AuthRestException
import io.github.jan.supabase.auth.exception.AuthWeakPasswordException
import io.github.jan.supabase.exceptions.HttpRequestException
import io.github.jan.supabase.exceptions.RestException
import kotlinx.io.IOException

internal enum class AuthOperation {
    SignIn,
    SignUp,
    Verify,
    PasswordReset,
    Google,
    Apple,
    Session,
}

internal fun mapAuthError(
    throwable: Throwable,
    operation: AuthOperation,
): AuthError {
    if (throwable is AuthCancelledException) {
        return when (throwable.provider) {
            OAuthProvider.Google -> AuthError.GoogleCancelled
            OAuthProvider.Apple -> AuthError.AppleCancelled
        }
    }
    if (throwable is AuthWeakPasswordException) {
        return AuthError.WeakPassword
    }

    val rest = throwable as? AuthRestException
    val code = rest?.errorCode
    val raw = listOfNotNull(
        code?.value,
        rest?.error,
        rest?.errorDescription,
        (throwable as? RestException)?.error,
        (throwable as? RestException)?.description,
        throwable.message,
    ).joinToString(" ").lowercase()

    return when {
        throwable is HttpRequestException || throwable is IOException -> AuthError.Network
        isNetworkMessage(raw) -> AuthError.Network
        code == AuthErrorCode.InvalidCredentials || raw.contains("invalid login") ||
                raw.contains("invalid_credentials") -> AuthError.InvalidCredentials

        code == AuthErrorCode.EmailExists || code == AuthErrorCode.UserAlreadyExists ||
                raw.contains("already registered") || raw.contains("email_exists") ||
                raw.contains("user_already_exists") -> AuthError.EmailAlreadyRegistered

        code == AuthErrorCode.EmailNotConfirmed || raw.contains("email_not_confirmed") ||
                raw.contains("email not confirmed") -> AuthError.EmailNotVerified

        code == AuthErrorCode.OtpExpired || raw.contains("otp_expired") -> when (operation) {
            AuthOperation.PasswordReset -> AuthError.ExpiredResetLink
            else -> AuthError.ExpiredVerification
        }

        code == AuthErrorCode.FlowStateExpired || raw.contains("flow_state_expired") ->
            AuthError.InvalidVerification

        rest?.error == "access_denied" || raw.contains("access_denied") -> when (operation) {
            AuthOperation.Google -> AuthError.GoogleCancelled
            AuthOperation.Apple -> AuthError.AppleCancelled
            else -> AuthError.InvalidVerification
        }

        code == AuthErrorCode.WeakPassword || raw.contains("weak_password") -> AuthError.WeakPassword
        code == AuthErrorCode.SessionNotFound || raw.contains("session_not_found") ||
                raw.contains("session expired") -> AuthError.ExpiredSession

        operation == AuthOperation.PasswordReset -> AuthError.PasswordResetFailed
        else -> AuthError.Unknown
    }
}

private fun isNetworkMessage(raw: String): Boolean =
    raw.contains("unable to resolve") ||
            raw.contains("timeout") ||
            raw.contains("failed to connect") ||
            raw.contains("network") ||
            raw.contains("unreachable")
