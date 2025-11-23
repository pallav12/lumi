package com.desktop.lumi.di

import com.desktop.lumi.db.AppDatabase
import com.desktop.lumi.data.repository.InsightsRepositoryImpl
import com.desktop.lumi.data.repository.InteractionRepositoryImpl
import com.desktop.lumi.data.repository.PersonRepositoryImpl
import com.desktop.lumi.data.repository.ReflectionRepositoryImpl
import com.desktop.lumi.domain.repository.InsightsRepository
import com.desktop.lumi.domain.repository.InteractionRepository
import com.desktop.lumi.domain.repository.PersonRepository
import com.desktop.lumi.domain.repository.ReflectionRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {
    @Provides
    @Singleton
    fun providePersonRepository(db: AppDatabase): PersonRepository =
        PersonRepositoryImpl(db)

    @Provides
    @Singleton
    fun provideReflectionRepository(db: AppDatabase): ReflectionRepository =
        ReflectionRepositoryImpl(db)

    @Provides
    @Singleton
    fun provideInteractionRepository(db: AppDatabase): InteractionRepository =
        InteractionRepositoryImpl(db)

    @Provides
    @Singleton
    fun provideInsightsRepository(
        reflectionRepository: ReflectionRepository
    ): InsightsRepository =
        InsightsRepositoryImpl(reflectionRepository)
}

