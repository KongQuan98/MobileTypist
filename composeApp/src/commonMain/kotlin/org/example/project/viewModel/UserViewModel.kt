package org.example.project.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.example.project.auth.AuthRepository
import org.example.project.auth.AuthState
import org.example.project.data.model.UserProfile
import org.example.project.data.repo.UserRepository

class UserViewModel(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    val userProfile: StateFlow<UserProfile> = userRepository.userProfile
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), UserProfile())

    init {
        // Automatically sync data when user becomes authenticated
        authRepository.authState
            .onEach { state ->
                if (state is AuthState.Authenticated) {
                    sync()
                } else if (state is AuthState.Unauthenticated) {
                    userRepository.clearData()
                }
            }
            .launchIn(viewModelScope)
    }

    fun sync() {
        viewModelScope.launch {
            userRepository.syncData()
        }
    }

    fun updateProfile(username: String, avatarId: String, bio: String) {
        viewModelScope.launch {
            val current = userProfile.value
            userRepository.updateProfile(
                current.copy(
                    username = username,
                    avatarId = avatarId,
                    bio = bio
                )
            )
        }
    }
}
