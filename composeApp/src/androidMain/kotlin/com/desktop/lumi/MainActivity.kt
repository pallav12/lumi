package com.desktop.lumi

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.desktop.lumi.analytics.Analytics
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
        AppModule(DatabaseDriverFactory(this), scheduler, Analytics())
    }

    private val reviewManager by lazy { AndroidReviewManager(this, Analytics()) }

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        scheduler.scheduleWeeklyReport()
        val settingsViewModel = SettingsViewModel(
            personRepository = appModule.personRepository,
            scheduler = scheduler
        )

        val deepLinkDestination = intent?.getStringExtra("navigation_route")

        setContent {

            val notificationPermissionLauncher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.RequestPermission(),
                onResult = { /* Handled */ }
            )

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
                voidViewModel = appModule.provideVoidViewModel(),
                scriptViewModel = appModule.provideScriptViewModel(),
                onRequestNotificationPermission = requestNotificationPermission,
                orbitViewModel = appModule.provideOrbitViewModel(),
                onRequestReview = { reviewManager.tryRequestReview(this) },
                deepLinkDestination = deepLinkDestination // ⬅ Pass the route
            )
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
    }
}