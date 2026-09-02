package org.example.project.auth

import io.github.jan.supabase.auth.OtpType
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.Apple
import io.github.jan.supabase.auth.providers.Google
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.auth.providers.builtin.IDToken
import io.github.jan.supabase.auth.status.SessionStatus
import io.github.jan.supabase.auth.user.UserInfo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonPrimitive
import org.example.project.auth.config.SupabaseConfig
import kotlin.concurrent.Volatile

class SupabaseAuthRepository constructor(
    val oauthAuthenticator: OAuthAuthenticator = OAuthAuthenticator(),
) : AuthRepository {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private val supabase = SupabaseProvider.client
    private val _authState = MutableStateFlow<AuthState>(AuthState.Loading)
    override val authState: StateFlow<AuthState> = _authState.asStateFlow()

    @Volatile
    private var pendingPasswordRecovery: Boolean = false

    init {
        scope.launch {
            supabase.auth.sessionStatus.collect { status ->
                _authState.value = status.toAuthState()
            }
        }
    }

    override suspend fun signUp(email: String, password: String, displayName: String?) {
        ensureConfigured()
        val result = supabase.auth.signUpWith(Email) {
            this.email = email
            this.password = password
            if (!displayName.isNullOrBlank()) {
                data = buildJsonObject {
                    put("username", JsonPrimitive(displayName))
                    put("display_name", JsonPrimitive(displayName))
                    put("full_name", JsonPrimitive(displayName))
                }
            }
        }
        if (supabase.auth.currentSessionOrNull() == null && result != null) {
            _authState.value = AuthState.Unauthenticated
        }
    }

    override suspend fun signIn(email: String, password: String) {
        ensureConfigured()
        supabase.auth.signInWith(Email) {
            this.email = email
            this.password = password
        }
    }

    override suspend fun resendVerification(email: String) {
        ensureConfigured()
        supabase.auth.resendEmail(OtpType.Email.SIGNUP, email)
    }

    override suspend fun sendPasswordResetEmail(email: String) {
        ensureConfigured()
        supabase.auth.resetPasswordForEmail(email)
    }

    override suspend fun updatePassword(newPassword: String) {
        ensureConfigured()
        supabase.auth.updateUser {
            password = newPassword
        }
        pendingPasswordRecovery = false
        val user = supabase.auth.currentUserOrNull()?.toAuthUser()
        _authState.value = if (user != null) {
            AuthState.Authenticated(user)
        } else {
            AuthState.Unauthenticated
        }
    }

    override suspend fun signInWithGoogle() {
        ensureConfigured()
        oauthAuthenticator.signInWithGoogle()
    }

    override suspend fun signInWithApple() {
        ensureConfigured()
        oauthAuthenticator.signInWithApple()
    }

    override suspend fun signOut() {
        pendingPasswordRecovery = false
        supabase.auth.signOut()
    }

    override fun onDeepLinkReceived(url: String) {
        val normalized = url.lowercase()
        if (normalized.contains("type=recovery")) {
            pendingPasswordRecovery = true
        }
    }

    internal suspend fun signInWithAppleIdToken(idToken: String, rawNonce: String) {
        supabase.auth.signInWith(IDToken) {
            this.idToken = idToken
            provider = Apple
            nonce = rawNonce
        }
    }

    internal suspend fun signInWithGoogleOAuth() {
        supabase.auth.signInWith(Google)
    }

    internal suspend fun signInWithAppleOAuth() {
        supabase.auth.signInWith(Apple)
    }

    private fun SessionStatus.toAuthState(): AuthState = when (this) {
        is SessionStatus.Initializing -> AuthState.Loading
        is SessionStatus.NotAuthenticated -> AuthState.Unauthenticated
        is SessionStatus.RefreshFailure -> AuthState.Unauthenticated
        is SessionStatus.Authenticated -> {
            val user = session.user?.toAuthUser()
                ?: supabase.auth.currentUserOrNull()?.toAuthUser()
            if (user == null) {
                AuthState.Unauthenticated
            } else if (pendingPasswordRecovery) {
                AuthState.PasswordRecovery(user)
            } else {
                AuthState.Authenticated(user)
            }
        }
    }

    private fun ensureConfigured() {
        if (!SupabaseConfig.isConfigured) {
            throw AuthNotConfiguredException()
        }
    }
}

internal class AuthNotConfiguredException : Exception()

internal fun UserInfo.toAuthUser(): AuthUser {
    val metadata = userMetadata
    fun meta(key: String): String? = metadata?.get(key)?.jsonPrimitive?.contentOrNull
    return AuthUser(
        id = id,
        email = email,
        displayName = meta("full_name") ?: meta("name") ?: meta("display_name") ?: meta("username"),
        avatarUrl = meta("avatar_url") ?: meta("picture"),
    )
}

object AuthModule {
    val repository: AuthRepository by lazy { SupabaseAuthRepository() }
    val viewModel: AuthViewModel by lazy { AuthViewModel(repository) }
}
