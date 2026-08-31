package org.example.project.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import org.example.project.data.model.UserProfile
import org.example.project.data.storage.StorageManager

class EditProfileViewModel(
    private val storageManager: StorageManager
) : ViewModel() {

    private val _username = MutableStateFlow("")
    val username = _username.asStateFlow()

    private val _email = MutableStateFlow("")
    val email = _email.asStateFlow()

    private val _bio = MutableStateFlow("")
    val bio = _bio.asStateFlow()

    private val _userProfile = storageManager.userProfileFlow
    val userProfile: StateFlow<UserProfile> = _userProfile.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = UserProfile()
    )

    init {
        val currentProfile = storageManager.getUserProfile()
        _username.value = currentProfile.username
        _email.value = currentProfile.email ?: ""
        _bio.value = currentProfile.bio
    }

    fun setUsername(value: String) {
        _username.value = value
    }

    fun setEmail(value: String) {
        _email.value = value
    }

    fun setBio(value: String) {
        _bio.value = value
    }

    fun saveProfile(onSuccess: () -> Unit) {
        val currentProfile = userProfile.value
        val updatedProfile = currentProfile.copy(
            username = _username.value,
            email = _email.value.ifBlank { null },
            bio = _bio.value
        )
        storageManager.saveUserProfile(updatedProfile)
        onSuccess()
    }
}
