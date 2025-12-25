package com.desktop.lumi.common

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.backhandler.BackHandler
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.desktop.lumi.db.com.desktop.lumi.message.VoidScreen
import com.desktop.lumi.db.com.desktop.lumi.message.VoidViewModel
import com.desktop.lumi.db.com.desktop.lumi.sos.SosViewModel
import com.desktop.lumi.home.HomeViewModel
import com.desktop.lumi.home.presentation.DailyReflectionScreen
import com.desktop.lumi.home.presentation.HomeScreen
import com.desktop.lumi.home.presentation.InteractionLogScreen
import com.desktop.lumi.home.presentation.InteractionViewModel
import com.desktop.lumi.home.presentation.MoodEffect
import com.desktop.lumi.home.presentation.ReflectionViewModel
import com.desktop.lumi.insights.InsightsScreen
import com.desktop.lumi.insights.InsightsViewModel
import com.desktop.lumi.insights.TimelineScreen
import com.desktop.lumi.onboarding.presentation.composable.OnboardingNameScreen
import com.desktop.lumi.onboarding.presentation.composable.OnboardingRelationshipTypeScreen
import com.desktop.lumi.onboarding.presentation.composable.OnboardingReminderTimeScreen
import com.desktop.lumi.onboarding.presentation.viewmodel.OnboardingViewModel
import com.desktop.lumi.orbit.OrbitScreen
import com.desktop.lumi.orbit.OrbitViewModel
import com.desktop.lumi.script.ui.ScriptLibraryScreen
import com.desktop.lumi.script.viewmodel.ScriptViewModel
import com.desktop.lumi.settings.SettingsScreen
import com.desktop.lumi.settings.SettingsViewModel
import com.desktop.lumi.sos.presentation.SosScreen

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun AppNavHost(
    homeViewModel: HomeViewModel,
    onboardingViewModel: OnboardingViewModel,
    reflectionViewModel: ReflectionViewModel,
    interactionViewModel: InteractionViewModel,
    insightsViewModel: InsightsViewModel,
    settingsViewModel: SettingsViewModel,
    sosViewModel: SosViewModel,
    voidViewModel: VoidViewModel,
    scriptViewModel: ScriptViewModel,
    orbitViewModel: OrbitViewModel,
    onRequestPermission: () -> Unit,
) {
    val current = homeViewModel.currentScreen.collectAsStateWithLifecycle().value

    // Global BackHandler strategy isn't possible because destinations vary.
    // We apply it per screen.

    when (current) {

        is Screen.OnboardingName -> {
            // If from settings, back goes to settings.
            // If initial launch, back exits the app (default behavior, so enabled = false).
            BackHandler(enabled = current.fromSettings) {
                homeViewModel.setCurrentScreen(Screen.Settings)
            }

            OnboardingNameScreen(
                name = onboardingViewModel.uiState.collectAsStateWithLifecycle().value.name,
                onNameChange = { onboardingViewModel.onNameChange(it) },
                onNext = { homeViewModel.setCurrentScreen(Screen.OnboardingType(current.fromSettings)) },
                onBack = {
                    if (current.fromSettings) {
                        homeViewModel.setCurrentScreen(Screen.Settings)
                    }
                }
            )
        }

        is Screen.OnboardingType -> {
            // IMPROVED: Back goes to previous step (Name) if initial flow
            BackHandler {
                if (current.fromSettings) {
                    homeViewModel.setCurrentScreen(Screen.Settings)
                } else {
                    homeViewModel.setCurrentScreen(Screen.OnboardingName(false))
                }
            }

            val state = onboardingViewModel.uiState.collectAsStateWithLifecycle().value
            OnboardingRelationshipTypeScreen(
                selectedType = state.relationshipType,
                onSelectType = { onboardingViewModel.onRelationshipTypeSelect(it) },
                onNext = { homeViewModel.setCurrentScreen(Screen.OnboardingReminder(current.fromSettings)) },
                onBack = {
                    if (current.fromSettings) {
                        homeViewModel.setCurrentScreen(Screen.Settings)
                    } else {
                        homeViewModel.setCurrentScreen(Screen.OnboardingName(false))
                    }
                }
            )
        }

        is Screen.OnboardingReminder -> {
            // IMPROVED: Back goes to previous step (Relationship) if initial flow
            BackHandler {
                if (current.fromSettings) {
                    homeViewModel.setCurrentScreen(Screen.Settings)
                } else {
                    homeViewModel.setCurrentScreen(Screen.OnboardingType(false))
                }
            }

            val state = onboardingViewModel.uiState.collectAsStateWithLifecycle().value
            OnboardingReminderTimeScreen(
                hour = state.reminderHour,
                minute = state.reminderMinute,
                onTimeChange = { h, m -> onboardingViewModel.onTimeChange(h, m) },
                onFinish = {
                    onboardingViewModel.completeOnboarding()
                    homeViewModel.setCurrentScreen(Screen.Home)
                    onRequestPermission()
                },
                onBack = {
                    if (current.fromSettings) {
                        homeViewModel.setCurrentScreen(Screen.Settings)
                    } else {
                        homeViewModel.setCurrentScreen(Screen.OnboardingType(false))
                    }
                }
            )
        }

        Screen.Home -> {

            val state = homeViewModel.uiState.collectAsStateWithLifecycle().value
            HomeScreen(
                uiState = state,
                orbitState = orbitViewModel.uiState.collectAsStateWithLifecycle().value,
                onLogReflection = { homeViewModel.setCurrentScreen(Screen.Reflection) },
                onLogInteraction = { homeViewModel.setCurrentScreen(Screen.Interaction) },
                onOpenInsights = { homeViewModel.setCurrentScreen(Screen.Insights) },
                onOpenTimeline = { homeViewModel.setCurrentScreen(Screen.Timeline) },
                onOpenSettings = { homeViewModel.setCurrentScreen(Screen.Settings) },
                onDismissInsight = { homeViewModel.clearInstantInsight() },
                onOpenSOS = { homeViewModel.setCurrentScreen(Screen.SOS) },
                onOpenVoid = { homeViewModel.setCurrentScreen(Screen.Void) },
                onOpenScripts = { homeViewModel.setCurrentScreen(Screen.Scripts) },
                onOpenOrbit = { homeViewModel.setCurrentScreen(Screen.Orbit) }
            )
        }

        Screen.SOS -> {
            // Back from SOS resets the state and goes Home
            BackHandler {
                sosViewModel.reset()
                homeViewModel.setCurrentScreen(Screen.Home)
            }

            // Track SOS started when screen is first shown (only once)
            LaunchedEffect(Unit) {
                sosViewModel.onSosStarted()
            }

            val state by sosViewModel.step.collectAsState()
            SosScreen(
                state = state,
                onBreathingDone = { sosViewModel.onBreathingDone() },
                onRealityCheckComplete = { sosViewModel.onRealityCheckComplete() },
                onExit = {
                    // Track SOS finished when user completes the flow
                    if (state == com.desktop.lumi.db.com.desktop.lumi.sos.SosViewModel.SosStep.RESOLUTION) {
                        sosViewModel.onSosFinished()
                    }
                    sosViewModel.reset()
                    homeViewModel.setCurrentScreen(Screen.Home)
                }
            )
        }

        Screen.Reflection -> {
            BackHandler { homeViewModel.setCurrentScreen(Screen.Home) }

            val state = reflectionViewModel.uiState.collectAsStateWithLifecycle().value
            DailyReflectionScreen(
                mood = state.mood,
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
            BackHandler { homeViewModel.setCurrentScreen(Screen.Home) }

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
                onSave = { interactionType ->
                    interactionViewModel.saveInteraction()
                    homeViewModel.setCurrentScreen(Screen.Home)
                    homeViewModel.showInstantInsight(interactionType)
                },
                onBack = { homeViewModel.setCurrentScreen(Screen.Home) }
            )
        }

        Screen.Insights -> {
            BackHandler { homeViewModel.setCurrentScreen(Screen.Home) }

            val state = insightsViewModel.uiState.collectAsStateWithLifecycle().value
            InsightsScreen(
                insights = state.insights,
                positiveCount = state.positiveCount,
                negativeCount = state.negativeCount,
                onBack = { homeViewModel.setCurrentScreen(Screen.Home) }
            )
        }

        Screen.Settings -> {
            BackHandler { homeViewModel.setCurrentScreen(Screen.Home) }

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
            BackHandler { homeViewModel.setCurrentScreen(Screen.Home) }

            TimelineScreen(
                items = insightsViewModel.timeline.collectAsStateWithLifecycle().value,
                onBack = { homeViewModel.setCurrentScreen(Screen.Home) }
            )
        }

        Screen.Void -> {
            BackHandler { homeViewModel.setCurrentScreen(Screen.Home) }
            VoidScreen(
                state = voidViewModel.uiState.collectAsStateWithLifecycle().value,
                onMessageChange = voidViewModel::onMessageChange,
                onRelease = voidViewModel::onRelease, onBack = {
                    homeViewModel.setCurrentScreen(Screen.Home)
                })
        }

        Screen.Scripts -> {
            BackHandler { homeViewModel.setCurrentScreen(Screen.Home) }

            val state = scriptViewModel.uiState.collectAsStateWithLifecycle().value
            ScriptLibraryScreen(
                state = state,
                onCategorySelect = { scriptViewModel.selectCategory(it) },
                onBack = { homeViewModel.setCurrentScreen(Screen.Home) }
            )
        }

        Screen.Orbit -> {
            BackHandler { homeViewModel.setCurrentScreen(Screen.Home) }

            val state = orbitViewModel.uiState.collectAsStateWithLifecycle().value
            OrbitScreen(
                state = state,
                onStart = orbitViewModel::startOrbit,
                onBreak = orbitViewModel::breakOrbit,
                onFinish = orbitViewModel::finishOrbit,
                onBack = { homeViewModel.setCurrentScreen(Screen.Home) },
                onGoToSOS = { homeViewModel.setCurrentScreen(Screen.SOS) }
            )
        }
    }
}