package com.desktop.lumi.home.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.desktop.lumi.domain.model.Interaction
import com.desktop.lumi.domain.repository.InteractionRepository
import com.desktop.lumi.instantmirror.InstantInsight
import com.desktop.lumi.instantmirror.MessageGenerator
import com.desktop.lumi.instantmirror.PatternMatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
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
        _uiState.update { it.copy(type = type) }
    }

    fun onMoodEffectSelected(effect: Int) {
        _uiState.update { it.copy(moodEffect = effect) }
    }

    @OptIn(ExperimentalTime::class)
    fun saveInteraction() {
        viewModelScope.launch {
            val state = _uiState.value
            val now = kotlin.time.Clock.System.now().toEpochMilliseconds()

            val interaction = Interaction(
                id = 0L,
                type = state.type.name,
                moodEffect = state.moodEffect,
                timestamp = now
            )

            interactionRepository.saveInteraction(interaction)
        }
    }
}
