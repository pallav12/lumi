package com.desktop.lumi.home.presentation

import com.desktop.lumi.domain.repository.InteractionRepository
import com.desktop.lumi.home.presentation.InteractionType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class InteractionViewModel(
    private val interactionRepository: InteractionRepository
) {

    private val _uiState = MutableStateFlow(InteractionUiState())
    val uiState: StateFlow<InteractionUiState> = _uiState

    data class InteractionUiState(
        val type: InteractionType = InteractionType.Call,
        val moodEffect: Int = 0
    )

    fun onTypeSelected(type: InteractionType) {
        _uiState.value = _uiState.value.copy(type = type)
    }

    fun onMoodEffectSelected(effect: Int) {
        _uiState.value = _uiState.value.copy(moodEffect = effect)
    }

    fun saveInteraction() {
        // Save later
    }
}
