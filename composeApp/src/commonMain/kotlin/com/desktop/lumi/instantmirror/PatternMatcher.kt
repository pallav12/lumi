package com.desktop.lumi.instantmirror

/**
 * Result of pattern detection. Keep it small and testable.
 */
import com.desktop.lumi.home.presentation.InteractionType

/**
 * The raw result of an analysis.
 * @param priority: Higher number = More urgent/useful to show.
 */
sealed class PatternMatch(val priority: Int) {

    // 1. The "Spiral" (High Urgency)
    // User logged 3+ entries in < 60 mins.
    data class SpiralDetected(
        val count: Int,
        val durationMinutes: Int
    ) : PatternMatch(priority = 100)

    // 2. Specific Interaction Trend (Medium Urgency)
    // "Texts make you anxious"
    data class TrendFound(
        val type: InteractionType,
        val trend: TrendDirection, // NEGATIVE, POSITIVE
        val count: Int
    ) : PatternMatch(priority = 80)

    // 3. Time Context (Medium Urgency)
    // "Late night makes you sad"
    data class TimeContextFound(
        val timeDescription: String, // "Late Night", "Morning"
        val trend: TrendDirection,
        val count: Int
    ) : PatternMatch(priority = 70)

    // 4. Contrast (High Value)
    // "Calls are better than Texts"
    data class ContrastFound(
        val betterType: InteractionType,
        val worseType: InteractionType
    ) : PatternMatch(priority = 90)

    object NoPattern : PatternMatch(priority = 0)
}

enum class TrendDirection { POSITIVE, NEGATIVE }
