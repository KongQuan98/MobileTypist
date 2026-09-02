package org.example.project.auth

import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.Apple
import io.github.jan.supabase.auth.providers.Google

actual class OAuthAuthenticator actual constructor() {
    actual suspend fun signInWithGoogle() {
        try {
            SupabaseProvider.client.auth.signInWith(Google)
        } catch (error: Throwable) {
            throw if (error.isUserCancellation()) {
                AuthCancelledException(OAuthProvider.Google)
            } else {
                error
            }
        }
    }

    actual suspend fun signInWithApple() {
        try {
            SupabaseProvider.client.auth.signInWith(Apple)
        } catch (error: Throwable) {
            throw if (error.isUserCancellation()) {
                AuthCancelledException(OAuthProvider.Apple)
            } else {
                error
            }
        }
    }
}

private fun Throwable.isUserCancellation(): Boolean {
    val message = message.orEmpty().lowercase()
    return message.contains("cancel") || message.contains("closed")
}
