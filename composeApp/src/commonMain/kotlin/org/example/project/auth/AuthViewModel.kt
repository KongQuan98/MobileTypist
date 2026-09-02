package org.example.project.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AuthUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val infoMessage: String? = null,
    val pendingEmail: String? = null,
)

class AuthViewModel(
    private val repository: AuthRepository,
) : ViewModel() {

    val authState: StateFlow<AuthState> = repository.authState

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    fun signIn(email: String, password: String) {
        if (!validateEmail(email)) return
        launchAuth(AuthOperation.SignIn) {
            repository.signIn(email.trim(), password)
        }
    }

    fun signUp(username: String, email: String, password: String, confirmPassword: String) {
        if (!validateEmail(email)) return
        if (password.length < 6) {
            _uiState.update { it.copy(errorMessage = AuthError.WeakPassword.userMessage) }
            return
        }
        if (password != confirmPassword) {
            _uiState.update { it.copy(errorMessage = AuthError.PasswordsDoNotMatch.userMessage) }
            return
        }
        _uiState.update { it.copy(pendingEmail = email.trim()) }
        launchAuth(AuthOperation.SignUp) {
            repository.signUp(email.trim(), password, username.trim().ifBlank { null })
        }
    }

    fun resendVerification() {
        val email = _uiState.value.pendingEmail
        if (email.isNullOrBlank()) {
            _uiState.update { it.copy(errorMessage = AuthError.InvalidEmail.userMessage) }
            return
        }
        launchAuth(AuthOperation.Verify, successMessage = "Verification email sent.") {
            repository.resendVerification(email)
        }
    }

    fun sendPasswordReset(email: String) {
        if (!validateEmail(email)) return
        _uiState.update { it.copy(pendingEmail = email.trim()) }
        launchAuth(AuthOperation.PasswordReset, successMessage = "Password reset email sent.") {
            repository.sendPasswordResetEmail(email.trim())
        }
    }

    fun updatePassword(password: String, confirmPassword: String) {
        if (password.length < 6) {
            _uiState.update { it.copy(errorMessage = AuthError.WeakPassword.userMessage) }
            return
        }
        if (password != confirmPassword) {
            _uiState.update { it.copy(errorMessage = AuthError.PasswordsDoNotMatch.userMessage) }
            return
        }
        launchAuth(AuthOperation.PasswordReset) {
            repository.updatePassword(password)
        }
    }

    fun signInWithGoogle() {
        launchAuth(AuthOperation.Google) {
            repository.signInWithGoogle()
        }
    }

    fun signInWithApple() {
        launchAuth(AuthOperation.Apple) {
            repository.signInWithApple()
        }
    }

    fun signOut() {
        launchAuth(AuthOperation.Session) {
            repository.signOut()
        }
    }

    fun rememberPendingEmail(email: String) {
        _uiState.update { it.copy(pendingEmail = email.trim()) }
    }

    fun clearMessages() {
        _uiState.update { it.copy(errorMessage = null, infoMessage = null) }
    }

    private fun validateEmail(email: String): Boolean {
        val valid = email.contains("@") && email.contains(".")
        if (!valid) {
            _uiState.update { it.copy(errorMessage = AuthError.InvalidEmail.userMessage) }
        }
        return valid
    }

    private fun launchAuth(
        operation: AuthOperation,
        successMessage: String? = null,
        block: suspend () -> Unit,
    ) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null, infoMessage = null) }
            try {
                block()
                _uiState.update {
                    it.copy(isLoading = false, infoMessage = successMessage)
                }
            } catch (cancelled: CancellationException) {
                _uiState.update { it.copy(isLoading = false) }
                throw cancelled
            } catch (notConfigured: AuthNotConfiguredException) {
                _uiState.update {
                    it.copy(isLoading = false, errorMessage = AuthError.NotConfigured.userMessage)
                }
            } catch (error: Throwable) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = mapAuthError(error, operation).userMessage,
                    )
                }
            }
        }
    }
}
