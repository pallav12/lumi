package com.desktop.lumi.insights

import com.desktop.lumi.domain.repository.InsightsRepository
import com.desktop.lumi.insights.models.TimelineItemUi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class InsightsViewModel(
    private val insightsRepository: InsightsRepository
) {

    private val _uiState = MutableStateFlow(InsightsUiState())
    val uiState: StateFlow<InsightsUiState> = _uiState

    private val _timeline = MutableStateFlow<List<TimelineItemUi>>(emptyList())
    val timeline: StateFlow<List<TimelineItemUi>> = _timeline

    data class InsightsUiState(
        val insights: List<String> = emptyList(),
        val positiveCount: Int = 0,
        val negativeCount: Int = 0
    )
}
