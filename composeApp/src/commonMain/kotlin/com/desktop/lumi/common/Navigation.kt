package com.desktop.lumi.common

sealed class Screen(val route: String) {
    data object OnboardingName : Screen("onboarding_name")
    data object OnboardingType : Screen("onboarding_type")
    data object OnboardingReminder : Screen("onboarding_reminder")
    data object Home : Screen("home")
    data object Reflection : Screen("reflection")
    data object Interaction : Screen("interaction")
    data object Timeline : Screen("timeline")
    data object Insights : Screen("insights")
    data object Settings : Screen("settings")
}

