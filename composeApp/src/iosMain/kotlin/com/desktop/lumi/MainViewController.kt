package com.desktop.lumi

import androidx.compose.ui.window.ComposeUIViewController
import com.desktop.lumi.common.App
import com.desktop.lumi.db.DatabaseDriverFactory
import com.desktop.lumi.db.com.desktop.lumi.di.AppModule

fun MainViewController() = ComposeUIViewController {
    val module = AppModule(DatabaseDriverFactory())
    App(
        homeViewModel = module.provideHomeViewModel(),
        onboardingViewModel = module.provideOnboardingViewModel(),
        reflectionViewModel = module.provideReflectionViewModel(),
        interactionViewModel = module.provideInteractionViewModel(),
        insightsViewModel = module.provideInsightsViewModel(),
        settingsViewModel = module.provideSettingsViewModel()
    )
}