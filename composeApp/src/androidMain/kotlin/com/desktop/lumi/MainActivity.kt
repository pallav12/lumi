package com.desktop.lumi

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.core.content.ContextCompat
import com.desktop.lumi.common.App
import com.desktop.lumi.db.DatabaseDriverFactory
import com.desktop.lumi.db.com.desktop.lumi.NotificationScheduler
import com.desktop.lumi.db.com.desktop.lumi.di.AppModule
import com.desktop.lumi.settings.SettingsViewModel

class MainActivity : ComponentActivity() {

    private val scheduler by lazy {
        NotificationScheduler(applicationContext)
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
            // State to track what to do after permission result (e.g., finish onboarding)
            // You might not need this if 'onFinish' handles navigation regardless of result

            val notificationPermissionLauncher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.RequestPermission(),
                onResult = { isGranted ->
                    // Logic to handle result if needed, or just rely on the flow continuing.
                    // For simple onboarding, we might just proceed regardless.
                }
            )

            // This is the callback we pass to the Common Main App
            val requestNotificationPermission: () -> Unit = {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    val hasPermission = ContextCompat.checkSelfPermission(
                        this,
                        Manifest.permission.POST_NOTIFICATIONS
                    ) == PackageManager.PERMISSION_GRANTED

                    if (!hasPermission) {
                        notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                    }
                }
            }

            App(
                homeViewModel = appModule.provideHomeViewModel(),
                onboardingViewModel = appModule.provideOnboardingViewModel(),
                reflectionViewModel = appModule.provideReflectionViewModel(),
                interactionViewModel = appModule.provideInteractionViewModel(),
                insightsViewModel = appModule.provideInsightsViewModel(),
                settingsViewModel = settingsViewModel,
                sosViewModel = appModule.provideSoSViewModel(),
                onRequestPermission = requestNotificationPermission,
            )
        }
    }
}