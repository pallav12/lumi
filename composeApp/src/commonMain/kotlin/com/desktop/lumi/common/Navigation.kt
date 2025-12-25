package com.desktop.lumi.common

sealed class Screen {
    data class OnboardingName(val fromSettings: Boolean = false) : Screen()
    data class OnboardingType(val fromSettings: Boolean = false) : Screen()
    data class OnboardingReminder(val fromSettings: Boolean = false) : Screen()

    data object Home : Screen()
    data object Reflection : Screen()
    data object Interaction : Screen()
    data object Timeline : Screen()
    data object Insights : Screen()
    data object Settings : Screen()
    data object SOS : Screen()
    data object Void : Screen()
    data object Scripts : Screen()
    data object Orbit : Screen()
}


