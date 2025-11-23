package com.desktop.lumi.onboarding.presentation.viewmodel

import com.desktop.lumi.domain.model.Person
import com.desktop.lumi.domain.repository.PersonRepository
import com.desktop.lumi.onboarding.presentation.model.RelationshipType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch


data class OnboardingUiState(
    val name: String = "",
    val relationshipType: RelationshipType? = null,
    val reminderHour: Int = 8,
    val reminderMinute: Int = 30
)

class OnboardingViewModel(
    private val repo: PersonRepository
) {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    private val _person = MutableStateFlow<Person?>(null)
    val person = _person.asStateFlow()
    private val _uiState = MutableStateFlow(OnboardingUiState())
    val uiState: StateFlow<OnboardingUiState> = _uiState

    init {
        scope.launch {
            repo.getPerson().collect { _person.value = it }
        }
    }

    fun onNameChange(name: String) {
        _uiState.value = _uiState.value.copy(name = name)
    }

    fun onRelationshipTypeChange(relationshipType: RelationshipType) {
        _uiState.value = _uiState.value.copy(relationshipType = relationshipType)
    }

    fun onReminderTimeChange(hour: Int, minute: Int) {
        _uiState.value = _uiState.value.copy(reminderHour = hour, reminderMinute = minute)
    }

    fun completeOnboarding() {
        val state = _uiState.value
        scope.launch {
            repo.savePerson(
                Person(
                    id = 1L,
                    name = state.name,
                    relationshipType = state.relationshipType?.name ?: "",
                    reminderHour = state.reminderHour.toLong(),
                    reminderMinute = state.reminderMinute.toLong()
                )
            )
        }
    }
}
