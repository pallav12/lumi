package com.desktop.lumi.di

import android.content.Context
import com.desktop.lumi.db.AppDatabase
import com.desktop.lumi.db.DatabaseDriverFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    @Singleton
    fun provideDriverFactory(
        @ApplicationContext context: Context
    ): DatabaseDriverFactory = DatabaseDriverFactory(context)

    @Provides
    @Singleton
    fun provideDatabase(
        factory: DatabaseDriverFactory
    ): AppDatabase = AppDatabase(factory.createDriver())
}

