package org.example.project.auth

import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.Google

actual class OAuthAuthenticator actual constructor() {
    actual suspend fun signInWithGoogle() {
        try {
            SupabaseProvider.client.auth.signInWith(Google)
        } catch (error: Throwable) {
            throw if (error.message.orEmpty().lowercase().contains("cancel")) {
                AuthCancelledException(OAuthProvider.Google)
            } else {
                error
            }
        }
    }

    actual suspend fun signInWithApple() {
        IosAppleSignIn.signIn()
    }
}
