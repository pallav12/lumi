package com.desktop.lumi.db.com.desktop.lumi.di

import com.desktop.lumi.data.repository.InsightsRepositoryImpl
import com.desktop.lumi.data.repository.InteractionRepositoryImpl
import com.desktop.lumi.data.repository.PersonRepositoryImpl
import com.desktop.lumi.data.repository.ReflectionRepositoryImpl
import com.desktop.lumi.db.AppDatabase
import com.desktop.lumi.db.DatabaseDriverFactory
import com.desktop.lumi.db.com.desktop.lumi.NotificationScheduler
import com.desktop.lumi.db.com.desktop.lumi.sos.SosViewModel
import com.desktop.lumi.domain.repository.InsightsRepository
import com.desktop.lumi.domain.repository.InteractionRepository
import com.desktop.lumi.domain.repository.PersonRepository
import com.desktop.lumi.domain.repository.ReflectionRepository
import com.desktop.lumi.home.HomeViewModel
import com.desktop.lumi.home.presentation.InteractionViewModel
import com.desktop.lumi.home.presentation.ReflectionViewModel
import com.desktop.lumi.insights.InsightsViewModel
import com.desktop.lumi.instantmirror.InsightEngine
import com.desktop.lumi.onboarding.presentation.viewmodel.OnboardingViewModel

class AppModule(
    driverFactory: DatabaseDriverFactory,
    private val notificationScheduler: NotificationScheduler? = null
) {
    val database: AppDatabase = AppDatabase(driverFactory.createDriver())

    // Repositories
    val personRepository: PersonRepository = PersonRepositoryImpl(database)
    val reflectionRepository: ReflectionRepository = ReflectionRepositoryImpl(database)
    val interactionRepository: InteractionRepository = InteractionRepositoryImpl(database)
    val insightsRepository: InsightsRepository = InsightsRepositoryImpl(reflectionRepository)
    val insightsEngine = InsightEngine()

    // ViewModels
    fun provideHomeViewModel() = HomeViewModel(
        personRepository,
        insightsRepository,
        reflectionRepository,
        interactionRepository,
        insightsEngine
    )

    fun provideOnboardingViewModel() = OnboardingViewModel(
        personRepository,
        notificationScheduler
    )

    fun provideReflectionViewModel() = ReflectionViewModel(reflectionRepository)

    fun provideInteractionViewModel() = InteractionViewModel(interactionRepository)

    fun provideSoSViewModel() = SosViewModel()
    fun provideInsightsViewModel() =
        InsightsViewModel(insightsRepository, reflectionRepository, interactionRepository)
}
