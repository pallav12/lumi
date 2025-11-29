package com.desktop.lumi.onboarding.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.desktop.lumi.analytics.Analytics
import com.desktop.lumi.db.com.desktop.lumi.NotificationScheduler
import com.desktop.lumi.domain.model.Person
import com.desktop.lumi.domain.repository.PersonRepository
import com.desktop.lumi.onboarding.presentation.model.RelationshipType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class OnboardingUiState(
    val name: String = "",
    val relationshipType: RelationshipType? = null,
    val reminderHour: Int = 20, // Default to 8 PM
    val reminderMinute: Int = 0
)

class OnboardingViewModel(
    private val repo: PersonRepository,
    private val scheduler: NotificationScheduler? = null,
    private val analytics: Analytics? = null
) : ViewModel() {

    private val _uiState = MutableStateFlow(OnboardingUiState())
    val uiState: StateFlow<OnboardingUiState> = _uiState

    init {
        // Pre-load data if user is editing settings
        viewModelScope.launch {
            val existingPerson = repo.getPerson().take(1).first()
            if (existingPerson != null) {
                _uiState.update {
                    it.copy(
                        name = existingPerson.name,
                        relationshipType = try {
                            RelationshipType.valueOf(existingPerson.relationshipType)
                        } catch (e: Exception) {
                            null // Fallback if enum changed
                        },
                        reminderHour = existingPerson.reminderHour.toInt(),
                        reminderMinute = existingPerson.reminderMinute.toInt()
                    )
                }
            }
        }
    }

    fun onNameChange(name: String) {
        _uiState.update { it.copy(name = name) }
    }

    fun onRelationshipTypeSelect(type: RelationshipType) {
        _uiState.update { it.copy(relationshipType = type) }
    }

    fun onTimeChange(hour: Int, minute: Int) {
        _uiState.update {
            it.copy(
                reminderHour = hour,
                reminderMinute = minute
            )
        }
    }

    fun completeOnboarding() {
        val state = _uiState.value

        viewModelScope.launch {
            // 1. Save to Database
            repo.savePerson(
                Person(
                    id = 1L, // Single user architecture
                    name = state.name,
                    relationshipType = state.relationshipType?.name ?: "Situationship",
                    reminderHour = state.reminderHour.toLong(),
                    reminderMinute = state.reminderMinute.toLong()
                )
            )

            // 2. Schedule Notification
            try {
                scheduler?.scheduleDailyReminder(
                    state.reminderHour,
                    state.reminderMinute,
                    "Time to reflect on your day with ${state.name} 🌙"
                )
            } catch (e: Exception) {
                // Log error but don't crash; onboarding is still "complete" even if alarm fails
                println("Failed to schedule reminder: ${e.message}")
            }

            // 3. Track analytics event
            analytics?.logEvent(
                "onboarding_complete",
                mapOf(
                    "relationship_type" to (state.relationshipType?.name ?: "unknown"),
                    "reminder_hour" to state.reminderHour,
                    "reminder_minute" to state.reminderMinute
                )
            )
        }
    }
}