package com.desktop.lumi.common

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.desktop.lumi.db.com.desktop.lumi.sos.SosViewModel
import com.desktop.lumi.home.HomeViewModel
import com.desktop.lumi.home.presentation.InteractionViewModel
import com.desktop.lumi.home.presentation.ReflectionViewModel
import com.desktop.lumi.insights.InsightsViewModel
import com.desktop.lumi.onboarding.presentation.viewmodel.OnboardingViewModel
import com.desktop.lumi.orbit.OrbitViewModel
import com.desktop.lumi.script.viewmodel.ScriptViewModel
import com.desktop.lumi.settings.SettingsViewModel
import com.desktop.lumi.void.VoidViewModel

@Composable
fun App(
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
    onRequestNotificationPermission: () -> Unit,
    onRequestReview: () -> Unit,
    deepLinkDestination: String? = null,
    onDeepLinkHandled: () -> Unit = {}
) {
    LaunchedEffect(deepLinkDestination) {
        if (deepLinkDestination != null) {
            // Small delay to ensure initial screen determination doesn't override
            kotlinx.coroutines.delay(100)
            when (deepLinkDestination) {
                "sos" -> homeViewModel.setCurrentScreen(Screen.SOS)
                "orbit" -> homeViewModel.setCurrentScreen(Screen.Orbit)
                "void" -> homeViewModel.setCurrentScreen(Screen.Void)
                "insights" -> homeViewModel.setCurrentScreen(Screen.Insights)
                "streak" -> homeViewModel.setCurrentScreen(Screen.Home)
            }
            // Clear the deep link after handling to prevent re-triggering
            onDeepLinkHandled()
        }
    }

    MaterialTheme {
        AppNavHost(
            homeViewModel = homeViewModel,
            onboardingViewModel = onboardingViewModel,
            reflectionViewModel = reflectionViewModel,
            interactionViewModel = interactionViewModel,
            insightsViewModel = insightsViewModel,
            settingsViewModel = settingsViewModel,
            sosViewModel = sosViewModel,
            voidViewModel = voidViewModel,
            scriptViewModel = scriptViewModel,
            orbitViewModel = orbitViewModel,
            onRequestPermission = onRequestNotificationPermission,
            onRequestReview = onRequestReview
        )
    }
}