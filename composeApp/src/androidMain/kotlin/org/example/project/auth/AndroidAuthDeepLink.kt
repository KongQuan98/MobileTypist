package org.example.project.auth

import android.content.Intent
import io.github.jan.supabase.auth.handleDeeplinks

object AndroidAuthDeepLink {
    fun handle(intent: Intent?) {
        if (intent == null) return
        intent.dataString?.let { AuthModule.repository.onDeepLinkReceived(it) }
        SupabaseProvider.client.handleDeeplinks(intent)
    }
}
