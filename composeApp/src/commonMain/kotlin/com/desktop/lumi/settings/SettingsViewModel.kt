package com.desktop.lumi.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.desktop.lumi.db.com.desktop.lumi.NotificationScheduler
import com.desktop.lumi.domain.model.Person
import com.desktop.lumi.domain.repository.PersonRepository
import com.desktop.lumi.onboarding.presentation.model.RelationshipType
import com.desktop.lumi.onboarding.presentation.viewmodel.OnboardingUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val personRepository: PersonRepository,
    private val scheduler: NotificationScheduler
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState

    init {
        loadPersonOnce()
    }

    private fun loadPersonOnce() {
        viewModelScope.launch {
            personRepository.getPerson()
                .take(1)  // ⬅ Only load ONCE
                .collect { person ->
                    if (person != null) {
                        _uiState.value = SettingsUiState(
                            personName = person.name,
                            relationshipType = person.relationshipType,
                            reminderTime = "${person.reminderHour}:${person.reminderMinute.toString().padStart(2, '0')}",
                            notificationsEnabled = true
                        )
                    }
                }
        }
    }

    fun onToggleNotifications(enabled: Boolean) {
        _uiState.update { it.copy(notificationsEnabled = enabled) }
        
        viewModelScope.launch {
            val state = _uiState.value
            if (enabled && state.reminderTime.isNotEmpty()) {
                // Schedule notification when enabled
                val (hour, minute) = state.reminderTime.split(":").map { it.toInt() }
                scheduler.scheduleDailyReminder(
                    hour,
                    minute,
                    "Time to reflect today 💭"
                )
            } else {
                // Cancel notification when disabled
                scheduler.cancelDailyReminder()
            }
        }
    }

    fun saveUpdatedPerson() {
        viewModelScope.launch {
            val state = _uiState.value
            val (hour, minute) = state.reminderTime.split(":").map { it.toInt() }

            val updated = Person(
                id = 0L, // SQLDelight auto ID
                name = state.personName,
                relationshipType = state.relationshipType,
                reminderHour = hour.toLong(),
                reminderMinute = minute.toLong()
            )

            personRepository.savePerson(updated)
            
            // Only schedule if notifications are enabled
            if (state.notificationsEnabled) {
                scheduler.scheduleDailyReminder(
                    updated.reminderHour.toInt(),
                    updated.reminderMinute.toInt(),
                    "Time to reflect today 💭"
                )
            } else {
                scheduler.cancelDailyReminder()
            }
        }

    }

    data class SettingsUiState(
        val personName: String = "",
        val relationshipType: String = "",
        val reminderTime: String = "",
        val notificationsEnabled: Boolean = true
    )
}
