package org.example.project.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import org.example.project.data.model.AppSettings
import org.example.project.data.storage.StorageManager

class SettingsViewModel(
    private val storageManager: StorageManager
) : ViewModel() {

    val appSettings: StateFlow<AppSettings> = storageManager.settingsFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = AppSettings()
        )

    fun saveSettings(settings: AppSettings) {
        storageManager.saveSettings(settings)
    }

    fun clearAllData() {
        storageManager.clearAllData()
    }
}
