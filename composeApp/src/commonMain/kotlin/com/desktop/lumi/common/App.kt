package com.desktop.lumi.common

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import com.desktop.lumi.home.HomeViewModel
import com.desktop.lumi.home.presentation.InteractionViewModel
import com.desktop.lumi.home.presentation.ReflectionViewModel
import com.desktop.lumi.insights.InsightsViewModel
import com.desktop.lumi.onboarding.presentation.viewmodel.OnboardingViewModel
import com.desktop.lumi.settings.SettingsViewModel
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun App(
    homeViewModel: HomeViewModel,
    onboardingViewModel: OnboardingViewModel,
    reflectionViewModel: ReflectionViewModel,
    interactionViewModel: InteractionViewModel,
    insightsViewModel: InsightsViewModel,
    settingsViewModel: SettingsViewModel
) {
    MaterialTheme {
        AppNavHost(
            homeViewModel = homeViewModel,
            onboardingViewModel = onboardingViewModel,
            reflectionViewModel = reflectionViewModel,
            interactionViewModel = interactionViewModel,
            insightsViewModel = insightsViewModel,
            settingsViewModel = settingsViewModel
        )
    }
}
