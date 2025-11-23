package com.desktop.lumi.domain.model

data class WeeklyTrend(
    val moodPoints: List<Int>,
    val positiveCount: Int,
    val negativeCount: Int
)

