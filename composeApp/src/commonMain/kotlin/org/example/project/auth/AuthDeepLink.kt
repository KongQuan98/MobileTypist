package org.example.project.auth

internal object AuthDeepLink {
    const val SCHEME = "com.keyboardwarrior.typing"
    const val HOST = "auth"
    const val PATH = "/callback"
    const val CALLBACK_URL = "$SCHEME://$HOST$PATH"
}
