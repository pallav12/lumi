package com.desktop.lumi.data.repository

import com.desktop.lumi.domain.model.WeeklyTrend
import com.desktop.lumi.domain.repository.InsightsRepository
import com.desktop.lumi.domain.repository.ReflectionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class InsightsRepositoryImpl(
    private val reflectionRepo: ReflectionRepository
) : InsightsRepository {

    override fun getWeeklyTrend(): Flow<WeeklyTrend> =
        reflectionRepo.getLast7Reflections().map { reflections ->
            val moodPoints = reflections.map { it.mood }
            WeeklyTrend(
                moodPoints = moodPoints,
                positiveCount = moodPoints.count { it >= 4 },
                negativeCount = moodPoints.count { it <= 2 }
            )
        }
}

