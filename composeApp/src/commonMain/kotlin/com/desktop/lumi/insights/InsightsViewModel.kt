package com.desktop.lumi.insights

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.desktop.lumi.domain.repository.InsightsRepository
import com.desktop.lumi.domain.repository.InteractionRepository
import com.desktop.lumi.domain.repository.ReflectionRepository
import com.desktop.lumi.home.presentation.InteractionType
import com.desktop.lumi.home.presentation.MoodEffect
import com.desktop.lumi.insights.models.TimelineItemUi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class InsightsViewModel(
    private val insightsRepository: InsightsRepository,
    private val reflectionRepository: ReflectionRepository,
    private val interactionRepository: InteractionRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(InsightsUiState())
    val uiState: StateFlow<InsightsUiState> = _uiState

    private val _timeline = MutableStateFlow<List<TimelineItemUi>>(emptyList())
    val timeline: StateFlow<List<TimelineItemUi>> = _timeline

    init {
        observeWeeklyInsights()
        observeTimeline()
    }

    private fun observeWeeklyInsights() {
        viewModelScope.launch {
            insightsRepository.getWeeklyTrend()
                .collect { trend ->

                    val generatedInsights = mutableListOf<String>()

                    if (trend.positiveCount > 3) {
                        generatedInsights += "You experienced several uplifting moments this week."
                    }
                    if (trend.negativeCount > 2) {
                        generatedInsights += "Certain interactions drained your mood more than usual."
                    }
                    if (trend.moodPoints.isNotEmpty()) {
                        generatedInsights += "Your overall emotional pattern remained fairly consistent."
                    }

                    _uiState.value = InsightsUiState(
                        insights = generatedInsights,
                        positiveCount = trend.positiveCount,
                        negativeCount = trend.negativeCount
                    )
                }
        }
    }

    // ----------------------------------------------------
    // TIMELINE (Reflection + Interaction merged)
    // ----------------------------------------------------

    private fun observeTimeline() {
        viewModelScope.launch {
            combine(
                reflectionRepository.getLast7Reflections(),
                interactionRepository.getInteractions()
            ) { reflections, interactions ->

                val reflectionItems = reflections.map {
                    TimelineItemUi.ReflectionItem(
                        id = it.id,
                        mood = it.mood,
                        note = it.note ?: "",
                        timestamp = it.timestamp
                    )
                }

                val interactionItems = interactions.map {

                    val typeEnum = InteractionType.valueOf(it.type)

                    val moodEffectEnum = when (it.moodEffect) {
                        1 -> MoodEffect.Better
                        -1 -> MoodEffect.Worse
                        else -> MoodEffect.Same
                    }

                    TimelineItemUi.InteractionItem(
                        id = it.id,
                        type = typeEnum,
                        moodEffect = moodEffectEnum,
                        timestamp = it.timestamp
                    )
                }

                // merge + sort by time descending
                (reflectionItems + interactionItems)
                    .sortedByDescending { it.timestamp }

            }.collect { merged ->
                _timeline.value = merged
            }
        }
    }

    // ----------------------------------------------------
    // UI STATE MODELS
    // ----------------------------------------------------

    data class InsightsUiState(
        val insights: List<String> = emptyList(),
        val positiveCount: Int = 0,
        val negativeCount: Int = 0
    )
}
