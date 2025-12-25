package com.desktop.lumi.home.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.desktop.lumi.analytics.Analytics
import com.desktop.lumi.db.com.desktop.lumi.NotificationScheduler
import com.desktop.lumi.domain.model.Interaction
import com.desktop.lumi.domain.repository.InteractionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.time.ExperimentalTime

class InteractionViewModel(
    private val interactionRepository: InteractionRepository,
    private val analytics: Analytics? = null,
    private val scheduler: NotificationScheduler?
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
            scheduler?.scheduleStreakNudge()

            interactionRepository.saveInteraction(interaction)
            
            // Track analytics event
            analytics?.logEvent(
                "interaction_logged",
                mapOf(
                    "type" to state.type.name,
                    "mood_effect" to state.moodEffect
                )
            )
        }
    }
}
