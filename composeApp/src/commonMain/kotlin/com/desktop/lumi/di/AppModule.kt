package com.desktop.lumi.db.com.desktop.lumi.di

import com.desktop.lumi.db.AppDatabase
import com.desktop.lumi.domain.repository.*
import com.desktop.lumi.data.repository.*
import com.desktop.lumi.home.HomeViewModel
import com.desktop.lumi.insights.InsightsViewModel
import com.desktop.lumi.settings.SettingsViewModel
import com.desktop.lumi.db.DatabaseDriverFactory
import com.desktop.lumi.home.presentation.InteractionViewModel
import com.desktop.lumi.home.presentation.ReflectionViewModel
import com.desktop.lumi.onboarding.presentation.viewmodel.OnboardingViewModel

class AppModule(
    driverFactory: DatabaseDriverFactory
) {
    val database: AppDatabase = AppDatabase(driverFactory.createDriver())

    // Repositories
    val personRepository: PersonRepository = PersonRepositoryImpl(database)
    val reflectionRepository: ReflectionRepository = ReflectionRepositoryImpl(database)
    val interactionRepository: InteractionRepository = InteractionRepositoryImpl(database)
    val insightsRepository: InsightsRepository = InsightsRepositoryImpl(reflectionRepository)

    // ViewModels
    fun provideHomeViewModel() = HomeViewModel(
        personRepository,
        insightsRepository,
        reflectionRepository
    )

    fun provideOnboardingViewModel() = OnboardingViewModel(personRepository)

    fun provideReflectionViewModel() = ReflectionViewModel(reflectionRepository)

    fun provideInteractionViewModel() = InteractionViewModel(interactionRepository)

    fun provideInsightsViewModel() = InsightsViewModel(insightsRepository)

    fun provideSettingsViewModel() = SettingsViewModel(personRepository)
}
