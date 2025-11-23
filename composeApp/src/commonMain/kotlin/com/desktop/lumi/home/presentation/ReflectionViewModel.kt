package com.desktop.lumi.home.presentation

import com.desktop.lumi.domain.repository.ReflectionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class ReflectionViewModel(
    private val reflectionRepository: ReflectionRepository
) {

    private val _uiState = MutableStateFlow(ReflectionUiState())
    val uiState: StateFlow<ReflectionUiState> = _uiState

    data class ReflectionUiState(
        val mood: Int = 3,
        val note: String = ""
    )

    fun onMoodSelected(mood: Int) {
        _uiState.value = _uiState.value.copy(mood = mood)
    }

    fun onNoteChange(note: String) {
        _uiState.value = _uiState.value.copy(note = note)
    }

    fun saveReflection() {
        // Save later
    }
}
