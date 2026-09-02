package org.example.project.auth

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.auth.FlowType
import io.github.jan.supabase.createSupabaseClient
import org.example.project.auth.config.SupabaseConfig

internal object SupabaseProvider {
    val client: SupabaseClient by lazy {
        createSupabaseClient(
            supabaseUrl = SupabaseConfig.url,
            supabaseKey = SupabaseConfig.publishableKey,
        ) {
            install(Auth) {
                scheme = AuthDeepLink.SCHEME
                host = AuthDeepLink.HOST
                defaultRedirectUrl = AuthDeepLink.CALLBACK_URL
                flowType = FlowType.PKCE
                autoLoadFromStorage = true
                autoSaveToStorage = true
                alwaysAutoRefresh = true
            }
        }
    }
}
