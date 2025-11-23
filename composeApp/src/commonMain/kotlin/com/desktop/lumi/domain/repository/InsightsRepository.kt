package com.desktop.lumi.domain.repository

import com.desktop.lumi.domain.model.WeeklyTrend
import kotlinx.coroutines.flow.Flow

interface InsightsRepository {
    fun getWeeklyTrend(): Flow<WeeklyTrend>
}

