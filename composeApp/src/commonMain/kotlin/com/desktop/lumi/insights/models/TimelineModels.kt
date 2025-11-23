package com.desktop.lumi.insights.models

import com.desktop.lumi.home.presentation.InteractionType
import com.desktop.lumi.home.presentation.MoodEffect
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime

sealed class TimelineItemUi {
    data class Reflection(
        val id: Long,
        val date: LocalDate,
        val mood: Int, // 1–5
        val note: String?
    ) : TimelineItemUi()

    data class Interaction(
        val id: Long,
        val timestamp: LocalDateTime,
        val type: InteractionType,
        val moodEffect: MoodEffect
    ) : TimelineItemUi()
}

