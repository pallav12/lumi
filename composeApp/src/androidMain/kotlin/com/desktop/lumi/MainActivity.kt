package com.desktop.lumi

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.desktop.lumi.common.App
import com.desktop.lumi.db.DatabaseDriverFactory
import com.desktop.lumi.db.com.desktop.lumi.NotificationScheduler
import com.desktop.lumi.db.com.desktop.lumi.di.AppModule
import com.desktop.lumi.settings.SettingsViewModel

class MainActivity : ComponentActivity() {
    private val scheduler by lazy {
        NotificationScheduler(this)
    }
    private val appModule by lazy {
        AppModule(DatabaseDriverFactory(this), scheduler)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        val settingsViewModel = SettingsViewModel(
            personRepository = appModule.personRepository,
            scheduler = scheduler
        )
        setContent {

            App(
                homeViewModel = appModule.provideHomeViewModel(),
                onboardingViewModel = appModule.provideOnboardingViewModel(),
                reflectionViewModel = appModule.provideReflectionViewModel(),
                interactionViewModel = appModule.provideInteractionViewModel(),
                insightsViewModel = appModule.provideInsightsViewModel(),
                settingsViewModel = settingsViewModel
            )
        }
    }
}