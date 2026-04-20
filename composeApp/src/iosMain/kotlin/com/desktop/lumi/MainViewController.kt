package com.desktop.lumi

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.window.ComposeUIViewController
import com.desktop.lumi.analytics.Analytics
import com.desktop.lumi.common.App
import com.desktop.lumi.db.DatabaseDriverFactory
import com.desktop.lumi.db.com.desktop.lumi.NotificationScheduler
import com.desktop.lumi.db.com.desktop.lumi.di.AppModule
import com.desktop.lumi.settings.SettingsViewModel
import com.revenuecat.purchases.kmp.Purchases
import platform.Foundation.NSBundle

fun MainViewController() = ComposeUIViewController {
    // Read key from Info.plist → Secrets.xcconfig (not in source control)
    val rcKey = NSBundle.mainBundle.objectForInfoDictionaryKey("REVENUECAT_IOS_KEY") as? String ?: ""
    Purchases.configure(apiKey = rcKey)

    val scheduler = NotificationScheduler()
    val analytics = Analytics()
    val module = AppModule(DatabaseDriverFactory(), scheduler, analytics)

    val settingsViewModel = SettingsViewModel(
        personRepository = module.personRepository,
        scheduler = scheduler
    )

    scheduler.scheduleWeeklyReport()

    // Fetch entitlement status + offerings on launch
    LaunchedEffect(Unit) {
        module.billingManager.refreshEntitlementStatus()
        module.billingManager.fetchOfferings()
    }

    App(
        homeViewModel = module.provideHomeViewModel(),
        onboardingViewModel = module.provideOnboardingViewModel(),
        reflectionViewModel = module.provideReflectionViewModel(),
        interactionViewModel = module.provideInteractionViewModel(),
        insightsViewModel = module.provideInsightsViewModel(),
        settingsViewModel = settingsViewModel,
        sosViewModel = module.provideSoSViewModel(),
        voidViewModel = module.provideVoidViewModel(),
        scriptViewModel = module.provideScriptViewModel(),
        orbitViewModel = module.provideOrbitViewModel(),
        anchorViewModel = module.provideAnchorViewModel(),
        billingManager = module.billingManager,
        onRequestNotificationPermission = { /* Handled in AppDelegate on iOS */ },
        onRequestReview = { /* TODO: Wire up SKStoreReviewController from Swift */ }
    )
}
