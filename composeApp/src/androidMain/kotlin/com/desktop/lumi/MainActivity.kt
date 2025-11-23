package com.desktop.lumi

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.desktop.lumi.common.App
import com.desktop.lumi.db.DatabaseDriverFactory
import com.desktop.lumi.db.com.desktop.lumi.di.AppModule

class MainActivity : ComponentActivity() {
    private val appModule by lazy {
        AppModule(DatabaseDriverFactory(this))
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        setContent {

            App(
                homeViewModel = appModule.provideHomeViewModel(),
                onboardingViewModel = appModule.provideOnboardingViewModel(),
                reflectionViewModel = appModule.provideReflectionViewModel(),
                interactionViewModel = appModule.provideInteractionViewModel(),
                insightsViewModel = appModule.provideInsightsViewModel(),
                settingsViewModel = appModule.provideSettingsViewModel()
            )
        }
    }
}