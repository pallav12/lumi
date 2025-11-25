package com.desktop.lumi.home.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.desktop.lumi.domain.model.Interaction
import com.desktop.lumi.domain.repository.InteractionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlin.time.ExperimentalTime

class InteractionViewModel(
    private val interactionRepository: InteractionRepository
) : ViewModel() {

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

    /**
     * Saves interaction log into SQLDelight DB.
     */
    @OptIn(ExperimentalTime::class)
    fun saveInteraction() {
        viewModelScope.launch {
            val state = _uiState.value

            val interaction = Interaction(
                id = 0L, // auto-incremented by SQLDelight
                type = state.type.name, // stored as String in DB
                moodEffect = state.moodEffect,
                timestamp = kotlin.time.Clock.System.now().toEpochMilliseconds()
            )

            interactionRepository.saveInteraction(interaction)

            // Reset UI for next log
            _uiState.value = InteractionUiState()
        }
    }
}
