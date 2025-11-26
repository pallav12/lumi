package com.desktop.lumi.instantmirror

import com.desktop.lumi.domain.model.Interaction
import com.desktop.lumi.home.presentation.InteractionType
import com.desktop.lumi.home.presentation.MoodEffect
import com.desktop.lumi.home.presentation.toInt
import com.desktop.lumi.home.presentation.toMoodEffectOrSame

/**
 * Result of pattern detection. Keep it small and testable.
 */
sealed class PatternMatch {
    data class InteractionPattern(
        val type: InteractionType,
        val positiveCount: Int,
        val negativeCount: Int,
        val total: Int,
        val lastTimestamps: List<Long>
    ) : PatternMatch()

    data class TimePattern(
        val windowDescription: String, // e.g. "late-night"
        val negativeCount: Int,
        val positiveCount: Int,
        val total: Int,
        val sampleTimestamps: List<Long>
    ) : PatternMatch()

    data class ContrastPattern(
        val interactionTypeA: InteractionType,
        val avgEffectA: Double,
        val interactionTypeB: InteractionType,
        val avgEffectB: Double
    ) : PatternMatch()

    object NoPattern : PatternMatch()
}

/**
 * Small, deterministic pattern matcher for instant mirror.
 * Keep logic simple and safe (no overclaiming).
 */
object PatternMatcher {

    private const val MIN_REQUIRED = 3 // require at least 3 samples for a "pattern"

    /**
     * Detects a simple interaction pattern: counts of Better/Worse for that interaction type.
     * Returns InteractionPattern if there is a clear positive or negative pattern.
     */
    fun detectInteractionPattern(recent: List<Interaction>): PatternMatch {
        if (recent.size < MIN_REQUIRED) return PatternMatch.NoPattern

        val typeCounts = recent.groupBy { InteractionType.valueOf(it.type) }
            .mapValues { entry ->
                val list = entry.value
                val positives = list.count { it.moodEffect.toMoodEffectOrSame() == MoodEffect.Better }
                val negatives = list.count { it.moodEffect.toMoodEffectOrSame() == MoodEffect.Worse }
                Triple(list.size, positives, negatives)
            }

        if (typeCounts.size == 1) {
            val (type, triple) = typeCounts.entries.first()
            val (total, positives, negatives) = Triple(triple.first, triple.second, triple.third)
            return when {
                negatives >= MIN_REQUIRED -> PatternMatch.InteractionPattern(
                    type = type,
                    positiveCount = positives,
                    negativeCount = negatives,
                    total = total,
                    lastTimestamps = recent.map { it.timestamp }
                )
                positives >= MIN_REQUIRED -> PatternMatch.InteractionPattern(
                    type = type,
                    positiveCount = positives,
                    negativeCount = negatives,
                    total = total,
                    lastTimestamps = recent.map { it.timestamp }
                )
                else -> PatternMatch.NoPattern
            }
        }

        // If multiple types exist, check if any single type has a strong pattern
        typeCounts.forEach { (type, triple) ->
            val (total, positives, negatives) = Triple(triple.first, triple.second, triple.third)
            if (total >= MIN_REQUIRED && (negatives >= MIN_REQUIRED || positives >= MIN_REQUIRED)) {
                // build list of timestamps for that type only
                val timestamps = recent.filter { InteractionType.valueOf(it.type) == type }.map { it.timestamp }
                return PatternMatch.InteractionPattern(
                    type = type,
                    positiveCount = positives,
                    negativeCount = negatives,
                    total = total,
                    lastTimestamps = timestamps
                )
            }
        }

        return PatternMatch.NoPattern
    }

    /**
     * Detects a simple time-of-day pattern: e.g., late-night interactions associate with negatives.
     * `lateNightStartHour` and `lateNightEndHour` in 24-hour clock (e.g. 23..4 wrap-around handled).
     */
    fun detectLateNightPattern(recent: List<Interaction>, lateNightStartHour: Int = 23, lateNightEndHour: Int = 4): PatternMatch {
        if (recent.size < MIN_REQUIRED) return PatternMatch.NoPattern

        // helper to convert timestamp to hour (UTC-based; adjust if you prefer local)
        fun hourOf(ts: Long): Int {
            val millisPerHour = 3_600_000L
            // Note: for production, use kotlinx-datetime to account for timezones.
            return ((ts / millisPerHour) % 24).toInt()
        }

        val lateNight = recent.filter {
            val h = hourOf(it.timestamp)
            if (lateNightStartHour <= lateNightEndHour) {
                h in lateNightStartHour..lateNightEndHour
            } else {
                // wrap-around (e.g., 23..4)
                h >= lateNightStartHour || h <= lateNightEndHour
            }
        }

        if (lateNight.size < MIN_REQUIRED) return PatternMatch.NoPattern

        val negatives = lateNight.count { it.moodEffect.toMoodEffectOrSame() == MoodEffect.Worse }
        val positives = lateNight.count { it.moodEffect.toMoodEffectOrSame() == MoodEffect.Better }

        return PatternMatch.TimePattern(
            windowDescription = "late-night",
            negativeCount = negatives,
            positiveCount = positives,
            total = lateNight.size,
            sampleTimestamps = lateNight.map { it.timestamp }
        )
    }

    /**
     * Contrast detection between two types (e.g., Calls vs Texts). Returns ContrastPattern if one
     * type has importantly better average effect than another.
     *
     * avgEffect mapping: Better = +1, Same = 0, Worse = -1 => average can be fractional
     */
    fun detectContrastPattern(recent: List<Interaction>, typeA: InteractionType, typeB: InteractionType): PatternMatch {
        fun avgForType(t: InteractionType): Double {
            val list = recent.filter { InteractionType.valueOf(it.type) == t }
            if (list.isEmpty()) return 0.0
            // Convert Int moodEffect to MoodEffect enum, then to Int for averaging
            return list.map { it.moodEffect.toMoodEffectOrSame().toInt() }.average()
        }

        val avgA = avgForType(typeA)
        val avgB = avgForType(typeB)

        // threshold for "contrast" - tweak later
        val threshold = 0.7
        return if (kotlin.math.abs(avgA - avgB) >= threshold) {
            PatternMatch.ContrastPattern(
                interactionTypeA = typeA,
                avgEffectA = avgA,
                interactionTypeB = typeB,
                avgEffectB = avgB
            )
        } else {
            PatternMatch.NoPattern
        }
    }
}
