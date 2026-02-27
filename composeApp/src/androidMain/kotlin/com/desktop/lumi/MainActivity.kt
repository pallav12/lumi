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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
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
    
    // Shared state for deep links that can be updated from onNewIntent
    private val deepLinkState = mutableStateOf<String?>(null)
    
    // Cache ViewModels to prevent recreation on recomposition
    private val homeViewModel by lazy { appModule.provideHomeViewModel() }
    private val onboardingViewModel by lazy { appModule.provideOnboardingViewModel() }
    private val reflectionViewModel by lazy { appModule.provideReflectionViewModel() }
    private val interactionViewModel by lazy { appModule.provideInteractionViewModel() }
    private val insightsViewModel by lazy { appModule.provideInsightsViewModel() }
    private val anchorViewModel by lazy { appModule.provideAnchorViewModel() }
    private val sosViewModel by lazy { appModule.provideSoSViewModel() }
    private val voidViewModel by lazy { appModule.provideVoidViewModel() }
    private val scriptViewModel by lazy { appModule.provideScriptViewModel() }
    private val orbitViewModel by lazy { appModule.provideOrbitViewModel() }

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        scheduler.scheduleWeeklyReport()
        val settingsViewModel = SettingsViewModel(
            personRepository = appModule.personRepository,
            scheduler = scheduler
        )

        val initialDeepLink = intent?.getStringExtra("navigation_route")
        deepLinkState.value = initialDeepLink

        setContent {
            // Use the shared state that can be updated from onNewIntent
            val deepLinkDestination by deepLinkState

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
                homeViewModel = homeViewModel,
                onboardingViewModel = onboardingViewModel,
                reflectionViewModel = reflectionViewModel,
                interactionViewModel = interactionViewModel,
                insightsViewModel = insightsViewModel,
                settingsViewModel = settingsViewModel,
                sosViewModel = sosViewModel,
                voidViewModel = voidViewModel,
                scriptViewModel = scriptViewModel,
                onRequestNotificationPermission = requestNotificationPermission,
                orbitViewModel = orbitViewModel,
                anchorViewModel = anchorViewModel,
                onRequestReview = { reviewManager.tryRequestReview(this) },
                deepLinkDestination = deepLinkDestination, // ⬅ Pass the route
                onDeepLinkHandled = { deepLinkState.value = null } // Clear after handling
            )
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        // Update the shared state to trigger recomposition
        deepLinkState.value = intent.getStringExtra("navigation_route")
    }
}