package com.desktop.lumi

import androidx.compose.ui.window.ComposeUIViewController
import com.desktop.lumi.common.App
import com.desktop.lumi.db.DatabaseDriverFactory
import com.desktop.lumi.db.com.desktop.lumi.NotificationScheduler
import com.desktop.lumi.db.com.desktop.lumi.di.AppModule
import com.desktop.lumi.settings.SettingsViewModel

fun MainViewController() = ComposeUIViewController {
    val scheduler = NotificationScheduler()
    val module = AppModule(DatabaseDriverFactory(), scheduler)

    val settingsViewModel = SettingsViewModel(
        personRepository = module.personRepository,
        scheduler = scheduler
    )
    App(
        homeViewModel = module.provideHomeViewModel(),
        onboardingViewModel = module.provideOnboardingViewModel(),
        reflectionViewModel = module.provideReflectionViewModel(),
        interactionViewModel = module.provideInteractionViewModel(),
        insightsViewModel = module.provideInsightsViewModel(),
        settingsViewModel = settingsViewModel,
        sosViewModel = module.provideSoSViewModel(),
        {}
    )
}