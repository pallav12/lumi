package com.desktop.lumi.db.com.desktop.lumi.di

import com.desktop.lumi.analytics.Analytics
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
import com.desktop.lumi.domain.repository.OrbitRepository
import com.desktop.lumi.domain.repository.OrbitRepositoryImpl
import com.desktop.lumi.domain.repository.PersonRepository
import com.desktop.lumi.domain.repository.ReflectionRepository
import com.desktop.lumi.home.HomeViewModel
import com.desktop.lumi.home.presentation.InteractionViewModel
import com.desktop.lumi.home.presentation.ReflectionViewModel
import com.desktop.lumi.insights.InsightsViewModel
import com.desktop.lumi.instantmirror.InsightEngine
import com.desktop.lumi.onboarding.presentation.viewmodel.OnboardingViewModel
import com.desktop.lumi.orbit.OrbitViewModel
import com.desktop.lumi.script.viewmodel.ScriptViewModel
import com.desktop.lumi.void.VoidViewModel

class AppModule(
    driverFactory: DatabaseDriverFactory,
    private val notificationScheduler: NotificationScheduler? = null,
    private val analytics: Analytics? = null
) {
    val database: AppDatabase = AppDatabase(driverFactory.createDriver())

    // Repositories
    val personRepository: PersonRepository = PersonRepositoryImpl(database)
    val reflectionRepository: ReflectionRepository = ReflectionRepositoryImpl(database)
    val interactionRepository: InteractionRepository = InteractionRepositoryImpl(database)
    val insightsRepository: InsightsRepository = InsightsRepositoryImpl(reflectionRepository)
    val insightsEngine = InsightEngine()
    val orbitReflectionRepository: OrbitRepository = OrbitRepositoryImpl(database)

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
        notificationScheduler,
        analytics
    )

    fun provideReflectionViewModel() = ReflectionViewModel(
        reflectionRepository,
        analytics,
        notificationScheduler
    )

    fun provideInteractionViewModel() = InteractionViewModel(
        interactionRepository,
        analytics,
        notificationScheduler
    )

    fun provideSoSViewModel() = SosViewModel(analytics)
    fun provideVoidViewModel() = VoidViewModel(analytics, notificationScheduler)

    fun provideInsightsViewModel() =
        InsightsViewModel(insightsRepository, reflectionRepository, interactionRepository)

    fun provideScriptViewModel(): ScriptViewModel {
        return ScriptViewModel()
    }

    fun provideOrbitViewModel(): OrbitViewModel {
        return OrbitViewModel(orbitReflectionRepository, analytics, notificationScheduler)
    }
}
