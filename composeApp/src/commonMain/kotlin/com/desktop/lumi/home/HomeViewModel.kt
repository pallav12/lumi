package com.desktop.lumi.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.desktop.lumi.common.Screen
import com.desktop.lumi.domain.model.Interaction
import com.desktop.lumi.domain.repository.InsightsRepository
import com.desktop.lumi.domain.repository.InteractionRepository
import com.desktop.lumi.domain.repository.PersonRepository
import com.desktop.lumi.domain.repository.ReflectionRepository
import com.desktop.lumi.home.presentation.InteractionType
import com.desktop.lumi.instantmirror.InsightEngine
import com.desktop.lumi.instantmirror.InstantInsight
import com.desktop.lumi.instantmirror.MessageGenerator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HomeViewModel(
    private val personRepository: PersonRepository,
    private val insightsRepository: InsightsRepository,
    private val reflectionRepository: ReflectionRepository,
    private val interactionRepository: InteractionRepository,
    private val insightEngine: InsightEngine
) : ViewModel() {

    private val _currentScreen = MutableStateFlow<Screen>(Screen.OnboardingName())
    val currentScreen: StateFlow<Screen> = _currentScreen

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState

    init {
        determineInitialScreen()
        observePerson()
        observeTodayReflection()
        observeWeeklyTrend()
    }

    fun clearInstantInsight() {
        _uiState.update { it.copy(instantInsight = null) }
    }

    fun showInstantInsight(currentInteraction: InteractionType?) = viewModelScope.launch {
        if (currentInteraction == null) return@launch

        val history = interactionRepository.getAllInteractions(limit = 30)

        val match = insightEngine.analyze(currentInteraction, history)

        val insight = MessageGenerator.generateFor(match)

        _uiState.update { it.copy(instantInsight = insight) }
    }

    private fun determineInitialScreen() {
        viewModelScope.launch {
            personRepository.getPerson()
                .take(1) // Only check once
                .collect { person ->
                    _currentScreen.value = if (person == null) {
                        Screen.OnboardingName()
                    } else {
                        Screen.Home
                    }
                }
        }
    }

    fun setCurrentScreen(screen: Screen) {
        _currentScreen.value = screen
    }

    // ------------------
    // OBSERVERS
    // ------------------

    private fun observePerson() {
        viewModelScope.launch {
            personRepository.getPerson()
                .collect { person ->
                    if (person != null) {
                        _uiState.update {
                            it.copy(personName = person.name)
                        }
                    }
                }
        }
    }

    private fun observeTodayReflection() {
        viewModelScope.launch {
            reflectionRepository.getTodayReflection()
                .collect { reflection ->
                    _uiState.update { prev ->
                        prev.copy(
                            todayReflection = reflection?.let {
                                ReflectionUiState(
                                    mood = it.mood,
                                    note = it.note
                                )
                            }
                        )
                    }
                }
        }
    }

    private fun observeWeeklyTrend() {
        viewModelScope.launch {
            insightsRepository.getWeeklyTrend()
                .collect { trend ->
                    _uiState.update { prev ->
                        prev.copy(
                            weeklyTrend = WeeklyTrendUiState(
                                moodPoints = trend.moodPoints,
                                positiveCount = trend.positiveCount,
                                negativeCount = trend.negativeCount
                            )
                        )
                    }
                }
        }
    }

    data class HomeUiState(
        val personName: String = "",
        val todayReflection: ReflectionUiState? = null,
        val weeklyTrend: WeeklyTrendUiState = WeeklyTrendUiState(
            moodPoints = emptyList(),
            positiveCount = 0,
            negativeCount = 0
        ),
        val instantInsight: InstantInsight? = null
    )

    data class ReflectionUiState(
        val mood: Int,
        val note: String?
    )

    data class WeeklyTrendUiState(
        val moodPoints: List<Int>,
        val positiveCount: Int,
        val negativeCount: Int
    )
}
