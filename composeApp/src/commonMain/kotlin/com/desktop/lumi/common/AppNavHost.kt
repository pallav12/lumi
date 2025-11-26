package com.desktop.lumi.common

import androidx.compose.runtime.Composable
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.desktop.lumi.home.HomeViewModel
import com.desktop.lumi.home.presentation.InteractionViewModel
import com.desktop.lumi.home.presentation.ReflectionViewModel
import com.desktop.lumi.insights.InsightsViewModel
import com.desktop.lumi.settings.SettingsViewModel
import com.desktop.lumi.home.presentation.DailyReflectionScreen
import com.desktop.lumi.home.presentation.HomeScreen
import com.desktop.lumi.home.presentation.InteractionLogScreen
import com.desktop.lumi.home.presentation.MoodEffect
import com.desktop.lumi.insights.InsightsScreen
import com.desktop.lumi.insights.TimelineScreen
import com.desktop.lumi.onboarding.presentation.composable.OnboardingNameScreen
import com.desktop.lumi.onboarding.presentation.composable.OnboardingRelationshipTypeScreen
import com.desktop.lumi.onboarding.presentation.composable.OnboardingReminderTimeScreen
import com.desktop.lumi.onboarding.presentation.viewmodel.OnboardingViewModel
import com.desktop.lumi.settings.SettingsScreen

@Composable
fun AppNavHost(
    homeViewModel: HomeViewModel,
    onboardingViewModel: OnboardingViewModel,
    reflectionViewModel: ReflectionViewModel,
    interactionViewModel: InteractionViewModel,
    insightsViewModel: InsightsViewModel,
    settingsViewModel: SettingsViewModel
) {
    val current = homeViewModel.currentScreen.collectAsStateWithLifecycle().value

    when (current) {

        is Screen.OnboardingName -> {
            OnboardingNameScreen(
                name = onboardingViewModel.uiState.collectAsStateWithLifecycle().value.name,
                onNameChange = { onboardingViewModel.onNameChange(it) },
                onNext = { homeViewModel.setCurrentScreen(Screen.OnboardingType(current.fromSettings)) },
                onBack = {
                    if ((current ).fromSettings) {
                        homeViewModel.setCurrentScreen(Screen.Settings)
                    }
                }
            )
        }

        is Screen.OnboardingType -> {
            val state = onboardingViewModel.uiState.collectAsStateWithLifecycle().value
            OnboardingRelationshipTypeScreen(
                selectedType = state.relationshipType,
                onSelectType = { onboardingViewModel.onRelationshipTypeChange(it) },
                onNext = { homeViewModel.setCurrentScreen(Screen.OnboardingReminder(current.fromSettings)) },
                onBack = {
                    if ((current ).fromSettings) {
                        homeViewModel.setCurrentScreen(Screen.Settings)
                    }
                }
            )
        }

        is Screen.OnboardingReminder -> {
            val state = onboardingViewModel.uiState.collectAsStateWithLifecycle().value
            OnboardingReminderTimeScreen(
                hour = state.reminderHour,
                minute = state.reminderMinute,
                onTimeChange = { h, m -> onboardingViewModel.onReminderTimeChange(h, m) },
                onFinish = {
                    onboardingViewModel.completeOnboarding()
                    homeViewModel.setCurrentScreen(Screen.Home)
                },
                onBack = {
                    if ((current ).fromSettings) {
                        homeViewModel.setCurrentScreen(Screen.Settings)
                    }
                }
            )
        }

        Screen.Home -> {
            val state = homeViewModel.uiState.collectAsStateWithLifecycle().value
            HomeScreen(
                uiState = state,
                onLogReflection = { homeViewModel.setCurrentScreen(Screen.Reflection) },
                onLogInteraction = { homeViewModel.setCurrentScreen(Screen.Interaction) },
                onOpenInsights = { homeViewModel.setCurrentScreen(Screen.Insights) },
                onOpenTimeline = { homeViewModel.setCurrentScreen(Screen.Timeline) },
                onOpenSettings = {homeViewModel.setCurrentScreen(Screen.Settings)},
                onDismissInsight = {homeViewModel.clearInstantInsight()}
            )
        }

        Screen.Reflection -> {
            val state = reflectionViewModel.uiState.collectAsStateWithLifecycle().value
            DailyReflectionScreen(
                mood = state.mood.takeIf { it != 3 || state.note.isNotEmpty() },
                note = state.note,
                onMoodSelected = { reflectionViewModel.onMoodSelected(it) },
                onNoteChange = { reflectionViewModel.onNoteChange(it) },
                onSave = {
                    reflectionViewModel.saveReflection()
                    homeViewModel.setCurrentScreen(Screen.Home)
                },
                onBack = { homeViewModel.setCurrentScreen(Screen.Home) }
            )
        }

        Screen.Interaction -> {
            val state = interactionViewModel.uiState.collectAsStateWithLifecycle().value
            InteractionLogScreen(
                selectedType = state.type,
                selectedMoodEffect = when (state.moodEffect) {
                    1 -> MoodEffect.Better
                    0 -> MoodEffect.Same
                    -1 -> MoodEffect.Worse
                    else -> null
                },
                onSelectType = { interactionViewModel.onTypeSelected(it) },
                onSelectMoodEffect = { effect ->
                    val moodEffectInt = when (effect) {
                        MoodEffect.Better -> 1
                        MoodEffect.Same -> 0
                        MoodEffect.Worse -> -1
                    }
                    interactionViewModel.onMoodEffectSelected(moodEffectInt)
                },
                onSave = {interactionType->
                    interactionViewModel.saveInteraction()
                    homeViewModel.setCurrentScreen(Screen.Home)
                    homeViewModel.showInstantInsight(interactionType)
                },
                onBack = { homeViewModel.setCurrentScreen(Screen.Home) }
            )
        }

        Screen.Insights -> {
            val state = insightsViewModel.uiState.collectAsStateWithLifecycle().value
            InsightsScreen(
                insights = state.insights,
                positiveCount = state.positiveCount,
                negativeCount = state.negativeCount,
                onBack = { homeViewModel.setCurrentScreen(Screen.Home) }
            )
        }

        Screen.Settings -> {
            val state = settingsViewModel.uiState.collectAsStateWithLifecycle().value
            SettingsScreen(
                personName = state.personName,
                relationshipType = state.relationshipType,
                reminderTime = state.reminderTime,
                onEditName = { homeViewModel.setCurrentScreen(Screen.OnboardingName(true)) },
                onEditRelationshipType = { homeViewModel.setCurrentScreen(Screen.OnboardingType(true)) },
                onEditReminderTime = { homeViewModel.setCurrentScreen(Screen.OnboardingReminder(true)) },
                onToggleNotifications = { settingsViewModel.onToggleNotifications(it) },
                notificationsEnabled = state.notificationsEnabled,
                onBack = { homeViewModel.setCurrentScreen(Screen.Home) }
            )
        }

        Screen.Timeline -> {
            TimelineScreen(
                items = insightsViewModel.timeline.collectAsStateWithLifecycle().value,
                onBack = { homeViewModel.setCurrentScreen(Screen.Home) }
            )
        }
    }
}
