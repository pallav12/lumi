package com.desktop.lumi.insights.models

import com.desktop.lumi.home.presentation.InteractionType
import com.desktop.lumi.home.presentation.MoodEffect
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime

sealed class TimelineItemUi {
    abstract val timestamp: Long

    data class ReflectionItem(
        val id: Long,
        val mood: Int,
        val note: String,
        override val timestamp: Long
    ) : TimelineItemUi()

    data class InteractionItem(
        val id: Long,
        val type: InteractionType,
        val moodEffect: MoodEffect,
        override val timestamp: Long
    ) : TimelineItemUi()
}

