package com.desktop.lumi.settings

import com.desktop.lumi.domain.repository.PersonRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class SettingsViewModel(
    private val personRepository: PersonRepository
) {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState

    data class SettingsUiState(
        val personName: String = "",
        val relationshipType: String = "",
        val reminderTime: String = "",
        val notificationsEnabled: Boolean = true
    )

    fun onToggleNotifications(enabled: Boolean) {
        _uiState.value = _uiState.value.copy(notificationsEnabled = enabled)
    }

    fun onExportData() {
        // Export data implementation
    }
}
