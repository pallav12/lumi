package com.desktop.lumi.onboarding.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
    val reminderHour: Int = 8,
    val reminderMinute: Int = 30
)

class OnboardingViewModel(
    private val repo: PersonRepository,
    private val scheduler: NotificationScheduler? = null
) : ViewModel() {

    private val _uiState = MutableStateFlow(OnboardingUiState())
    val uiState: StateFlow<OnboardingUiState> = _uiState

    init {
        viewModelScope.launch {
            repo.getPerson().take(1).first()?.let {
                loadExistingPerson(it)
            }
        }
    }

    fun onNameChange(name: String) {
        _uiState.update { it.copy(name = name) }
    }

    fun loadExistingPerson(person: Person) {
        _uiState.value = OnboardingUiState(
            name = person.name,
            relationshipType = RelationshipType.valueOf(person.relationshipType),
            reminderHour = person.reminderHour.toInt(),
            reminderMinute = person.reminderMinute.toInt()
        )
    }

    fun onRelationshipTypeChange(relationshipType: RelationshipType) {
        _uiState.update { it.copy(relationshipType = relationshipType) }
    }

    fun onReminderTimeChange(hour: Int, minute: Int) {
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
            repo.savePerson(
                Person(
                    id = 1L,
                    name = state.name,
                    relationshipType = state.relationshipType?.name ?: "",
                    reminderHour = state.reminderHour.toLong(),
                    reminderMinute = state.reminderMinute.toLong()
                )
            )
            
            // Schedule daily reminder notification
            scheduler?.scheduleDailyReminder(
                state.reminderHour,
                state.reminderMinute,
                "Time to reflect today 💭"
            )
        }
    }
}
